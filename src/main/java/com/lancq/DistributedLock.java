package com.lancq;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @Author lancq
 * @Description ·Ö²¼Ê½Ëø
 * @Date 2018/6/10
 **/
public class DistributedLock implements Lock,Watcher {
    private ZooKeeper zk;
    private String ROOT_LOCK = "/looks";
    private String WAIT_LOCK ;
    private String CURRENT_LOCK;

    private static CountDownLatch connectedSemaphore;

    public DistributedLock() {
        try {
            zk = new ZooKeeper("192.168.227.129:2181,192.168.227.130:2181,192.168.227.131:2181/zk-book",5000,new DistributedLock());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void lock() {

    }

    public void lockInterruptibly() throws InterruptedException {

    }

    public boolean tryLock() {
        return false;
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    public void unlock() {

    }

    public Condition newCondition() {
        return null;
    }

    public void process(WatchedEvent event) {

    }
}
