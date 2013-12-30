package org.apache.peer.protocol;


import org.apache.avro.AvroRemoteException;

public class DiscoveryProtocol implements Discovery {
    @Override
    public Response hello(Advertisement adv) throws AvroRemoteException {
        Response response = new Response();
        response.setMessage("Hello");

        return response;
    }

    @Override
    public void heartbeat() {

    }
}
