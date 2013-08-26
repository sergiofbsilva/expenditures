/*
 * @(#)ListMissionsJune2011.java
 *
 * Copyright 2011 Instituto Superior Tecnico
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
package module.mission.domain.util;

import java.util.TreeSet;

import module.mission.domain.Mission;
import module.mission.domain.MissionFinancer;
import module.mission.domain.MissionProcess;
import module.mission.domain.MissionSystem;

import org.apache.commons.collections.ComparatorUtils;
import org.joda.time.DateTime;

import pt.ist.bennu.core.i18n.BundleUtil;
import pt.ist.bennu.scheduler.custom.CustomTask;

/**
 * 
 * @author João Neves
 * 
 */
public class ListMissionsJune2011 extends CustomTask {

    @Override
    public void runTask() {
        final DateTime limitDate = new DateTime(2011, 7, 1, 0, 0, 0, 0);
        taskLog("N Processo \t Data de Criacao \t"
                + "Aprovacao do Processo \t Cabimentacao \t Autorizacao das Deslocacoes \t Autorizacao da Despesa \t Processamento pela DRH \t Arquivado \t"
                + "Designacao \t Equiparacao Bolseiro? \t Projecto/Unidade Pagadora \t Valor \t Duracao (dias) \n");

        TreeSet<Mission> missions =
                new TreeSet<Mission>(ComparatorUtils.reversedComparator(Mission.COMPARATOR_BY_PROCESS_IDENTIFICATION));
        missions.addAll(MissionSystem.getInstance().getMissionsSet());
        for (final Mission mission : missions) {
            MissionProcess process = mission.getMissionProcess();
            if ((process.isCanceled()) || (process.getCreationDate().isAfter(limitDate))) {
                continue;
            }

            taskLog(process.getProcessNumber() + "\t");
            taskLog(process.getCreationDate().getYear() + "/" + process.getCreationDate().getMonthOfYear() + "/"
                    + process.getCreationDate().getDayOfMonth() + "\t");

            taskLog(MissionState.APPROVAL.getStateProgress(process).getLocalizedName());
            taskLog("\t");

            if (MissionState.FUND_ALLOCATION.isRequired(process)) {
                taskLog(MissionState.FUND_ALLOCATION.getStateProgress(process).getLocalizedName());
            } else {
                taskLog("N/A");
            }
            taskLog("\t");

            taskLog(MissionState.PARTICIPATION_AUTHORIZATION.getStateProgress(process).getLocalizedName());
            taskLog("\t");

            if (MissionState.EXPENSE_AUTHORIZATION.isRequired(process)) {
                taskLog(MissionState.EXPENSE_AUTHORIZATION.getStateProgress(process).getLocalizedName());
            } else {
                taskLog("N/A");
            }
            taskLog("\t");

            taskLog(MissionState.PERSONAL_INFORMATION_PROCESSING.getStateProgress(process).getLocalizedName());
            taskLog("\t");

            taskLog(MissionState.ARCHIVED.getStateProgress(process).getLocalizedName());
            taskLog("\t");

            taskLog(BundleUtil.getString("resources/MissionResources", "label." + mission.getClass().getName()) + "\t");
            taskLog((mission.getGrantOwnerEquivalence() ? "Sim" : "Nao") + "\t");

            if (!mission.hasAnyFinancer()) {
                taskLog("N/A");
            } else {
                boolean first = true;
                for (MissionFinancer financer : mission.getFinancerSet()) {
                    if (first) {
                        first = false;
                    } else {
                        taskLog(" & ");
                    }
                    taskLog(financer.getUnit().getPresentationName());
                }
            }
            taskLog("\t");

            taskLog(mission.getValue().getValue() + " Eur\t");
            taskLog(mission.getDurationInDays() + "\n");
        }
    }
}
