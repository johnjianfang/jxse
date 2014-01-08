package org.apache.peer.protocol;


import org.apache.avro.AvroRemoteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PeerDiscoveryProtocol implements Discovery {

    private static final Logger log = LoggerFactory.getLogger(PeerDiscoveryProtocol.class);

    @Override
    public Response hello(Advertisement adv) throws AvroRemoteException {
        return null;
    }

    @Override
    public void ping() {
        log.info("Received ping");
    }
}
