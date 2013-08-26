package module.mission.domain;

import java.util.ArrayList;
import java.util.List;

import jvstm.cps.ConsistencyPredicate;
import pt.ist.bennu.core.domain.MyOrg;
import pt.ist.bennu.core.i18n.BundleUtil;
import pt.ist.expenditureTrackingSystem.domain.exceptions.ExpenditureTrackingDomainException;
import pt.ist.fenixframework.dml.runtime.Relation;
import pt.ist.fenixframework.dml.runtime.RelationAdapter;

public class MissionProcessAssociation extends MissionProcessAssociation_Base {

    static {
        MissionProcess.getRelationMissionProcessAssociationMissionProcess().addListener(
                new RelationAdapter<MissionProcess, MissionProcessAssociation>() {
                    @Override
                    public void afterAdd(Relation<MissionProcess, MissionProcessAssociation> relation, MissionProcess process,
                            MissionProcessAssociation association) {
                        if (!association.checkHasNoRepeatedMissionSystems()) {
                            throw new ExpenditureTrackingDomainException(BundleUtil.getString("resources/MissionResources",
                                    "error.cannot.associate.MissionProcesses.from.same.system"));
                        }
                    }
                });

        MissionProcess.getRelationMissionProcessAssociationMissionProcess().addListener(
                new RelationAdapter<MissionProcess, MissionProcessAssociation>() {
                    @Override
                    public void afterAdd(Relation<MissionProcess, MissionProcessAssociation> relation, MissionProcess process,
                            MissionProcessAssociation association) {
                        if (!association.checkAllMissionProcessesOfSameType()) {
                            throw new ExpenditureTrackingDomainException(BundleUtil.getString("resources/MissionResources",
                                    "error.cannot.associate.MissionProcesses.of.diferent.types"));
                        }
                    }
                });
    }

    public MissionProcessAssociation() {
        super();
    }

    public MissionProcessAssociation(MissionProcess... missionProcesses) {
        this();
        setMyOrg(MyOrg.getInstance());
        for (MissionProcess process : missionProcesses) {
            addMissionProcesses(process);
        }
    }

    @ConsistencyPredicate
    public boolean checkHasAtLeastTwoMissionProcesses() {
        return getMissionProcessesSet().size() >= 2;
    }

    @ConsistencyPredicate
    public boolean checkHasMyOrg() {
        return hasMyOrg();
    }

    //@ConsistencyPredicate
    public boolean checkHasNoRepeatedMissionSystems() {
        List<MissionSystem> systemsFound = new ArrayList<MissionSystem>();
        for (MissionProcess process : getMissionProcesses()) {
            if (systemsFound.contains(process.getMissionSystem())) {
                return false;
            }
            systemsFound.add(process.getMissionSystem());
        }

        return true;
    }

    //@ConsistencyPredicate
    public boolean checkAllMissionProcessesOfSameType() {
        Boolean isGrantOwnerType = getMissionProcesses().iterator().next().getMission().getGrantOwnerEquivalence();
        for (MissionProcess process : getMissionProcesses()) {
            if (!process.getMission().getGrantOwnerEquivalence().equals(isGrantOwnerType)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isConnectedToCurrentHost() {
        return true;
    }

    public void delete() {
        for (MissionProcess process : getMissionProcesses()) {
            removeMissionProcesses(process);
        }
        setMyOrg(null);
        deleteDomainObject();
    }

    @Deprecated
    public java.util.Set<module.mission.domain.MissionProcess> getMissionProcesses() {
        return getMissionProcessesSet();
    }

    @Deprecated
    public boolean hasAnyMissionProcesses() {
        return !getMissionProcessesSet().isEmpty();
    }

    @Deprecated
    public boolean hasMyOrg() {
        return getMyOrg() != null;
    }

}
