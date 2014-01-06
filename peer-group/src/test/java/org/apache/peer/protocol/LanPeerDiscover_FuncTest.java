package org.apache.peer.protocol;


import org.apache.peer.entity.PeerView;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.fail;

public class LanPeerDiscover_FuncTest {
    @Test
    public void testPeerDiscovery() {
        LanPeerDiscover discover = new LanPeerDiscover(new PeerView(), 9000, "192.168.2.100", new HashSet<String>());
        Thread thread = new Thread(discover);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }
}
