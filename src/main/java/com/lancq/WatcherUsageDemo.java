package com.lancq;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @Author lancq
 * @Description
 * @Date 2018/6/10
 **/
public class WatcherUsageDemo implements Watcher {
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    /**
     * 监听事件测试
     * @throws IOException
     * @throws KeeperException
     * @throws InterruptedException
     */
    @Test
    public void watchEventTest() throws IOException, KeeperException, InterruptedException {
        ZooKeeper zooKeeper = new ZooKeeper(ZKConfig.CONNECTION_STR,5000,new WatcherUsageDemo());

        System.out.println(zooKeeper.getState());
        try {
            connectedSemaphore.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Zookeeper session established.");

        //创建永久节点
        String path = zooKeeper.create("/zk-test-presistent", //节点path
                "abc".getBytes(), //节点数据
                ZooDefs.Ids.OPEN_ACL_UNSAFE, //ACL控制：无权限控制
                CreateMode.PERSISTENT);//节点类型
        System.out.println("Success create znode: " + path);

        byte[] bytes = zooKeeper.getData(path,null,new Stat());
        System.out.println("data:" + new String(bytes));



        bytes = zooKeeper.getData(path,true,new Stat());
        System.out.println("data:" + new String(bytes));

        Stat stat = zooKeeper.exists(path,true);
        int version = stat.getAversion();
        zooKeeper.delete(path,version);//删除节点

        zooKeeper.close();
    }
    public void process(WatchedEvent event) {
        System.out.println("watchedEvent = [" + event + "]");
        if(Event.KeeperState.SyncConnected == event.getState()){
            connectedSemaphore.countDown();
        }
    }
}
