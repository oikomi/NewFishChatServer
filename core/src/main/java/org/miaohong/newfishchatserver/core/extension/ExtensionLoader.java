package org.miaohong.newfishchatserver.core.extension;


import org.miaohong.newfishchatserver.annotations.Spi;
import org.miaohong.newfishchatserver.annotations.SpiMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ExtensionLoader<T> {

    private static final Logger LOG = LoggerFactory.getLogger(ExtensionLoader.class);

    private static ConcurrentMap<Class<?>, ExtensionLoader<?>> extensionLoaders = new ConcurrentHashMap<Class<?>, ExtensionLoader<?>>();

    private ConcurrentMap<String, T> extensionClasses = new ConcurrentHashMap<>();

    private Class<T> type;
    private volatile boolean init = false;

    // spi path prefix
    private ClassLoader classLoader;

    private ExtensionLoader(Class<T> type) {
        this(type, Thread.currentThread().getContextClassLoader());
    }

    private ExtensionLoader(Class<T> type, ClassLoader classLoader) {
        this.type = type;
        this.classLoader = classLoader;
    }

    private static <T> void checkInterfaceType(Class<T> clz) {
        if (clz == null) {
            failThrows(clz, "Error extension type is null");
        }

        if (!clz.isInterface()) {
            failThrows(clz, "Error extension type is not interface");
        }

//        if (!isSpiType(clz)) {
//            failThrows(clz, "Error extension type without @Spi annotation");
//        }
    }

    @SuppressWarnings("unchecked")
    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        checkInterfaceType(type);
        ExtensionLoader<T> loader = (ExtensionLoader<T>) extensionLoaders.get(type);
        if (loader == null) {
            loader = initExtensionLoader(type);
        }
        return loader;
    }

    @SuppressWarnings("unchecked")
    public static synchronized <T> ExtensionLoader<T> initExtensionLoader(Class<T> type) {
        ExtensionLoader<T> loader = (ExtensionLoader<T>) extensionLoaders.get(type);

        if (loader == null) {
            loader = new ExtensionLoader<T>(type);

            extensionLoaders.put(type, loader);
        }

        return loader;
    }

    private static <T> boolean isSpiType(Class<T> clz) {
        return clz.isAnnotationPresent(Spi.class);
    }

    private static <T> void failLog(Class<T> type, String msg, Throwable cause) {
        LOG.error(type.getName() + ": " + msg, cause);
    }

    private static <T> void failThrows(Class<T> type, String msg, Throwable cause) {
        throw new RuntimeException(type.getName() + ": " + msg, cause);
    }

    private static <T> void failThrows(Class<T> type, String msg) {
        throw new RuntimeException(type.getName() + ": " + msg);
    }

    private static <T> void failThrows(Class<T> type, URL url, int line, String msg) throws ServiceConfigurationError {
        failThrows(type, url + ":" + line + ": " + msg);
    }

    public T getExtension(Class<T> clz, String name) throws IllegalAccessException, InstantiationException {
        if (extensionClasses.containsKey(name)) {
            return extensionClasses.get(name);
        } else {
            ServiceLoader<T> ss = ServiceLoader.load(clz);
            for (T s : ss) {
                String extensionName = s.getClass().getAnnotation(SpiMeta.class).name();
                extensionClasses.putIfAbsent(extensionName, s);
            }
        }

        return extensionClasses.get(name);
    }

}
