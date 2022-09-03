package netty.csl.server.service;


import netty.csl.annotation.server.RpcServer;

@RpcServer
public class CSLServiceImpl implements HelloService{
    @Override
    public String sayHello(String name) {
        return "csl说你好"+name;
    }
}
