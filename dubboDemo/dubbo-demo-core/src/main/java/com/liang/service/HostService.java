package com.liang.service;

/**
 * Created by setup on 2017/7/26.
 */
public interface HostService {
    /**
     * 判断当前主机是否master
     *
     * @return
     */
    boolean isMasterHost();

    /**
     * 将当前主机设置为master
     *
     * @return
     */
    String switchThisHostToMaster();

    /**
     * 打印主机详情
     *
     * @return
     */
    public String clearMasterHostName();
}
