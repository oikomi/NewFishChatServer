package org.miaohong.newfishchatserver.core.execption;

import java.io.Serializable;

public class CoreErrorMsg implements Serializable {

    private static final long serialVersionUID = 5688688468614074520L;

    private int status;
    private int errorcode;
    private String message;

    public CoreErrorMsg(int status, int errorcode, String message) {
        this.status = status;
        this.errorcode = errorcode;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public int getErrorCode() {
        return errorcode;
    }

    public String getMessage() {
        return message;
    }


}
