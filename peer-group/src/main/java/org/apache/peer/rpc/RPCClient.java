package org.apache.peer.rpc;


import org.apache.avro.ipc.NettyTransceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.peer.protocol.Discovery;
import org.apache.peer.protocol.PeerProtocol;

import java.net.InetSocketAddress;

public class RPCClient {
    final private String hostname;
    final private int port;
    private NettyTransceiver client;
    private PeerProtocol proxy;

    public RPCClient(String hostname, int port) throws Exception {
        this.hostname = hostname;
        this.port = port;
        client = new NettyTransceiver(new InetSocketAddress(hostname, port));
        proxy = SpecificRequestor.getClient(PeerProtocol.class, client);
    }

    PeerProtocol getProxy() {
        return proxy;
    }
}
