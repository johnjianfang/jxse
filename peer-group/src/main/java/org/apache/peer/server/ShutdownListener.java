package org.apache.peer.server;


public interface ShutdownListener {

    void shutdown();

    boolean isRunning();

}
