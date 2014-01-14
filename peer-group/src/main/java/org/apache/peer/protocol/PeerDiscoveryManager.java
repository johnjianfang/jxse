package org.apache.peer.protocol;


import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.apache.peer.entity.PeerView;
import org.apache.peer.server.ShutdownListener;
import org.apache.peer.util.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.Executors;

public class PeerDiscoveryManager implements Runnable, ShutdownListener {

    private static final Logger log = LoggerFactory.getLogger(PeerDiscoveryManager.class);

    private final Set<String> seeds;

    private final Set<String> filters;

    private final PeerView peerView;

    private final int port;

    private boolean running;

    private final ListeningExecutorService pool;

    private final int threadNum;

    public PeerDiscoveryManager(Set<String> seeds, int port, int threadNum) {
        this.port = port;
        this.threadNum = threadNum;
        if (seeds == null || seeds.isEmpty()) {
            this.seeds = new HashSet<String>();
        } else {
            this.seeds = seeds;
        }
        this.filters = new HashSet<String>();
        this.peerView = new PeerView();
        this.pool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(this.threadNum));
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
        if (seeds != null && !seeds.isEmpty()) {
            log.info("Using existing seeds to discover peers...");
            SeedPeerDiscover seedPeerDiscover = new SeedPeerDiscover(peerView, seeds, port);
            seedPeerDiscover.run();
        }
        if (peerView.getPeers() == null || peerView.getPeers().isEmpty()) {
            log.info("Using Lan discover to find peers...");
            init();
            discover();
        }
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
