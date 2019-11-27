package org.miaohong.newfishchatserver.core.execption;

public class UncheckedCheckedException extends RuntimeException {
    private static final long serialVersionUID = -4308438819320877625L;

    public UncheckedCheckedException(Throwable t) {
        super(t);
    }
}
