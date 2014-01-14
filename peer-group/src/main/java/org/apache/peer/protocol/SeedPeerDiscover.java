package org.apache.peer.protocol;


import org.apache.avro.ipc.NettyTransceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.peer.entity.PeerInfo;
import org.apache.peer.entity.PeerView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Set;

public class SeedPeerDiscover implements PeerDiscover {

    private static final Logger log = LoggerFactory.getLogger(SeedPeerDiscover.class);

    private final Set<String> seeds;

    private final int port;

    private final PeerView peerView;

    private boolean running = false;

    public SeedPeerDiscover(PeerView peerView, Set<String> seeds, int port) {
        this.peerView = peerView;
        this.seeds = seeds;
        this.port = port;
    }

    @Override
    public void discover(String seed) {
        log.info("Checking " + seed);
        check(seed);
    }

    public boolean check(String ip) {
        NettyTransceiver client = null;
        try {
            client = new NettyTransceiver(new InetSocketAddress(ip, port));
            Membership membership = SpecificRequestor.getClient(Membership.class, client);
            membership.ping();
            PeerInfo peer = new PeerInfo();
            peer.setAddress(ip);
            peer.setId(ip);
            peerView.addPeer(peer);
            log.info("Found peer " + peer);
            return true;
        } catch (IOException e) {
            log.debug(e.getMessage());

            return false;
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (Exception e) {
                    log.info(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public PeerView getPeerView() {
        return peerView;
    }

    @Override
    public void run() {
        running = true;

        for (String seed : seeds) {
            try {
                discover(seed);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
