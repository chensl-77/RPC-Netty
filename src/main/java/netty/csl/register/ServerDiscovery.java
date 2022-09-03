package netty.csl.register;

import com.alibaba.nacos.api.exception.NacosException;

import java.net.InetSocketAddress;

/**
 * 服务发现接口
 * @Author: csl
 **/
public interface ServerDiscovery {

    /**
     * 根据服务名找到InetSocketAddress
     */
    InetSocketAddress getService(String serviceName) throws NacosException;

}
