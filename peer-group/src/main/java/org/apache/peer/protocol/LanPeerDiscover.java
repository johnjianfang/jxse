package org.apache.peer.protocol;


import org.apache.peer.entity.PeerInfo;
import org.apache.peer.entity.PeerView;
import org.apache.peer.rpc.RPCClientFactory;
import org.apache.peer.util.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

public class LanPeerDiscover implements PeerDiscover {

    private static final Logger log = LoggerFactory.getLogger(LanPeerDiscover.class);

    private final PeerView peerView;

    private final int port;

    private final String seed;

    private Set<String> filters;

    private boolean running = false;

    public LanPeerDiscover(PeerView peerView, int port, String seed, Set<String> filters) {
        this.peerView = peerView;
        this.port = port;
        this.seed = seed;
        this.filters = filters;
    }

    @Override
    public void discover(String seed) {
        for (int i = 0; i <= 254; i++) {
            String ip = IpUtils.getIpAddress(seed, i);
            if (!filters.contains(ip)) {
                check(ip);
            }
        }
    }

    public boolean check(String ip) {
        try {
            Discovery client = RPCClientFactory.getProxy(ip, port, Discovery.class);
            client.ping();
            PeerInfo peer = new PeerInfo();
            peer.setAddress(ip);
            peer.setId(ip);
            peerView.addPeer(peer);

            return true;
        } catch (IOException e) {
            log.debug(e.getMessage());

            return false;
        }
    }

    @Override
    public PeerView getPeerView() {
        return peerView;
    }

    @Override
    public void run() {
        running = true;

        try {
            discover(seed);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
