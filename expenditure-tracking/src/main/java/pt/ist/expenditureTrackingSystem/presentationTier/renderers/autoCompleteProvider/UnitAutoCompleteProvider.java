/*
 * @(#)UnitAutoCompleteProvider.java
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
package pt.ist.expenditureTrackingSystem.presentationTier.renderers.autoCompleteProvider;

import pt.ist.bennu.core.presentationTier.renderers.autoCompleteProvider.AutoCompleteProvider;
import pt.ist.expenditureTrackingSystem.domain.ExpenditureTrackingSystem;
import pt.ist.expenditureTrackingSystem.domain.organization.CostCenter;
import pt.ist.expenditureTrackingSystem.domain.organization.Project;
import pt.ist.expenditureTrackingSystem.domain.organization.SubProject;
import pt.ist.expenditureTrackingSystem.domain.organization.Unit;
import pt.utl.ist.fenix.tools.util.StringNormalizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author Luis Cruz
 * @author Susana Fernandes
 * 
 */
public class UnitAutoCompleteProvider implements AutoCompleteProvider<Unit> {

    @Override
    public Collection<Unit> getSearchResults(Map<String, String> argsMap, String value, int maxCount) {
        final List<Unit> units = new ArrayList<Unit>();

        final String trimmedValue = value.trim();

        for (final Unit unit : ExpenditureTrackingSystem.getInstance().getUnits()) {
            if (unit instanceof CostCenter) {
                final CostCenter costCenter = (CostCenter) unit;
                final String unitCode = costCenter.getCostCenter();
                if (!StringUtils.isEmpty(unitCode) && trimmedValue.equalsIgnoreCase(unitCode)) {
                    units.add(unit);
                }
            } else if (unit instanceof Project) {
                final Project project = (Project) unit;
                final String unitCode = project.getProjectCode();
                if (!StringUtils.isEmpty(unitCode) && trimmedValue.equalsIgnoreCase(unitCode)) {
                    if (unit.hasAnySubUnits()) {
                        addAllSubUnits(units, unit);
                    } else {
                        units.add(unit);
                    }
                }
            }
        }

        final String[] input = trimmedValue.split(" ");
        StringNormalizer.normalize(input);

        for (final Unit unit : ExpenditureTrackingSystem.getInstance().getUnits()) {
            if (unit instanceof CostCenter || unit instanceof Project || unit instanceof SubProject) {
                final String unitName = StringNormalizer.normalize(unit.getName());
                if (hasMatch(input, unitName)) {
                    units.add(unit);
                }
            }
        }

        Collections.sort(units, Unit.COMPARATOR_BY_PRESENTATION_NAME);

        return units;
    }

    private void addAllSubUnits(final List<Unit> units, final Unit unit) {
        for (final Unit subUnit : unit.getSubUnitsSet()) {
            units.add(subUnit);
            addAllSubUnits(units, subUnit);
        }
    }

    private boolean hasMatch(final String[] input, final String unitNameParts) {
        for (final String namePart : input) {
            if (unitNameParts.indexOf(namePart) == -1) {
                return false;
            }
        }
        return true;
    }

    private boolean isNumeric(String someString) {
        boolean isNumeric = StringUtils.isNumeric(someString);
        if (isNumeric) {
            try {
                int i = Integer.parseInt(someString);
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        }
        return false;
    }
}
