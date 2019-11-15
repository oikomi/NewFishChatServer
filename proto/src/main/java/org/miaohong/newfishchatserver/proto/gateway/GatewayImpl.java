package org.miaohong.newfishchatserver.proto.gateway;

public class GatewayImpl implements GatewayProto {

    @Override
    public String test() {
        return "test";
    }

    @Override
    public Person person() {
        Person person = new Person("zh", 11);
        return person;
    }
}
