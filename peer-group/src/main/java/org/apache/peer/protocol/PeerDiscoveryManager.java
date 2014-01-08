package org.apache.peer.protocol;


import org.apache.peer.entity.PeerView;
import org.apache.peer.server.ShutdownListener;
import org.apache.peer.util.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

public class PeerDiscoveryManager implements Runnable, ShutdownListener {

    private static final Logger log = LoggerFactory.getLogger(PeerDiscoveryManager.class);

    private final Set<String> seeds;

    private final Set<String> filters;

    private final PeerView peerView;

    private final int port;

    private boolean running;

    public PeerDiscoveryManager(int port) {
        this.port = port;
        this.seeds = new HashSet<String>();
        this.filters = new HashSet<String>();
        this.peerView = new PeerView();
    }

    public void init() {
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
    }

    public void discover() {
        if (!seeds.isEmpty()) {
            int i = 0;
            List<Thread> threads = new ArrayList<Thread>();

            for (String seed: seeds) {
                LanPeerDiscover discover = new LanPeerDiscover(peerView, port, seed, filters);
                Thread thread = new Thread(discover);
                thread.setName("LanPeerDiscover " + (++i));
                threads.add(thread);
                thread.start();
            }

            for (Thread thread: threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void run() {
        running = true;
        init();
        discover();
    }

    @Override
    public void shutdown() {
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
