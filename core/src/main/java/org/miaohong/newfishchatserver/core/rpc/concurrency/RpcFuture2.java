package org.miaohong.newfishchatserver.core.rpc.concurrency;


import io.netty.util.concurrent.Future;

/**
 * custom future extends netty future
 *
 * @param <V>
 */
public interface RpcFuture2<V> extends Future<V> {


}
