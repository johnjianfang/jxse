package org.apache.peer.rpc;


import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.NettyServer;
import org.apache.avro.ipc.NettyTransceiver;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.avro.ipc.specific.SpecificResponder;
import org.apache.peer.protocol.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class DiscoveryRPC_FuncTest {
    private static Server server;
    private static NettyTransceiver client;
    private static Discovery proxy;

    @BeforeClass
    public static void setup() throws Exception {
        server = new NettyServer(new SpecificResponder(Discovery.class, new DiscoveryProtocol()), new InetSocketAddress("127.0.0.1", 65111));
        client = new NettyTransceiver(new InetSocketAddress("127.0.0.1", 65111));
        proxy = (Discovery) SpecificRequestor.getClient(Discovery.class, client);
    }

    @Test
    public void testDiscovery() {
        Advertisement adv = new Advertisement();
        adv.setPid("123");
        adv.setGid("234");
        adv.setName("test");
        adv.setType(Type.DISCOVERY);
        adv.setDescription("test");
        List<String> array = new ArrayList<String>();
        array.add("127.0.0.1");
        adv.setEndpoints(array);
//        Map<String, String> options = new HashMap<String, String>();
//        options.put("test", "test");
//        adv.setParameters(options);
        try {
            Response response = proxy.hello(adv);
            assertNotNull(response);
        } catch (AvroRemoteException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testPing() {
        try {
            proxy.heartbeat();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @AfterClass
    public static void tearDown() {
        if (client != null) {
            client.close();
        }
        if (server != null) {
            server.close();
        }
    }
}
