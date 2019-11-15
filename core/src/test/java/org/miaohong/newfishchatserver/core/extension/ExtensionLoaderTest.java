package org.miaohong.newfishchatserver.core.extension;

import org.junit.Test;
import org.miaohong.newfishchatserver.core.proto.serialize.Serialization;

public class ExtensionLoaderTest {

    @Test
    public void testGetExtension() throws InstantiationException, IllegalAccessException {
        Serialization serialization = ExtensionLoader.getExtensionLoader(Serialization.class).getExtension(Serialization.class, "protobuf");
        System.out.println(serialization);
    }
}
