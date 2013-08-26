/*
 * @(#)SubmitForValidationActivity.java
 *
 * Copyright 2010 Instituto Superior Tecnico
 * Founding Authors: Luis Cruz
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Working Capital Module.
 *
 *   The Working Capital Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The Working Capital Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Working Capital Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package module.workingCapital.domain.activity;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import module.workflow.activities.ActivityInformation;
import module.workflow.activities.WorkflowActivity;
import module.workingCapital.domain.WorkingCapital;
import module.workingCapital.domain.WorkingCapitalAcquisitionSubmission;
import module.workingCapital.domain.WorkingCapitalAcquisitionSubmissionDocument;
import module.workingCapital.domain.WorkingCapitalAcquisitionTransaction;
import module.workingCapital.domain.WorkingCapitalProcess;
import module.workingCapital.domain.WorkingCapitalTransaction;
import module.workingCapital.domain.exception.WorkingCapitalDomainException;
import net.sf.jasperreports.engine.JRException;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.core.domain.util.Money;
import pt.ist.bennu.core.i18n.BundleUtil;
import pt.ist.bennu.core.util.ConfigurationManager;
import pt.ist.expenditureTrackingSystem.util.ReportUtils;

/**
 * 
 * @author João Neves
 * @author Luis Cruz
 * 
 */
public class SubmitForValidationActivity extends WorkflowActivity<WorkingCapitalProcess, SubmitForValidationActivityInformation> {

    @Override
    public String getLocalizedName() {
        return BundleUtil.getString("resources/WorkingCapitalResources", "activity." + getClass().getSimpleName());
    }

    @Override
    public boolean isActive(final WorkingCapitalProcess missionProcess, final User user) {
        WorkingCapital workingCapital = missionProcess.getWorkingCapital();
        return !workingCapital.isCanceledOrRejected() && workingCapital.isMovementResponsible(user)
                && workingCapital.hasApprovedAndUnSubmittedAcquisitions() && workingCapital.areAllAcquisitionsApproved();
    }

    @Override
    protected void process(final SubmitForValidationActivityInformation activityInformation) {
        final WorkingCapitalProcess workingCapitalProcess = activityInformation.getProcess();
        workingCapitalProcess.submitAcquisitionsForValidation();
        final WorkingCapital workingCapital = workingCapitalProcess.getWorkingCapital();

        final Money accumulatedValue = workingCapital.getLastTransaction().getAccumulatedValue();
        WorkingCapitalAcquisitionSubmission acquisitionSubmission =
                new WorkingCapitalAcquisitionSubmission(workingCapital, getLoggedPerson().getPerson(), accumulatedValue,
                        activityInformation.isPaymentRequired());
        WorkingCapitalTransaction previousTransaction = acquisitionSubmission.getPreviousTransaction();
        while (previousTransaction != null) {
            if (previousTransaction.isSubmission()) {
                break;
            }
            if ((previousTransaction.isAcquisition()) && previousTransaction.isApproved()) {
                acquisitionSubmission
                        .addWorkingCapitalAcquisitionTransactions((WorkingCapitalAcquisitionTransaction) previousTransaction);
            }
            previousTransaction = previousTransaction.getPreviousTransaction();
        }

        String txNumber = String.valueOf(acquisitionSubmission.getNumber());
        byte[] contents = createAcquisitionSubmissionDocument(acquisitionSubmission);
        WorkingCapitalAcquisitionSubmissionDocument document =
                new WorkingCapitalAcquisitionSubmissionDocument(acquisitionSubmission, contents, "SubmissionDocument" + txNumber
                        + ".pdf", activityInformation.getProcess());
        document.setFilename("Submission" + document.getOid() + document.getFilename());

        if (activityInformation.isLastSubmission()) {
            TerminateWorkingCapitalActivity terminateWorkingCapitalActivity = new TerminateWorkingCapitalActivity();
            terminateWorkingCapitalActivity.execute(activityInformation);
        }
    }

    private byte[] createAcquisitionSubmissionDocument(WorkingCapitalAcquisitionSubmission acquisitionSubmission) {
        final Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("workingCapital", acquisitionSubmission.getWorkingCapital());
        paramMap.put("responsibleName", acquisitionSubmission.getPerson().getName());
        paramMap.put("IBAN", acquisitionSubmission.getWorkingCapital().getWorkingCapitalInitialization()
                .getInternationalBankAccountNumber());
        paramMap.put("logoFilename", "Logo_" + VirtualHost.getVirtualHostForThread().getHostname() + ".png");

        paramMap.put("submissionTransactionNumber", acquisitionSubmission.getNumber());
        paramMap.put("submissionDescription", acquisitionSubmission.getDescription());
        paramMap.put("submissionValue", acquisitionSubmission.getValue());
        paramMap.put("submissionAccumulatedValue", acquisitionSubmission.getAccumulatedValue());
        paramMap.put("submissionBalance", acquisitionSubmission.getBalance());
        paramMap.put("submissionDebt", acquisitionSubmission.getDebt());
        paramMap.put("institutionSocialSecurityNumber",
                ConfigurationManager.getProperty(VirtualHost.getVirtualHostForThread().getHostname() + ".ssn"));
        paramMap.put("cae", ConfigurationManager.getProperty(VirtualHost.getVirtualHostForThread().getHostname() + ".cae"));

        paramMap.put("paymentRequired",
                BundleUtil.getString("resources/MyorgResources", acquisitionSubmission.getPaymentRequired().toString()));

        final ResourceBundle resourceBundle = ResourceBundle.getBundle("resources/WorkingCapitalResources");
        try {
            byte[] byteArray =
                    ReportUtils.exportToPdfFileAsByteArray("/reports/workingCapitalAcquisitionSubmissionDocument.jasper",
                            paramMap, resourceBundle, acquisitionSubmission.getWorkingCapitalAcquisitionTransactionsSorted());
            return byteArray;
        } catch (JRException e) {
            e.printStackTrace();
            throw new WorkingCapitalDomainException("resources/WorkingCapitalResources",
                    "workingCapitalAcquisitionSubmissionDocument.exception.failedCreation");
        }
    }

    @Override
    public String getUsedBundle() {
        return "resources/WorkingCapitalResources";
    }

    @Override
    protected String[] getArgumentsDescription(SubmitForValidationActivityInformation activityInformation) {
        if (activityInformation.isLastSubmission()) {
            return new String[] { "("
                    + BundleUtil.getString("resources/WorkingCapitalResources",
                            "label.module.workingCapital.initialization.lastSubmission") + ")" };
        }
        return new String[] { "" };
    }

    @Override
    public ActivityInformation<WorkingCapitalProcess> getActivityInformation(final WorkingCapitalProcess process) {
        return new SubmitForValidationActivityInformation(process, this);
    }

    @Override
    public boolean isUserAwarenessNeeded(final WorkingCapitalProcess process, final User user) {
        return false;
    }

    @Override
    public boolean isDefaultInputInterfaceUsed() {
        return false;
    }

}
