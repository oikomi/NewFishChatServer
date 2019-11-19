package org.miaohong.newfishchatserver.core.rpc.eventbus;

import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceRegistedListener {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceRegistedListener.class);

    @Subscribe
    public void doAction(final String event) {
        if (LOG.isInfoEnabled()) {
            LOG.info("Received event [{}] and will take a action", event);
        }
    }
}
