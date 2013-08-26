/*
 * @(#)RemovePermanentProjectFunds.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: Luis Cruz, Nuno Ochoa, Paulo Abrantes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Expenditure Tracking Module.
 *
 *   The Expenditure Tracking Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The Expenditure Tracking Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Expenditure Tracking Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package pt.ist.expenditureTrackingSystem.domain.acquisitions.activities.commons;

import module.workflow.activities.ActivityInformation;
import module.workflow.activities.WorkflowActivity;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.i18n.BundleUtil;
import pt.ist.expenditureTrackingSystem.domain.acquisitions.PaymentProcess;
import pt.ist.expenditureTrackingSystem.domain.acquisitions.simplified.SimplifiedProcedureProcess;
import pt.ist.expenditureTrackingSystem.domain.organization.Person;

/**
 * 
 * @author Paulo Abrantes
 * 
 */
public class RemovePermanentProjectFunds<P extends PaymentProcess> extends WorkflowActivity<P, ActivityInformation<P>> {

    @Override
    public boolean isActive(P process, User user) {
        return process.isProjectAccountingEmployee(user.getExpenditurePerson()) && isUserProcessOwner(process, user)
                && isInAPossibleState(process) && process.hasAllocatedFundsPermanentlyForAllProjectFinancers();
    }

    private boolean isInAPossibleState(P process) {
        return process.isInvoiceConfirmed()
                || (process instanceof SimplifiedProcedureProcess && !process.getRequest().getInvoices().isEmpty() && ((SimplifiedProcedureProcess) process)
                        .getAcquisitionProcessState().isAcquisitionProcessed());
    }

    @Override
    protected void process(ActivityInformation<P> activityInformation) {
        activityInformation.getProcess().getRequest().resetPermanentProjectFundAllocationId(Person.getLoggedPerson());
    }

    @Override
    public String getLocalizedName() {
        return BundleUtil.getString(getUsedBundle(), "label." + getClass().getName());
    }

    @Override
    public String getUsedBundle() {
        return "resources/AcquisitionResources";
    }

    @Override
    public boolean isUserAwarenessNeeded(final P process, final User user) {
        return process.isCanceled();
    }

}
