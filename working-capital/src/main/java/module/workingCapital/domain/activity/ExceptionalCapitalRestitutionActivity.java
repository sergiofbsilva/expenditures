/*
 * @(#)ExceptionalCapitalRestitutionActivity.java
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
import module.workingCapital.domain.ExceptionalWorkingCapitalRefund;
import module.workingCapital.domain.WorkingCapital;
import module.workingCapital.domain.WorkingCapitalProcess;
import pt.ist.bennu.core.domain.RoleType;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.util.Money;
import pt.ist.bennu.core.i18n.BundleUtil;
import pt.ist.bennu.core.util.legacy.LegacyUtil;

/**
 * 
 * @author João Neves
 * 
 */
public class ExceptionalCapitalRestitutionActivity extends
        WorkflowActivity<WorkingCapitalProcess, ExceptionalCapitalRestitutionInfo> {

    @Override
    public String getLocalizedName() {
        return BundleUtil.getString(getUsedBundle(), "activity." + getClass().getSimpleName());
    }

    @Override
    public boolean isActive(final WorkingCapitalProcess missionProcess, final User user) {
        return LegacyUtil.hasRoleType(user, RoleType.MANAGER);
    }

    @Override
    protected void process(final ExceptionalCapitalRestitutionInfo activityInformation) {
        final WorkingCapital workingCapital = activityInformation.getProcess().getWorkingCapital();
        final Money value = activityInformation.getValue();
        final String description = activityInformation.getCaseDescription();
        new ExceptionalWorkingCapitalRefund(workingCapital, getLoggedPerson().getPerson(), value, description);
    }

    @Override
    public ActivityInformation<WorkingCapitalProcess> getActivityInformation(final WorkingCapitalProcess process) {
        return new ExceptionalCapitalRestitutionInfo(process, this);
    }

    @Override
    public String getUsedBundle() {
        return "resources/WorkingCapitalResources";
    }

    @Override
    public boolean isConfirmationNeeded(WorkingCapitalProcess process) {
        return true;
    }

    @Override
    public boolean isUserAwarenessNeeded(WorkingCapitalProcess process, User user) {
        return false;
    }

}
