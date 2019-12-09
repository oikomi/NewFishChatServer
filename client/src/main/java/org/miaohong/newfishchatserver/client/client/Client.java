package org.miaohong.newfishchatserver.client.client;

import org.miaohong.newfishchatserver.core.rpc.client.ConsumerConfig;
import org.miaohong.newfishchatserver.core.rpc.client.RpcClient;
import org.miaohong.newfishchatserver.proto.gateway.GatewayProto;
import org.miaohong.newfishchatserver.proto.gateway.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {

    private static final Logger LOG = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) {

        ConsumerConfig<GatewayProto> consumerConfig = new ConsumerConfig<>();
        LOG.info(GatewayProto.class.getName());
        LOG.info(GatewayProto.class.getCanonicalName());
        consumerConfig.setInterfaceId(GatewayProto.class.getName());
        RpcClient<GatewayProto> rpcClient = new RpcClient<>(consumerConfig);
        rpcClient.start();

        for (int i = 0; i < 1; i++) {
            LOG.info("i = {}", i);
            GatewayProto s = rpcClient.refer();
            Person person = s.person();
            System.out.println(person);
            LOG.info("result is {}", person);

            person = s.person();
            System.out.println(person);
            LOG.info("result is {}", person);
        }
    }
}
