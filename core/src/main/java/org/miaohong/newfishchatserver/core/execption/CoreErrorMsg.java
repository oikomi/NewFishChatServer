package org.miaohong.newfishchatserver.core.execption;

import lombok.Getter;

import java.io.Serializable;

public class CoreErrorMsg implements Serializable {

    private static final long serialVersionUID = 5954565648909663307L;

    @Getter
    private int status;

    @Getter
    private int errorCode;

    @Getter
    private String message;

    public CoreErrorMsg(int status, int errorCode, String message) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
    }
}
