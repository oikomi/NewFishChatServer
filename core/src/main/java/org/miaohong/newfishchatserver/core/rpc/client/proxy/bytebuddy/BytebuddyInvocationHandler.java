/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.miaohong.newfishchatserver.core.rpc.client.proxy.bytebuddy;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import org.miaohong.newfishchatserver.core.proto.RpcRequest;
import org.miaohong.newfishchatserver.core.rpc.client.ConnectManager;
import org.miaohong.newfishchatserver.core.rpc.client.RPCFuture;
import org.miaohong.newfishchatserver.core.rpc.client.RpcClientHandler;
import org.miaohong.newfishchatserver.core.rpc.client.proxy.AbstractInvocationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class BytebuddyInvocationHandler extends AbstractInvocationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(BytebuddyInvocationHandler.class);

    @RuntimeType
    public Object byteBuddyInvoke(@This Object proxy, @Origin Method method, @AllArguments @RuntimeType Object[] args)
            throws Throwable {

        if (isLocalMethod(method)) {
            String name = method.getName();
            Class[] paramTypes = method.getParameterTypes();
            if ("equals".equals(name)) {
                return proxy == args[0];
            } else if ("hashCode".equals(name)) {
                return System.identityHashCode(proxy);
            } else if ("toString".equals(name)) {
                return proxy.getClass().getName() + "@" +
                        Integer.toHexString(System.identityHashCode(proxy)) +
                        ", with InvocationHandler " + this;
            } else {
                throw new IllegalStateException(String.valueOf(method));
            }
        }

        RpcRequest request = buildRequest(method, args);
        LOG.debug(method.getDeclaringClass().getName());
        LOG.debug(method.getName());

        for (int i = 0; i < method.getParameterTypes().length; ++i) {
            LOG.debug(method.getParameterTypes()[i].getName());
        }
        if (args != null) {
            for (int i = 0; i < args.length; ++i) {
                LOG.debug(args[i].toString());
            }
        }

        RpcClientHandler handler = ConnectManager.getInstance().chooseHandler();
        RPCFuture rpcFuture = handler.sendRequest(request);
        return rpcFuture.get();
    }
}