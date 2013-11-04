package module.mission.presentationTier.provider;

import pt.ist.bennu.core.presentationTier.renderers.autoCompleteProvider.AutoCompleteProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import module.mission.domain.MissionProcess;
import module.mission.domain.MissionSystem;

import org.apache.commons.lang.StringUtils;

public class MissionProcessProvider implements AutoCompleteProvider<MissionProcess> {

    @Override
    public Collection<MissionProcess> getSearchResults(Map<String, String> argsMap, String value, int maxCount) {
        final String currentValue = StringUtils.trim(value);

        final List<MissionProcess> result = new ArrayList<MissionProcess>();
        final MissionSystem missionSystem = MissionSystem.getInstance();
        for (final MissionProcess missionProcess : missionSystem.getMissionProcessesSet()) {
            String[] processIdParts = missionProcess.getProcessNumber().split("/M");
            if (missionProcess.getProcessIdentification().equals(currentValue)
                    || processIdParts[processIdParts.length - 1].equals(currentValue)) {
                result.add(missionProcess);
            }
        }

        return result;
    }

}
