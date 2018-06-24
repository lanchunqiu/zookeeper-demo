package com.lancq;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @Author lancq
 * @Description
 * @Date 2018/6/24
 **/
public class ExistsUsageDemo implements Watcher {
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zk ;
    private Stat stat;

    /**
     * 同步检查节点是否存在
     */
    @Test
    public void existsSync() throws IOException, InterruptedException, KeeperException {
        zk = new ZooKeeper(ZKConfig.CONNECTION_STR, 5000, this);
        connectedSemaphore.await();
        stat = zk.exists("/child", true);
        System.out.println(stat);
        if(null == stat) {
            zk.create("/child", "123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

            zk.setData("/child", "abc".getBytes(), -1);

            zk.create("/child/cl", "123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

            zk.delete("/child/cl", -1);

            zk.delete("/child", -1);
        }

        Thread.sleep(1000);
    }
    /**
     * 异步检查节点是否存在
     */
    public void existsAsync(){

    }
    @Override
    public void process(WatchedEvent event) {
        try{
            if(Event.KeeperState.SyncConnected == event.getState()){
                if(Event.EventType.None == event.getType() && null == event.getPath()){
                    connectedSemaphore.countDown();

                } else if(Event.EventType.NodeCreated == event.getType()){
                    System.out.println("Node(" + event.getPath() + ")Created");
                    zk.exists(event.getPath(), true);
                } else if(Event.EventType.NodeDeleted == event.getType()){
                    System.out.println("Node(" + event.getPath() + ")Deleted");
                    zk.exists(event.getPath(), true);
                } else if(Event.EventType.NodeDataChanged == event.getType()){
                    System.out.println("Node(" + event.getPath() + ")DataChanged");
                    zk.exists(event.getPath(), true);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
