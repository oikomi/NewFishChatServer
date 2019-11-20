package org.miaohong.newfishchatserver.core.rpc.base;

public interface Destroyable {

    /**
     * 销毁接口
     */
    void destroy();

    /**
     * Do destroy with hook.
     *
     * @param hook DestroyHook
     */
    void destroy(DestroyHook hook);

    /**
     * 销毁钩子
     */
    interface DestroyHook {
        /**
         * 销毁前要做的事情
         */
        void preDestroy();

        /**
         * 銷毀后要做的事情
         */
        void postDestroy();
    }

}
