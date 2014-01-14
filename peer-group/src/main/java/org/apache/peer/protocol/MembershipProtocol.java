package org.apache.peer.protocol;


import org.apache.avro.AvroRemoteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MembershipProtocol implements Membership {

    private static final Logger log = LoggerFactory.getLogger(MembershipProtocol.class);

    @Override
    public Response hello(Advertisement adv) throws AvroRemoteException {
        return null;
    }

    @Override
    public void ping() {
        log.info("Received ping");
    }

    @Override
    public Challenge join(JoinRequest request) throws AvroRemoteException {
        return null;
    }

    @Override
    public JoinResponse challenge(ChallengeResponse response) throws AvroRemoteException {
        return null;
    }
}
