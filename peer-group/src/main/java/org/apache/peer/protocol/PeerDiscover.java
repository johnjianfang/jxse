package org.apache.peer.protocol;


import org.apache.peer.entity.PeerView;

public interface PeerDiscover extends Runnable {

    void discover();

    PeerView getPeerView();

}
