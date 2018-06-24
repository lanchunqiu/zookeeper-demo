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
     * �����¼�����
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

        //�������ýڵ�
        String path = zooKeeper.create("/zk-test-presistent", //�ڵ�path
                "abc".getBytes(), //�ڵ�����
                ZooDefs.Ids.OPEN_ACL_UNSAFE, //ACL���ƣ���Ȩ�޿���
                CreateMode.PERSISTENT);//�ڵ�����
        System.out.println("Success create znode: " + path);

        byte[] bytes = zooKeeper.getData(path,null,new Stat());
        System.out.println("data:" + new String(bytes));



        bytes = zooKeeper.getData(path,true,new Stat());
        System.out.println("data:" + new String(bytes));

        Stat stat = zooKeeper.exists(path,true);
        int version = stat.getAversion();
        zooKeeper.delete(path,version);//ɾ���ڵ�

        zooKeeper.close();
    }
    public void process(WatchedEvent event) {
        System.out.println("watchedEvent = [" + event + "]");
        if(Event.KeeperState.SyncConnected == event.getState()){
            connectedSemaphore.countDown();
        }
    }
}
