package pt.ist.expenditureTrackingSystem.presentationTier.actions;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.expenditureTrackingSystem.domain.processes.AbstractActivity;
import pt.ist.expenditureTrackingSystem.domain.processes.ActivityException;
import pt.ist.expenditureTrackingSystem.domain.processes.GenericFile;
import pt.ist.expenditureTrackingSystem.domain.processes.GenericProcess;

public abstract class ProcessAction extends BaseAction {

    protected abstract <T extends GenericProcess> T getProcess(final HttpServletRequest request);

    protected GenericProcess getProcess(final HttpServletRequest request, final String attributeName) {
	final GenericProcess genericProcess = getDomainObject(request, attributeName);
	return genericProcess;
    }

    protected void genericActivityExecution(final GenericProcess genericProcess, final String activityName) {
	AbstractActivity<GenericProcess> acquitivity = genericProcess.getActivityByName(activityName);
	try {
	    acquitivity.execute(genericProcess);
	} catch (ActivityException e) {
	    addMessage(e.getMessage(), "EXPENDITURE_RESOURCES", e.getActivityName());
	}

    }

    protected void genericActivityExecution(final GenericProcess genericProcess, final String activityName, Object... args) {
	AbstractActivity<GenericProcess> acquitivity = genericProcess.getActivityByName(activityName);
	try {
	    acquitivity.execute(genericProcess, args);
	} catch (ActivityException e) {
	    addMessage(e.getMessage(), "EXPENDITURE_RESOURCES", e.getActivityName());
	}
    }

    protected void genericActivityExecution(final HttpServletRequest request, final String activityName) {
	final GenericProcess genericProcess = getProcess(request);
	genericActivityExecution(genericProcess, activityName);
    }

    protected void genericActivityExecution(final HttpServletRequest request, final String activityName, final Object... args) {
	final GenericProcess genericProcess = getProcess(request);
	genericActivityExecution(genericProcess, activityName, args);
    }

    protected String getBundle() {
	return StringUtils.EMPTY;
    }
    
    public ActionForward downloadGenericFile(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws IOException {
	
	GenericFile file = getDomainObject(request, "fileOID");
	return download(response, file);
    }
}
