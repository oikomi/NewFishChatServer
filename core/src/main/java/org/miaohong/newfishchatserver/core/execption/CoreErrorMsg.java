package org.miaohong.newfishchatserver.core.execption;

import lombok.Data;

import java.io.Serializable;

@Data
public class CoreErrorMsg implements Serializable {

    private static final long serialVersionUID = 5954565648909663307L;

    private int status;
    private int errorCode;
    private String message;

    public CoreErrorMsg(int status, int errorCode, String message) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
    }
}
