package org.miaohong.newfishchatserver.core.execption;

public class ServerCoreException extends AbstractCoreException {

    private static final long serialVersionUID = -1902979343618111565L;

    public ServerCoreException() {
        super();
    }

    public ServerCoreException(CoreErrorMsg coreErrorMsg) {
        super(coreErrorMsg);
    }

    public ServerCoreException(String message) {
        super(message);
    }

    public ServerCoreException(String message, CoreErrorMsg coreErrorMsg) {
        super(message, coreErrorMsg);
    }

    public ServerCoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerCoreException(String message, Throwable cause, CoreErrorMsg coreErrorMsg) {
        super(message, cause, coreErrorMsg);
    }

    public ServerCoreException(Throwable cause) {
        super(cause);
    }

    public ServerCoreException(Throwable cause, CoreErrorMsg coreErrorMsg) {
        super(cause, coreErrorMsg);
    }

}
