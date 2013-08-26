/*
 * @(#)CorrectWorkingCapitalAcquisitionClassificationActivity.java
 *
 * Copyright 2011 Instituto Superior Tecnico
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

import module.workflow.activities.ActivityInformation;
import module.workflow.activities.WorkflowActivity;
import module.workingCapital.domain.WorkingCapital;
import module.workingCapital.domain.WorkingCapitalAcquisition;
import module.workingCapital.domain.WorkingCapitalAcquisitionTransaction;
import module.workingCapital.domain.WorkingCapitalProcess;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.i18n.BundleUtil;

/**
 * 
 * @author João Neves
 * 
 */
public class CorrectWorkingCapitalAcquisitionClassificationActivity extends
        WorkflowActivity<WorkingCapitalProcess, EditWorkingCapitalActivityInformation> {

    @Override
    public String getLocalizedName() {
        return BundleUtil.getString("resources/WorkingCapitalResources", "activity."
                + getClass().getSimpleName());
    }

    @Override
    public boolean isActive(final WorkingCapitalProcess workingCapitalProcess, final User user) {
        final WorkingCapital workingCapital = workingCapitalProcess.getWorkingCapital();
        return !workingCapital.isCanceledOrRejected() && workingCapital.hasAcquisitionPendingVerification(user);
    }

    @Override
    protected void process(final EditWorkingCapitalActivityInformation activityInformation) {
        final WorkingCapitalAcquisitionTransaction workingCapitalAcquisitionTransaction =
                activityInformation.getWorkingCapitalAcquisitionTransaction();
        if (workingCapitalAcquisitionTransaction.isPendingVerificationByUser()) {
            final WorkingCapitalAcquisition workingCapitalAcquisition =
                    workingCapitalAcquisitionTransaction.getWorkingCapitalAcquisition();

            workingCapitalAcquisition.edit(activityInformation.getDocumentNumber(), activityInformation.getSupplier(),
                    activityInformation.getDescription(), activityInformation.getAcquisitionClassification(),
                    activityInformation.getValueWithoutVat());
        }
    }

    @Override
    public ActivityInformation<WorkingCapitalProcess> getActivityInformation(final WorkingCapitalProcess process) {
        return new EditWorkingCapitalActivityInformation(process, this);
    }

    @Override
    public boolean isVisible() {
        return false;
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
