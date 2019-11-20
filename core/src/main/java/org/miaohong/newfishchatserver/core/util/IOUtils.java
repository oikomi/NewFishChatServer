package org.miaohong.newfishchatserver.core.util;

import java.io.Closeable;
import java.net.ServerSocket;
import java.net.Socket;

public class IOUtils {

    private IOUtils() {
        throw new AssertionError();
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignore) {
                // NOPMD
            }
        }
    }

    public static void closeQuietly(ServerSocket closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignore) {
                // NOPMD
            }
        }
    }

    public static void closeQuietly(Socket closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignore) {
                // NOPMD
            }
        }
    }

}
