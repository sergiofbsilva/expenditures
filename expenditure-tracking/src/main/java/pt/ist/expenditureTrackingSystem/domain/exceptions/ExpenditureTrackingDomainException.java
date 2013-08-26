package pt.ist.expenditureTrackingSystem.domain.exceptions;

import javax.ws.rs.core.Response.Status;

import pt.ist.bennu.core.domain.exceptions.DomainException;

@SuppressWarnings("serial")
public class ExpenditureTrackingDomainException extends DomainException {

    public ExpenditureTrackingDomainException(String key) {
        super(null, key);
    }

    public ExpenditureTrackingDomainException(String bundle, String key, String... args) {
        super(bundle, key, args);
    }

    public ExpenditureTrackingDomainException(Status status, String bundle, String key, String... args) {
        super(status, bundle, key, args);
    }

    public ExpenditureTrackingDomainException(Throwable cause, String bundle, String key, String... args) {
        super(cause, bundle, key, args);
    }

    public ExpenditureTrackingDomainException(Throwable cause, String bundle, Status status, String key, String... args) {
        super(cause, status, bundle, key, args);
    }

}
