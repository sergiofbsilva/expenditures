package module.workingCapital.domain.exception;

import javax.ws.rs.core.Response.Status;

import pt.ist.bennu.core.domain.exceptions.DomainException;

public class WorkingCapitalDomainException extends DomainException {

    public WorkingCapitalDomainException(String key) {
        super(null, key);
    }

    public WorkingCapitalDomainException(String bundle, String key, String... args) {
        super(bundle, key, args);
    }

    public WorkingCapitalDomainException(Throwable cause, Status status, String bundle, String key, String... args) {
        super(cause, status, bundle, key, args);
    }

    public WorkingCapitalDomainException(Throwable cause, String bundle, String key, String... args) {
        super(cause, bundle, key, args);
    }

}
