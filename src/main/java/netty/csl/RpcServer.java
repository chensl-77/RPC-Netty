package netty.csl;


import lombok.extern.slf4j.Slf4j;
import netty.csl.annotation.server.RpcServerScan;
import netty.csl.server.RpcServiceManager;

@Slf4j
@RpcServerScan
public class RpcServer {

    public static void main(String[] args) {
        //创建服务管理器  启动服务
        new RpcServiceManager("127.0.0.1",5678).start();
//        new RpcServiceManager("127.0.0.1",5679).start();
    }
}
