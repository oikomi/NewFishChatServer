package org.miaohong.newfishchatserver.core.rpc.registry;

public interface Destroyable {

    /**
     * 销毁接口
     */
    public void destroy();

    /**
     * Do destroy with hook.
     *
     * @param hook DestroyHook
     */
    public void destroy(DestroyHook hook);

    /**
     * 销毁钩子
     */
    interface DestroyHook {
        /**
         * 销毁前要做的事情
         */
        public void preDestroy();

        /**
         * 銷毀后要做的事情
         */
        public void postDestroy();
    }

}
