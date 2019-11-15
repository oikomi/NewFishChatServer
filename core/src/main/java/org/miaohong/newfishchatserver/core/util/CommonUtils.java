package org.miaohong.newfishchatserver.core.util;

import java.util.Collection;
import java.util.Map;

public class CommonUtils {

    public static boolean isNotEmpty(Collection collection) {
        return collection != null && !collection.isEmpty();
    }

    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map map) {
        return map != null && !map.isEmpty();
    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isNotEmpty(Object[] array) {
        return array != null && array.length > 0;
    }

}
