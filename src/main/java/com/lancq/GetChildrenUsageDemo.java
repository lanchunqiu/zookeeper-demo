package com.lancq;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Author lancq
 * @Description
 * @Date 2018/6/10
 **/
public class GetChildrenUsageDemo implements Watcher {
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zk ;

    /**
     * 同步方式读取子节点列表
     */
    @Test
    public void getChildreSync() throws IOException, InterruptedException, KeeperException {
        zk = new ZooKeeper(ZKConfig.CONNECTION_STR,5000,this);
        connectedSemaphore.await();

        zk.create("/child1", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        List<String> childrenList = zk.getChildren("/", true);
        System.out.println(childrenList);

        zk.create("/child2", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        Thread.sleep(1000);
    }

    /**
     * 异步方式读取子节点列表
     */
    @Test
    public void getChildreAsync() throws IOException, InterruptedException, KeeperException {
        zk = new ZooKeeper(ZKConfig.CONNECTION_STR,5000,this);
        connectedSemaphore.await();

        zk.create("/child1", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        zk.create("/child2", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        zk.getChildren("/", true, new IChildren2Callback(),null);//异步获取子节点

        Thread.sleep(1000);
    }


    public void process(WatchedEvent event) {
        System.out.println("watchedEvent = [" + event + "]");
        if(Event.KeeperState.SyncConnected == event.getState()){
            if(Event.EventType.None == event.getType() && null == event.getPath()){
                connectedSemaphore.countDown();
            } else if(Event.EventType.NodeChildrenChanged == event.getType()){
                try {
                    System.out.println("GetChildren:" + zk.getChildren(event.getPath(),true));
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    class IChildren2Callback implements AsyncCallback.Children2Callback{

        @Override
        public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
            System.out.println("rc = [" + rc + "], path = [" + path + "], ctx = [" + ctx + "], children = " + children + ", stat = [" + stat + "]");
        }
    }
}
