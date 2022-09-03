package netty.csl.register;

import com.alibaba.nacos.api.exception.NacosException;

import java.net.InetSocketAddress;

/**
 * @Author: csl
 **/
public class NacosServerRegistry implements ServerRegistry {
    /**
     * 服务注册
     * @param serviceName
     * @param inetSocketAddress
     */
    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NacosUtils.registerServer(serviceName,inetSocketAddress);
            System.out.println("注册"+serviceName);
        } catch (NacosException e) {
            throw new RuntimeException("注册Nacos出现异常");
        }
    }

    /**
     * 根据服务名获取地址
     * @param serviceName
     * @return
     */
    @Override
    public InetSocketAddress getService(String serviceName) {
        return null;
    }
}
