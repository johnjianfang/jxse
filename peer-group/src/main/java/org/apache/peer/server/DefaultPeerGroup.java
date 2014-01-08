package org.apache.peer.server;


import org.apache.peer.protocol.Discovery;
import org.apache.peer.protocol.PeerDiscoveryManager;
import org.apache.peer.protocol.PeerDiscoveryProtocol;
import org.apache.peer.rpc.RPCServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DefaultPeerGroup extends Thread implements LifeCycle {

    private static final Logger log = LoggerFactory.getLogger(DefaultPeerGroup.class);

    private boolean running = false;

    private List<ShutdownListener> listeners;

    private final int port;

    private final String token;

    private RPCServer discoveryRPCServer;

    private PeerDiscoveryManager discoveryManager;

    public DefaultPeerGroup(int port, String token) {
        this.port = port;
        this.token = token;
    }

    @Override
    public void startService() {
        running = true;

        log.info("Adding Shutdown Hook...");
        ShutdownHook hook = new ShutdownHook();
        hook.setLifeCycleListener(this);
        Runtime.getRuntime().addShutdownHook(hook);

        listeners = new ArrayList<ShutdownListener>();
        discoveryRPCServer = new RPCServer(port, Discovery.class, new PeerDiscoveryProtocol());
        listeners.add(discoveryRPCServer);
        Thread rpcServerThread = new Thread(discoveryRPCServer);
        rpcServerThread.setName("RPC Server Thread");
        rpcServerThread.start();

        discoveryManager = new PeerDiscoveryManager(port);
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
