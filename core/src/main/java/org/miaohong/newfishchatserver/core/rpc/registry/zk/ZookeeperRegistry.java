package org.miaohong.newfishchatserver.core.rpc.registry.zk;

import com.google.common.base.Strings;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.miaohong.newfishchatserver.core.rpc.registry.Register;
import org.miaohong.newfishchatserver.core.rpc.registry.RegistryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ZookeeperRegistry extends Register {

    private static final Logger LOG = LoggerFactory.getLogger(ZookeeperRegistry.class);
    private static final String CONTEXT_SEP = "/";
    private CuratorFramework zkClient;
    private String rootPath;

    public ZookeeperRegistry() {
        super(RegistryConfig.getINSTANCE());
    }

//    private List<AuthInfo> buildAuthInfo() {
//        List<AuthInfo> info = new ArrayList<>();
//
//        String scheme = registryConfig.getParameter("scheme");
//
//        //如果存在多个认证信息，则在参数形式为为addAuth=user1:paasswd1,user2:passwd2
//        String addAuth = registryConfig.getParameter("addAuth");
//
//        if (!Strings.isNullOrEmpty(addAuth)) {
//            String[] addAuths = addAuth.split(",");
//            for (String singleAuthInfo : addAuths) {
//                info.add(new AuthInfo(scheme, singleAuthInfo.getBytes()));
//            }
//        }
//
//        return info;
//    }

    public static void main(String[] args) {
        new ZookeeperRegistry().start();
    }

    private ACLProvider getDefaultAclProvider() {
        return new ACLProvider() {
            @Override
            public List<ACL> getDefaultAcl() {
                return ZooDefs.Ids.CREATOR_ALL_ACL;
            }

            @Override
            public List<ACL> getAclForPath(String path) {
                return ZooDefs.Ids.CREATOR_ALL_ACL;
            }
        };
    }

    private synchronized void init() {
        if (zkClient != null) {
            return;
        }
        String addressInput = registryConfig.getAddress();
        if (Strings.isNullOrEmpty(addressInput)) {
            throw new RuntimeException("Address of zookeeper registry is empty.");
        }
        int idx = addressInput.indexOf(CONTEXT_SEP);
        String address;
        if (idx > 0) {
            address = addressInput.substring(0, idx);
            rootPath = addressInput.substring(idx);
            if (!rootPath.endsWith(CONTEXT_SEP)) {
                rootPath += CONTEXT_SEP;
            }
        } else {
            address = addressInput;
            rootPath = CONTEXT_SEP;
        }
//        preferLocalFile = !CommonUtils.isFalse(registryConfig.getParameter(PARAM_PREFER_LOCAL_FILE));
//        ephemeralNode = !CommonUtils.isFalse(registryConfig.getParameter(PARAM_CREATE_EPHEMERAL));
//        if (LOG.isInfoEnabled()) {
//            LOG.info(
//                    "Init ZookeeperRegistry with address {}, root path is {}. preferLocalFile:{}, ephemeralNode:{}",
//                    address, rootPath, preferLocalFile, ephemeralNode);
//        }
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFrameworkFactory.Builder zkClientuilder = CuratorFrameworkFactory.builder()
                .connectString(address)
                .sessionTimeoutMs(registryConfig.getConnectTimeout())
                .connectionTimeoutMs(registryConfig.getTimeout())
                .canBeReadOnly(false)
                .retryPolicy(retryPolicy)
                .defaultData(null);

        //是否需要添加zk的认证信息
//        List<AuthInfo> authInfos = buildAuthInfo();
//        if (CommonUtils.isNotEmpty(authInfos)) {
//            zkClientuilder = zkClientuilder.aclProvider(getDefaultAclProvider())
//                    .authorization(authInfos);
//        }

        zkClient = zkClientuilder.build();

        zkClient.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {

                if (LOG.isInfoEnabled()) {
                    LOG.info("reconnect to zookeeper,recover provider and consumer data");
                }
                if (newState == ConnectionState.RECONNECTED) {
//                    recoverRegistryData();
                }
            }
        });
    }

    @Override
    public boolean start() {
        init();
        if (zkClient == null) {
            LOG.warn("Start zookeeper registry must be do init first!");
            return false;
        }
        if (zkClient.getState() == CuratorFrameworkState.STARTED) {
            return true;
        }
        try {
            zkClient.start();
        } catch (Exception e) {
            throw new RuntimeException("Failed to start zookeeper zkClient", e);
        }
        return zkClient.getState() == CuratorFrameworkState.STARTED;
    }

}
