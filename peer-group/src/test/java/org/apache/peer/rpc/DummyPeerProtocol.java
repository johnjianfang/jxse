package org.apache.peer.rpc;


import org.apache.avro.AvroRemoteException;
import org.apache.peer.protocol.*;

public class DummyPeerProtocol implements Discovery, Membership, Transport {
    @Override
    public Response hello(Advertisement adv) throws AvroRemoteException {
        Response response = new Response();
        response.setMessage("Hello");

        return response;
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
