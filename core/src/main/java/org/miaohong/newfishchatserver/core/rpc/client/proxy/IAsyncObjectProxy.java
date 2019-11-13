package org.miaohong.newfishchatserver.core.rpc.client.proxy;


import org.miaohong.newfishchatserver.core.rpc.client.RPCFuture;

public interface IAsyncObjectProxy {
    RPCFuture call(String funcName, Object... args);
}