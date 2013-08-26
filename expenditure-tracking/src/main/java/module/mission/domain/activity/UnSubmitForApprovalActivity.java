package module.mission.domain.activity;

import module.mission.domain.MissionProcess;
import module.workflow.activities.ActivityInformation;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.i18n.BundleUtil;

public class UnSubmitForApprovalActivity extends MissionProcessActivity<MissionProcess, ActivityInformation<MissionProcess>> {

    @Override
    public String getLocalizedName() {
        return BundleUtil.getString("resources/MissionResources", "activity." + getClass().getSimpleName());
    }

    @Override
    public boolean isActive(final MissionProcess missionProcess, final User user) {
        return super.isActive(missionProcess, user) && !missionProcess.isUnderConstruction()
                && !missionProcess.isProcessCanceled() && missionProcess.isRequestor(user) && !missionProcess.hasAnyAproval()
                && !missionProcess.isApproved();
    }

    @Override
    protected void process(final ActivityInformation activityInformation) {
        final MissionProcess missionProcess = (MissionProcess) activityInformation.getProcess();
        missionProcess.setIsUnderConstruction(Boolean.TRUE);
    }

}
