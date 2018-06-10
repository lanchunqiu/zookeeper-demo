package com.lancq;

import org.apache.zookeeper.AsyncCallback;

/**
 * @Author ZookeeperDemo
 * @Description
 * @Date 2018/6/6
 **/
public class IStringCallback implements AsyncCallback.StringCallback {
    /**
     * 创建节点后的回调方法
     * @param resultCode 服务端响应码：0接口调用成功，-4客户端与服务端连接已断开，-110指定节点已存在，-122会话已经过期
     * @param path 调用接口时传入的节点路径
     * @param ctx 上下文对象
     * @param name 服务端实际创建的节点名
     */
    public void processResult(int resultCode, String path, Object ctx, String name) {
        System.out.println("result = [" + resultCode + ", " + path + ", " + ctx + ", " + name + "]");
    }
}
