package org.miaohong.newfishchatserver.core.util;

import java.util.Collection;
import java.util.Map;

public final class CommonUtils {

    private CommonUtils() {
    }

    public static boolean isEmpty(Collection collection) {
        return !isNotEmpty(collection);
    }

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
