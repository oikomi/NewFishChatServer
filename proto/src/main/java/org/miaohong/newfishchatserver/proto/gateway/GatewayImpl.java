package org.miaohong.newfishchatserver.proto.gateway;

public class GatewayImpl implements GatewayProto {

    @Override
    public String test() {
        System.out.println("test");

        return "test";
    }
}
