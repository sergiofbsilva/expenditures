package module.mission.domain;

import pt.ist.bennu.core.domain.MyOrg;
import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.scheduler.CronTask;
import pt.ist.bennu.scheduler.annotation.Task;

@Task(englishTitle = "Clear Temporary Mission Items")
public class ClearTemporaryMissionItems extends CronTask {

    public ClearTemporaryMissionItems() {
        super();
    }

    @Override
    public void runTask() {
        for (VirtualHost vHost : MyOrg.getInstance().getVirtualHosts()) {
            try {
                VirtualHost.setVirtualHostForThread(vHost);
                for (final TemporaryMissionItemEntry temporaryMissionItemEntry : MissionSystem.getInstance()
                        .getTemporaryMissionItemEntriesSet()) {
                    temporaryMissionItemEntry.gc();
                }
            } finally {
                VirtualHost.releaseVirtualHostFromThread();
            }
        }
    }

    @Override
    public String getLocalizedName() {
        return getClass().getName();
    }

}
