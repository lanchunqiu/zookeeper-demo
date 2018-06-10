package com.lancq;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

/**
 * @Author lancq
 * @Description
 * @Date 2018/6/10
 **/
public class GetChildrenUsageDemo implements Watcher {
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    /**
     * ͬ����ʽ��ȡ�ӽڵ��б�
     */
    public void getChildreSync(){

    }

    /**
     * �첽��ʽ��ȡ�ӽڵ��б�
     */
    public void getChildreAsync(){

    }
    public void process(WatchedEvent event) {
        System.out.println("watchedEvent = [" + event + "]");
        if(Event.KeeperState.SyncConnected == event.getState()){
            connectedSemaphore.countDown();
        }
    }
}
