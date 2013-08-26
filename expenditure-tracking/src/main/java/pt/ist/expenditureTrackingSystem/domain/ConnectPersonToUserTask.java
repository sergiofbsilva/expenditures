/*
 * @(#)ConnectPersonToUserTask.java
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
package pt.ist.expenditureTrackingSystem.domain;

import pt.ist.bennu.core.domain.MyOrg;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.i18n.BundleUtil;
import pt.ist.bennu.scheduler.CronTask;
import pt.ist.bennu.scheduler.annotation.Task;
import pt.ist.expenditureTrackingSystem.domain.organization.Person;

/**
 * 
 * @author Luis Cruz
 * @author Paulo Abrantes
 * 
 */
@Task(englishTitle = "Connect Person to User")
public class ConnectPersonToUserTask extends CronTask {

    public ConnectPersonToUserTask() {
        super();
    }

    @Override
    public void runTask() {
        for (Person person : MyOrg.getInstance().getPeopleFromExpenditureTackingSystemSet()) {
            if (!person.hasUser()) {
                String username = person.getUsername();
                User user = User.findByUsername(username);
                if (user == null) {
                    user = new User(username);
                }
                person.setUser(user);
            }
        }
    }

    @Override
    public String getLocalizedName() {
        return BundleUtil.getString("resources/ExpenditureResources", "label.task.connectPersonToUserTask");
    }

}
