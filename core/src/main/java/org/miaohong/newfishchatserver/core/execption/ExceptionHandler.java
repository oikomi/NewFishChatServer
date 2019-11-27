package org.miaohong.newfishchatserver.core.execption;

public interface ExceptionHandler<T extends Exception> {
    <S extends Exception> T handle(S e);
}
