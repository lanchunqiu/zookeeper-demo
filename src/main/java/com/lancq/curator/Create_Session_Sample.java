package com.lancq.curator;

import com.lancq.ZKConfig;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * @Author lancq
 * @Description
 * @Date 2018/6/24
 **/
public class Create_Session_Sample {
    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);

        CuratorFramework client = CuratorFrameworkFactory
                .builder()
                .connectString(ZKConfig.CONNECTION_STR)
                .retryPolicy(retryPolicy)
                .sessionTimeoutMs(4000)
                .namespace("curator")
                .build();

        client.start();
        String s = client.getNamespace();
        System.out.println("s:"+s);
        client.create().withMode(CreateMode.EPHEMERAL).forPath("test");
        Thread.sleep(5000);
        client.close();

    }
}
