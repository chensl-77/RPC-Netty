package netty.csl;


import lombok.extern.slf4j.Slf4j;
import netty.csl.client.ClientProxy;
import netty.csl.client.RpcClientManager;
import netty.csl.server.service.CSLServiceImpl;
import netty.csl.server.service.HelloService;

@Slf4j
public class RpcClient {
    public static void main(String[] args) {
        RpcClientManager clientManager = new RpcClientManager();
        //创建代理对象
        HelloService service = new ClientProxy(clientManager).getProxyService(CSLServiceImpl.class);
        System.out.println(service.sayHello("zhangsan"));
    }
}
