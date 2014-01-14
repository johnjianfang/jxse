package org.apache.peer.server;

import org.apache.peer.protocol.Membership;
import org.apache.peer.protocol.MembershipProtocol;
import org.apache.peer.protocol.PeerDiscoveryManager;
import org.apache.peer.rpc.RPCServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DefaultPeerGroup extends Thread implements LifeCycle {

    private static final Logger log = LoggerFactory.getLogger(DefaultPeerGroup.class);

    private boolean running = false;

    private List<ShutdownListener> listeners;

    private final int port;

    private final String token;

    private final Set<String> seeds;

    private int threadNum;

    private RPCServer memberRPCServer;

    private PeerDiscoveryManager discoveryManager;

    public DefaultPeerGroup(Set<String> seeds, int port, String token) {
        this.seeds = seeds;
        this.port = port;
        this.token = token;
        this.threadNum = 10;
    }

    @Override
    public void startService() {
        running = true;

        log.info("Adding Shutdown Hook...");
        ShutdownHook hook = new ShutdownHook();
        hook.setLifeCycleListener(this);
        Runtime.getRuntime().addShutdownHook(hook);

        listeners = new ArrayList<ShutdownListener>();
        memberRPCServer = new RPCServer(port, Membership.class, new MembershipProtocol());
        listeners.add(memberRPCServer);
        Thread rpcServerThread = new Thread(memberRPCServer);
        rpcServerThread.setName("RPC Server Thread");
        rpcServerThread.start();

        discoveryManager = new PeerDiscoveryManager(seeds, port, threadNum);
        listeners.add(discoveryManager);
        Thread discoveryManagerThread = new Thread(discoveryManager);
        discoveryManagerThread.setName("Discovery Manager Thread");
        discoveryManagerThread.start();

        while (running){
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public void stopService() {
        log.info("Stopping PeerGroup...");
        running = false;

        log.info("Shutdown listeners...");
        for(ShutdownListener listener: listeners){
            try{
                listener.shutdown();
            }catch (Exception e){
                log.error(e.getMessage(), e);
            }
        }
    }

    public synchronized void run() {
        running = true;

        try {
            startService();
        } catch (Exception e) {
            // do nothing, and continue
            log.error("Error running PeerGroup " + e.getMessage(), e);
        }
    }
}
