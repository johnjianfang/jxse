package org.apache.peer.rpc;


import org.apache.avro.ipc.NettyServer;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.specific.SpecificResponder;
import org.apache.peer.protocol.Discovery;
import org.apache.peer.server.ShutdownListener;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.execution.ExecutionHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class RPCServer<T> implements Runnable, ShutdownListener {

    private Server server;

    final private int port;

    final private Class<T> iface;

    final private T protocol;

    private volatile boolean running = false;

    public RPCServer(int port, Class<T> iface, T protocol) {
        this.port = port;
        this.iface = iface;
        this.protocol = protocol;

    }

    @Override
    public void run() {
        server = new NettyServer(new SpecificResponder(iface, protocol),
                new InetSocketAddress(port),
                new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(), Executors.newCachedThreadPool()),
                new ExecutionHandler(Executors.newCachedThreadPool()));
        server.start();
        running = true;
        try {
            server.join();
        } catch (InterruptedException e) {

        }
    }

    @Override
    public void shutdown() {
        if (server != null) {
            server.close();
        }
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
