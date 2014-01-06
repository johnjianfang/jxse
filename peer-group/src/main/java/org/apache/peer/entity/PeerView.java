package org.apache.peer.entity;


import java.util.Map;

public class PeerView {

    private Map<String, PeerInfo> peers;

    public Map<String, PeerInfo> getPeers() {
        return peers;
    }

    public void setPeers(Map<String, PeerInfo> peers) {
        this.peers = peers;
    }

    public void addPeer(PeerInfo peer) {
        peers.put(peer.getId(), peer);
    }
}
