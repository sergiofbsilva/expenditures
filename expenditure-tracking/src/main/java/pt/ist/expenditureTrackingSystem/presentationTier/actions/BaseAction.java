/*
 * @(#)BaseAction.java
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
package pt.ist.expenditureTrackingSystem.presentationTier.actions;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForward;

import pt.ist.bennu.core.presentationTier.actions.ContextBaseAction;
import pt.ist.bennu.io.domain.GenericFile;
import pt.ist.expenditureTrackingSystem.domain.organization.Person;
import pt.ist.expenditureTrackingSystem.presentationTier.util.FileUploadBean;

/**
 * 
 * @author Shezad Anavarali
 * @author Paulo Abrantes
 * @author Luis Cruz
 * 
 */
public abstract class BaseAction extends ContextBaseAction {

    protected Person getLoggedPerson() {
        return Person.getLoggedPerson();
    }

    protected byte[] consumeInputStream(final FileUploadBean fileUploadBean) {
        final InputStream inputStream = fileUploadBean.getInputStream();
        return consumeInputStream(inputStream);
    }

    protected ActionForward download(final HttpServletResponse response, final GenericFile file) throws IOException {
        String filename = file.getFilename();
        if (filename == null) {
            filename = file.getDisplayName();
        }
        return file != null && file.getContent() != null ? download(response, filename != null ? filename : "", file.getStream(),
                file.getContentType()) : null;
    }

}
