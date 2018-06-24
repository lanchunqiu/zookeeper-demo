package com.lancq;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @Author lancq
 * @Description �ֲ�ʽ��
 * @Date 2018/6/10
 **/
public class DistributedLock implements Lock,Watcher {
    private ZooKeeper zk;
    private String ROOT_LOCK = "/looks";
    private String WAIT_LOCK ;
    private String CURRENT_LOCK;

    private CountDownLatch countDownLatch;

    public DistributedLock() {
        try {
            zk = new ZooKeeper("192.168.227.129:2181,192.168.227.130:2181,192.168.227.131:2181",5000,new DistributedLock());
            Stat stat = zk.exists(ROOT_LOCK, false);

            if(stat == null){
                zk.create(ROOT_LOCK,"0".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void lock() {
        if(this.tryLock()){//���������ɹ�
            System.out.println(Thread.currentThread().getName()+"->"+CURRENT_LOCK+"->������ɹ�");
            return;
        }
        try {
            waitForLock(WAIT_LOCK); //û�л�����������ȴ������
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void waitForLock(String prev) throws KeeperException, InterruptedException {
        Stat stat = zk.exists(prev,true);
        if(stat != null){
            System.out.println(Thread.currentThread().getName()+"->�ȴ���"+ROOT_LOCK+"/"+prev+"�ͷ�");
            countDownLatch = new CountDownLatch(1);
            countDownLatch.await();

            //TODO  watcher�����Ժ󣬻���Ҫ�ٴ��жϵ�ǰ�ȴ��Ľڵ��ǲ�����С��
            System.out.println(Thread.currentThread().getName()+"->������ɹ�");

        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        try {
            CURRENT_LOCK = zk.create(ROOT_LOCK + "/", "0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

            System.out.println(Thread.currentThread().getName()+"->"+ CURRENT_LOCK+"�����Ծ�����");

            List<String> children = zk.getChildren(ROOT_LOCK, false);//��ȡ���ڵ��µ������ӽڵ�

            SortedSet<String> sortedSet = new TreeSet<String>();//����һ�����Ͻ�������

            for(String ch : children){
                sortedSet.add(ROOT_LOCK + "/" + ch);
            }
            String firstNode = sortedSet.first();//��õ�ǰ�����ӽڵ�����С�Ľڵ�

            SortedSet<String> lessThanMe = sortedSet.headSet(CURRENT_LOCK);

            if(CURRENT_LOCK.equals(firstNode)){//ͨ����ǰ�Ľڵ���ӽڵ�����С�Ľڵ���бȽϣ������ȣ���ʾ������ɹ�
                return true;
            }

            if(!lessThanMe.isEmpty()){
                WAIT_LOCK = lessThanMe.last();//��ñȵ�ǰ�ڵ��С�����һ���ڵ㣬���ø�WAIT_LOCK
            }

        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        System.out.println(Thread.currentThread().getName()+"->�ͷ���"+CURRENT_LOCK);
        try {
            zk.delete(CURRENT_LOCK,-1);
            CURRENT_LOCK = null;
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Condition newCondition() {
        return null;
    }

    @Override
    public void process(WatchedEvent event) {
        if(this.countDownLatch!=null){
            this.countDownLatch.countDown();
        }

    }
}
