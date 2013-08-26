package module.mission.domain.util;

import module.mission.domain.MissionProcess;
import pt.ist.bennu.core.i18n.BundleUtil;
import pt.ist.fenixWebFramework.rendererExtensions.util.IPresentableEnum;

public enum MissionState implements IPresentableEnum {

    APPROVAL {
        @Override
        public MissionStateProgress getStateProgress(MissionProcess missionProcess) {
            if (missionProcess.isApprovedByResponsible()) {
                return MissionStateProgress.COMPLETED;
            }
            if (missionProcess.isUnderConstruction() || missionProcess.isCanceled()) {
                return MissionStateProgress.IDLE;
            }
            return MissionStateProgress.PENDING;
        }
    },

    VERIFICATION {
        @Override
        public MissionStateProgress getStateProgress(MissionProcess missionProcess) {
            if (!APPROVAL.isCompleted(missionProcess)) {
                return MissionStateProgress.IDLE;
            }

            if (missionProcess.isVerified()) {
                return MissionStateProgress.COMPLETED;
            }

            if (missionProcess.isCanceled()) {
                return MissionStateProgress.IDLE;
            }
            return MissionStateProgress.PENDING;
        }
    },

    VEHICLE_AUTHORIZATION {
        @Override
        public boolean isRequired(MissionProcess missionProcess) {
            return missionProcess.hasAnyVehicleItems();
        }

        @Override
        public MissionStateProgress getStateProgress(MissionProcess missionProcess) {
            if (!VERIFICATION.isCompleted(missionProcess)) {
                return MissionStateProgress.IDLE;
            }

            if (!isRequired(missionProcess)) {
                return MissionStateProgress.COMPLETED;
            }
            if (missionProcess.getMission().areAllVehicleItemsAuthorized()) {
                return MissionStateProgress.COMPLETED;
            }

            if (missionProcess.isCanceled()) {
                return MissionStateProgress.IDLE;
            }
            return MissionStateProgress.PENDING;
        }
    },

    FUND_ALLOCATION {
        @Override
        public boolean isRequired(MissionProcess missionProcess) {
            return missionProcess.hasAnyMissionItems();
        }

        @Override
        public MissionStateProgress getStateProgress(MissionProcess missionProcess) {
            if (missionProcess.isCanceled()) {
                if (!missionProcess.hasAnyAllocatedFunds() && !missionProcess.hasAnyAllocatedProjectFunds()) {
                    return MissionStateProgress.IDLE;
                }
                return MissionStateProgress.PENDING;
            }

            if (!VEHICLE_AUTHORIZATION.isCompleted(missionProcess)) {
                return MissionStateProgress.IDLE;
            }

            if (!isRequired(missionProcess)) {
                return MissionStateProgress.COMPLETED;
            }

            if (missionProcess.hasAllAllocatedFunds() && missionProcess.hasAllCommitmentNumbers()
                    && (!missionProcess.hasAnyProjectFinancer() || missionProcess.hasAllAllocatedProjectFunds())) {
                return MissionStateProgress.COMPLETED;
            }

            return MissionStateProgress.PENDING;
        }
    },

    PARTICIPATION_AUTHORIZATION {
        @Override
        public MissionStateProgress getStateProgress(MissionProcess missionProcess) {
            if (!FUND_ALLOCATION.isCompleted(missionProcess)) {
                return MissionStateProgress.IDLE;
            }

            if (missionProcess.areAllParticipantsAuthorized()) {
                return MissionStateProgress.COMPLETED;
            }

            if (missionProcess.isCanceled()) {
                return MissionStateProgress.IDLE;
            }
            return MissionStateProgress.PENDING;
        }
    },

    EXPENSE_AUTHORIZATION {
        @Override
        public boolean isRequired(MissionProcess missionProcess) {
            return missionProcess.hasAnyMissionItems();
        }

        @Override
        public MissionStateProgress getStateProgress(MissionProcess missionProcess) {
            if (!PARTICIPATION_AUTHORIZATION.isCompleted(missionProcess)) {
                return MissionStateProgress.IDLE;
            }

            if (!isRequired(missionProcess)) {
                return MissionStateProgress.COMPLETED;
            }
            if (missionProcess.isAuthorized()) {
                return MissionStateProgress.COMPLETED;
            }

            if (missionProcess.isCanceled()) {
                return MissionStateProgress.IDLE;
            }
            return MissionStateProgress.PENDING;
        }
    },

    PERSONAL_INFORMATION_PROCESSING {
        @Override
        public boolean isRequired(MissionProcess missionProcess) {
            return missionProcess.isPersonalInformationProcessingNeeded();
        }

        @Override
        public MissionStateProgress getStateProgress(MissionProcess missionProcess) {
            if (!EXPENSE_AUTHORIZATION.isCompleted(missionProcess)) {
                return MissionStateProgress.IDLE;
            }

            if (!isRequired(missionProcess)) {
                return MissionStateProgress.COMPLETED;
            }
            if (missionProcess.isPersonalInformationProcessed()) {
                return MissionStateProgress.COMPLETED;
            }

            return MissionStateProgress.PENDING;
        }
    },

    ARCHIVED {
        @Override
        public MissionStateProgress getStateProgress(MissionProcess missionProcess) {
            if (missionProcess.isArchived()) {
                return MissionStateProgress.COMPLETED;
            }

            if (!missionProcess.isTerminated()) {
                return MissionStateProgress.IDLE;
            }
            return MissionStateProgress.PENDING;
        }
    };

    private static final String BUNDLE = "resources.MissionResources";
    private static final String KEY_PREFIX = "label.MissionState.";
    private static final String KEY_PREFIX_DESCRIPTION = "label.MissionState.description.";

    @Override
    public String getLocalizedName() {
        final String key = KEY_PREFIX + name();
        return BundleUtil.getString(BUNDLE, key);
    }

    public String getLocalizedDescription() {
        final String key = KEY_PREFIX_DESCRIPTION + name();
        return BundleUtil.getString(BUNDLE, key);
    }

    public boolean isRequired(MissionProcess missionProcess) {
        return true;
    }

    public boolean isCompleted(MissionProcess missionProcess) {
        return getStateProgress(missionProcess) == MissionStateProgress.COMPLETED;
    }

    public boolean isPending(MissionProcess missionProcess) {
        return getStateProgress(missionProcess) == MissionStateProgress.PENDING;
    }

    public boolean isIdle(MissionProcess missionProcess) {
        return getStateProgress(missionProcess) == MissionStateProgress.IDLE;
    }

    public abstract MissionStateProgress getStateProgress(MissionProcess missionProcess);
}
