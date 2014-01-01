package org.apache.peer.rpc;


import org.apache.avro.ipc.NettyTransceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;

import java.io.IOException;
import java.net.InetSocketAddress;

public class RPCClientFactory {

    public static <T> T getProxy(String hostname, int port, Class<T> clazz) throws IOException {
        NettyTransceiver client = new NettyTransceiver(new InetSocketAddress(hostname, port));
        return SpecificRequestor.getClient(clazz, client);
    }
}
