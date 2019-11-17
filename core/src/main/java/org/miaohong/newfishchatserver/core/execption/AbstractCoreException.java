package org.miaohong.newfishchatserver.core.execption;

public abstract class AbstractCoreException extends RuntimeException {

    private static final long serialVersionUID = -5362997510190915252L;

    protected CoreErrorMsg coreErrorMsg = null;
    protected String errorMsg = null;

    public AbstractCoreException() {
        super();
    }

    public AbstractCoreException(CoreErrorMsg coreErrorMsg) {
        super();
        this.coreErrorMsg = coreErrorMsg;
    }

    public AbstractCoreException(String message) {
        super(message);
        this.errorMsg = message;
    }

    public AbstractCoreException(String message, CoreErrorMsg coreErrorMsg) {
        super(message);
        this.coreErrorMsg = coreErrorMsg;
        this.errorMsg = message;
    }

    public AbstractCoreException(String message, Throwable cause) {
        super(message, cause);
        this.errorMsg = message;
    }

    public AbstractCoreException(String message, Throwable cause, CoreErrorMsg coreErrorMsg) {
        super(message, cause);
        this.coreErrorMsg = coreErrorMsg;
        this.errorMsg = message;
    }

    public AbstractCoreException(Throwable cause) {
        super(cause);
    }

    public AbstractCoreException(Throwable cause, CoreErrorMsg coreErrorMsg) {
        super(cause);
        this.coreErrorMsg = coreErrorMsg;
    }

    @Override
    public String getMessage() {
        String message = getOriginMessage();

        return "error_message: " + message + ", status: " + coreErrorMsg.getStatus() + ", error_code: " + coreErrorMsg.getErrorCode();
    }

    public String getOriginMessage() {
        if (coreErrorMsg == null) {
            return super.getMessage();
        }

        String message;

        if (errorMsg != null && !"".equals(errorMsg)) {
            message = errorMsg;
        } else {
            message = coreErrorMsg.getMessage();
        }
        return message;
    }

    public int getStatus() {
        return coreErrorMsg != null ? coreErrorMsg.getStatus() : 0;
    }

    public int getErrorCode() {
        return coreErrorMsg != null ? coreErrorMsg.getErrorCode() : 0;
    }

    public CoreErrorMsg getCoreErrorMsg() {
        return coreErrorMsg;
    }

}
