package org.miaohong.newfishchatserver.core.execption;

public class ClientCoreException extends AbstractCoreException {

    private static final long serialVersionUID = -3559448822544122913L;

    public ClientCoreException() {
        super();
    }

    public ClientCoreException(CoreErrorMsg coreErrorMsg) {
        super(coreErrorMsg);
    }

    public ClientCoreException(String message) {
        super(message);
    }

    public ClientCoreException(String message, CoreErrorMsg coreErrorMsg) {
        super(message, coreErrorMsg);
    }

    public ClientCoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientCoreException(String message, Throwable cause, CoreErrorMsg coreErrorMsg) {
        super(message, cause, coreErrorMsg);
    }

    public ClientCoreException(Throwable cause) {
        super(cause);
    }

    public ClientCoreException(Throwable cause, CoreErrorMsg coreErrorMsg) {
        super(cause, coreErrorMsg);
    }

}
