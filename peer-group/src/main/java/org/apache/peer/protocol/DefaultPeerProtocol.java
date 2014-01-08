package org.apache.peer.protocol;

import org.apache.avro.AvroRemoteException;

public class DefaultPeerProtocol implements PeerProtocol {

    @Override
    public Response hello(Advertisement adv) throws AvroRemoteException {
        return null;
    }

    @Override
    public void ping() {

    }

    @Override
    public Challenge join(JoinRequest request) throws AvroRemoteException {
        return null;
    }

    @Override
    public JoinResponse challenge(ChallengeResponse response) throws AvroRemoteException {
        return null;
    }

    @Override
    public Result unicast(CharSequence pid, Payload payload) throws AvroRemoteException {
        return null;
    }

    @Override
    public Result broadcast(Payload payload) throws AvroRemoteException {
        return null;
    }
}
