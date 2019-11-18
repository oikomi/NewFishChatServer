package org.miaohong.newfishchatserver.client.client;

import org.miaohong.newfishchatserver.core.rpc.client.ConnectionManager;
import org.miaohong.newfishchatserver.core.rpc.client.ConsumerConfig;
import org.miaohong.newfishchatserver.core.rpc.client.RpcClient;
import org.miaohong.newfishchatserver.proto.gateway.GatewayProto;
import org.miaohong.newfishchatserver.proto.gateway.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class Client {

    private static final Logger LOG = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) {

        ConsumerConfig<GatewayProto> consumerConfig = new ConsumerConfig<>();
        LOG.info(GatewayProto.class.getName());
        LOG.info(GatewayProto.class.getCanonicalName());
        consumerConfig.setInterfaceId(GatewayProto.class.getName());
        RpcClient<GatewayProto> rpcClient = new RpcClient<>("127.0.0.1:15000", consumerConfig);

        ConnectionManager.getINSTANCE().updateConnectedServer(Collections.singletonList("127.0.0.1:15000"));
        GatewayProto s = rpcClient.refer();
        Person person = s.person();
        System.out.println(person);
        LOG.info("result is {}", person);
    }
}
