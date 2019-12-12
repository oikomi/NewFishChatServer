package org.miaohong.newfishchatserver.client.client;

import org.miaohong.newfishchatserver.core.rpc.client.ConsumerConfig;
import org.miaohong.newfishchatserver.core.rpc.client.RpcClient;
import org.miaohong.newfishchatserver.proto.gateway.GatewayProto;
import org.miaohong.newfishchatserver.proto.gateway.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {

    private static final Logger LOG = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws InterruptedException {

        ConsumerConfig<GatewayProto> consumerConfig = new ConsumerConfig<>();
        LOG.info(GatewayProto.class.getName());
        LOG.info(GatewayProto.class.getCanonicalName());
        consumerConfig.setInterfaceId(GatewayProto.class.getName());
        RpcClient<GatewayProto> rpcClient = new RpcClient<>(consumerConfig);

        GatewayProto s = rpcClient.refer();

        for (int i = 0; i < 10; i++) {
            LOG.info("i = {}", i);
            try {
                Person person = s.person();
                System.out.println(person);
                LOG.info("result is {}", person);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }
}
