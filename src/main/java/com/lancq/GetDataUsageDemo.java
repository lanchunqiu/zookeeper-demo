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
 * @Date 2018/6/24
 **/
public class GetDataUsageDemo implements Watcher {
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zk ;
    private static Stat stat = new Stat();

    /**
     * 同步方式读取节点数据
     */
    @Test
    public void getDataSync() throws IOException, InterruptedException, KeeperException {
        zk = new ZooKeeper(ZKConfig.CONNECTION_STR,5000,this);
        connectedSemaphore.await();

        zk.create("/child", "123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        byte[] bytes = zk.getData("/child", true, stat);
        System.out.println(new String(bytes));
        System.out.println(stat.getCzxid() + "," + stat.getMzxid() + "," + stat.getVersion());

        zk.setData("/child", "abc".getBytes(), -1);


        Thread.sleep(1000);
    }

    /**
     * 异步方式读取节点数据
     */
    @Test
    public void getDataAsync() throws IOException, InterruptedException, KeeperException {
        zk = new ZooKeeper(ZKConfig.CONNECTION_STR,5000,this);
        connectedSemaphore.await();

        zk.create("/child", "123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        zk.getData("/child", true, new IDataCallback(), null);

        zk.setData("/child", "abc".getBytes(), -1);


        Thread.sleep(1000);

    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println("watchedEvent = [" + event + "]");
        if(Event.KeeperState.SyncConnected == event.getState()){
            if(Event.EventType.None == event.getType() && null == event.getPath()){
                connectedSemaphore.countDown();
            } else if(Event.EventType.NodeDataChanged == event.getType()){
                try {
                    System.out.println("GetData:" + new String(zk.getData(event.getPath(),true, stat)));
                    System.out.println(stat.getCzxid() + "," + stat.getMzxid() + "," + stat.getVersion());
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    class IDataCallback implements AsyncCallback.DataCallback{

        @Override
        public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
            System.out.println(rc + "," + path + "," + new String(data));
            System.out.println(stat.getCzxid() + "," + stat.getMzxid() + "," + stat.getVersion());
        }
    }
}
