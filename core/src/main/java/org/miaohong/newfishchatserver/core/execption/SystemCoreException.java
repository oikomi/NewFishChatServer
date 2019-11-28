package org.miaohong.newfishchatserver.core.execption;

public class SystemCoreException extends AbstractCoreException {

    private static final long serialVersionUID = -5744571877339125775L;

    public SystemCoreException() {
        super();
    }

    public SystemCoreException(CoreErrorMsg coreErrorMsg) {
        super(coreErrorMsg);
    }

    public SystemCoreException(String message) {
        super(message);
    }

    public SystemCoreException(String message, CoreErrorMsg coreErrorMsg) {
        super(message, coreErrorMsg);
    }

    public SystemCoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public SystemCoreException(String message, Throwable cause, CoreErrorMsg coreErrorMsg) {
        super(message, cause, coreErrorMsg);
    }

    public SystemCoreException(Throwable cause) {
        super(cause);
    }

    public SystemCoreException(Throwable cause, CoreErrorMsg coreErrorMsg) {
        super(cause, coreErrorMsg);
    }

}
