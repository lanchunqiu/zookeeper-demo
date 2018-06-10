package com.lancq;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @Author ZookeeperDemo
 * @Description ����һ��������Zookeeper�Ựʵ��
 * @Date 2018/6/6
 **/
public class CreateUsageDemo implements Watcher{
        private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

        /**
         * ��ͬ���ķ�ʽ�����ڵ�
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

            //������ʱ�ڵ�
            String path = zooKeeper.create("/zk-test-ephemeral-", //�ڵ�path
                    "abc".getBytes(), //�ڵ�����
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, //ACL���ƣ���Ȩ�޿���
                    CreateMode.EPHEMERAL);//�ڵ����ͣ���ʱ�ڵ�
            System.out.println("Success create znode: " + path);

            byte[] bytes = zooKeeper.getData(path,null,new Stat());
            System.out.println("data:" + new String(bytes));

            //������ʱ����ڵ�
            path = zooKeeper.create("/zk-test-ephemeral-", //�ڵ�path
                    "ABC".getBytes(), //�ڵ�����
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, //ACL���ƣ���Ȩ�޿���
                    CreateMode.EPHEMERAL_SEQUENTIAL);//�ڵ����ͣ���ʱ����ڵ�
            System.out.println("Success create znode: " + path);

            bytes = zooKeeper.getData(path,null,new Stat());
            System.out.println("data:" + new String(bytes));

            zooKeeper.close();
        }

        /**
         * ���첽�ķ�ʽ�����ڵ�
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

            //������ʱ�ڵ�
            zooKeeper.create("/zk-test-ephemeral-", //�ڵ�path
                    "abc".getBytes(), //�ڵ�����
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, //ACL���ƣ���Ȩ�޿���
                    CreateMode.EPHEMERAL, new IStringCallback(),"I am context.");//�ڵ����ͣ���ʱ�ڵ�

            zooKeeper.create("/zk-test-ephemeral-", //�ڵ�path
                    "abc".getBytes(), //�ڵ�����
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, //ACL���ƣ���Ȩ�޿���
                    CreateMode.EPHEMERAL, new IStringCallback(),"I am context.");//�ڵ����ͣ���ʱ�ڵ�


            //������ʱ����ڵ�
            zooKeeper.create("/zk-test-ephemeral-", //�ڵ�path
                    "ABC".getBytes(), //�ڵ�����
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, //ACL���ƣ���Ȩ�޿���
                    CreateMode.EPHEMERAL_SEQUENTIAL, new IStringCallback(),"I am context.");//�ڵ����ͣ���ʱ����ڵ�

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
