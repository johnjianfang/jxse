package org.apache.peer.rpc;


import org.apache.avro.ipc.NettyServer;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.specific.SpecificResponder;
import org.apache.peer.protocol.Discovery;
import org.apache.peer.protocol.PeerProtocol;
import org.apache.peer.server.LifeCycle;
import org.apache.peer.server.ShutdownListener;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.execution.ExecutionHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class RPCServer implements Runnable, ShutdownListener {

    private Server server;

    final private int port;

    final private PeerProtocol protocol;

    private volatile boolean running = false;

    public RPCServer(int port, PeerProtocol protocol) {
        this.port = port;
        this.protocol = protocol;

    }

    @Override
    public void run() {
        server = new NettyServer(new SpecificResponder(PeerProtocol.class, protocol),
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
