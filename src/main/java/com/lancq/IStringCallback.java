package com.lancq;

import org.apache.zookeeper.AsyncCallback;

/**
 * @Author ZookeeperDemo
 * @Description
 * @Date 2018/6/6
 **/
public class IStringCallback implements AsyncCallback.StringCallback {
    /**
     * �����ڵ��Ļص�����
     * @param resultCode �������Ӧ�룺0�ӿڵ��óɹ���-4�ͻ��������������ѶϿ���-110ָ���ڵ��Ѵ��ڣ�-122�Ự�Ѿ�����
     * @param path ���ýӿ�ʱ����Ľڵ�·��
     * @param ctx �����Ķ���
     * @param name �����ʵ�ʴ����Ľڵ���
     */
    public void processResult(int resultCode, String path, Object ctx, String name) {
        System.out.println("result = [" + resultCode + ", " + path + ", " + ctx + ", " + name + "]");
    }
}
