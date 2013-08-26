/*
 * @(#)ApproveActivity.java
 *
 * Copyright 2010 Instituto Superior Tecnico
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
package module.mission.domain.activity;

import module.mission.domain.MissionProcess;
import module.workflow.activities.ActivityInformation;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.i18n.BundleUtil;
import pt.ist.bennu.core.security.Authenticate;

/**
 * 
 * @author Luis Cruz
 * 
 */
public class ApproveActivity extends MissionProcessActivity<MissionProcess, LateJustificationActivityInformation> {

    @Override
    public String getLocalizedName() {
        return BundleUtil.getString("resources/MissionResources", "activity." + getClass().getSimpleName());
    }

    @Override
    public boolean isActive(final MissionProcess missionProcess, final User user) {
        return super.isActive(missionProcess, user) && !missionProcess.isUnderConstruction() && !missionProcess.getIsCanceled()
                && missionProcess.isPendingApprovalBy(user);
    }

    @Override
    protected void process(final LateJustificationActivityInformation activityInformation) {
        final MissionProcess missionProcess = activityInformation.getProcess();
        final User user = Authenticate.getUser();
        missionProcess.approve(user);
        if (!missionProcess.isOnTime()) {
            missionProcess.justifyLateSubmission(activityInformation.getJustification());
        }
        missionProcess.addToVerificationQueue();
    }

    @Override
    public ActivityInformation<MissionProcess> getActivityInformation(MissionProcess process) {
        return new LateJustificationActivityInformation(process, this);
    }

    @Override
    public boolean isConfirmationNeeded(final MissionProcess process) {
        return true;
    }

    @Override
    public String getLocalizedConfirmationMessage() {
        return BundleUtil.getString("resources/MissionResources",
                "label.module.mission.approve.confirm.service.is.assured");
    }

}
