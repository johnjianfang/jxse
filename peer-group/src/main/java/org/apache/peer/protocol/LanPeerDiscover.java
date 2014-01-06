package org.apache.peer.protocol;


import org.apache.peer.entity.PeerInfo;
import org.apache.peer.entity.PeerView;
import org.apache.peer.rpc.RPCClientFactory;
import org.apache.peer.util.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class LanPeerDiscover implements PeerDiscover {

    private static final Logger log = LoggerFactory.getLogger(LanPeerDiscover.class);

    private final PeerView peerView;

    private final int port;

    private Set<String> seeds;

    private Set<String> filters;

    private boolean running = false;

    public LanPeerDiscover(PeerView peerView, int port) {
        this.peerView = peerView;
        this.port = port;
        this.seeds = new HashSet<String>();
        this.filters = new HashSet<String>();
    }

    @Override
    public void discover() {
        for (String seed: seeds) {
            for (int i=0; i<=254; i++) {
                String ip = IpUtils.getIpAddress(seed, i);
                if (!filters.contains(ip)) {
                    check(ip);
                }
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
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface nif = interfaces.nextElement();
                if (!nif.isLoopback()) {
                    Enumeration<InetAddress> addresses = nif.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress ip = addresses.nextElement();
                        if ((!ip.isLinkLocalAddress()) && (!ip.isAnyLocalAddress()) && (!ip.isLoopbackAddress())) {
                            filters.add(ip.getHostAddress());
                            seeds.add(IpUtils.getIpAddress(ip.getHostAddress(), 0));
                        }
                    }
                }
            }
        } catch (SocketException e) {
            log.error(e.getMessage(), e);
        }

        try {
            discover();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
