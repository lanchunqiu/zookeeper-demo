package com.lancq;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @Author ZookeeperDemo
 * @Description 创建一个基本的Zookeeper会话实例
 * @Date 2018/6/6
 **/
public class CreateUsageDemo implements Watcher{
        private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

        /**
         * 以同步的方式创建节点
         * @throws IOException
         * @throws KeeperException
         * @throws InterruptedException
         */
        @Test
        public void CreateSyncUsage() throws IOException, KeeperException, InterruptedException {
            ZooKeeper zooKeeper = new ZooKeeper("192.168.227.129:2181,192.168.227.130:2181,192.168.227.131:2181/zk-book",5000,new CreateUsageDemo());

            System.out.println(zooKeeper.getState());
            try {
                connectedSemaphore.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Zookeeper session established.");

            //创建临时节点
            String path = zooKeeper.create("/zk-test-ephemeral-", //节点path
                    "abc".getBytes(), //节点数据
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, //ACL控制：无权限控制
                    CreateMode.EPHEMERAL);//节点类型：临时节点
            System.out.println("Success create znode: " + path);

            byte[] bytes = zooKeeper.getData(path,null,new Stat());
            System.out.println("data:" + new String(bytes));

            //创建临时有序节点
            path = zooKeeper.create("/zk-test-ephemeral-", //节点path
                    "ABC".getBytes(), //节点数据
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, //ACL控制：无权限控制
                    CreateMode.EPHEMERAL_SEQUENTIAL);//节点类型：临时有序节点
            System.out.println("Success create znode: " + path);

            bytes = zooKeeper.getData(path,null,new Stat());
            System.out.println("data:" + new String(bytes));

            zooKeeper.close();
        }

        /**
         * 以异步的方式创建节点
         * @throws KeeperException
         * @throws InterruptedException
         * @throws IOException
         */
        @Test
        public void CreateAsyncUsage() throws KeeperException, InterruptedException, IOException {
            ZooKeeper zooKeeper = new ZooKeeper("192.168.227.129:2181,192.168.227.130:2181,192.168.227.131:2181/zk-book",5000,new CreateUsageDemo());

            System.out.println(zooKeeper.getState());
            try {
                connectedSemaphore.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Zookeeper session established.");

            //创建临时节点
            zooKeeper.create("/zk-test-ephemeral-", //节点path
                    "abc".getBytes(), //节点数据
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, //ACL控制：无权限控制
                    CreateMode.EPHEMERAL, new IStringCallback(),"I am context.");//节点类型：临时节点

            zooKeeper.create("/zk-test-ephemeral-", //节点path
                    "abc".getBytes(), //节点数据
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, //ACL控制：无权限控制
                    CreateMode.EPHEMERAL, new IStringCallback(),"I am context.");//节点类型：临时节点


            //创建临时有序节点
            zooKeeper.create("/zk-test-ephemeral-", //节点path
                    "ABC".getBytes(), //节点数据
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, //ACL控制：无权限控制
                    CreateMode.EPHEMERAL_SEQUENTIAL, new IStringCallback(),"I am context.");//节点类型：临时有序节点

            Thread.currentThread().sleep(10000);

            zooKeeper.close();
        }

        public void process(WatchedEvent watchedEvent) {
            System.out.println("watchedEvent = [" + watchedEvent + "]");
            if(Event.KeeperState.SyncConnected == watchedEvent.getState()){
                connectedSemaphore.countDown();
            }
        }
}
