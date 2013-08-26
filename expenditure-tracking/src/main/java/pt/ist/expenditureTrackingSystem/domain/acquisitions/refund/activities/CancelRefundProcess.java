/*
 * @(#)CancelRefundProcess.java
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
package pt.ist.expenditureTrackingSystem.domain.acquisitions.refund.activities;

import module.workflow.activities.ActivityInformation;
import module.workflow.activities.WorkflowActivity;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.i18n.BundleUtil;
import pt.ist.expenditureTrackingSystem.domain.acquisitions.refund.RefundProcess;
import pt.ist.expenditureTrackingSystem.domain.organization.Person;

/**
 * 
 * @author Paulo Abrantes
 * @author Luis Cruz
 * 
 */
public class CancelRefundProcess extends WorkflowActivity<RefundProcess, ActivityInformation<RefundProcess>> {

    @Override
    public boolean isActive(RefundProcess process, User user) {
        final Person executor = user.getExpenditurePerson();

        return isUserProcessOwner(process, user)
                && (((process.isInGenesis() || process.isInAuthorizedState()) && process.getRequestor() == executor)
                        || (process.isPendingApproval() && process.isResponsibleForAtLeastOnePayingUnit(executor))
                        || ((process.isPendingInvoicesConfirmation() || process.isPendingFundAllocation()) && ((process
                                .isAccountingEmployee(executor) && !process.hasProjectsAsPayingUnits()) || (process
                                .isProjectAccountingEmployee(executor) && process.hasProjectsAsPayingUnits()))) || (process
                        .isResponsibleForUnit(executor, process.getRequest().getTotalValue())
                        && !process.getRequest().hasBeenAuthorizedBy(executor) && process.isInAllocatedToUnitState()));
    }

    @Override
    protected void process(ActivityInformation<RefundProcess> activityInformation) {
        activityInformation.getProcess().cancel();
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
    public boolean isConfirmationNeeded(RefundProcess process) {
        return true;
    }

    @Override
    public boolean isUserAwarenessNeeded(RefundProcess process, User user) {
        return false;
    }

}
