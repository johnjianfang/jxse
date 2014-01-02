package org.apache.peer.access;


public interface Authenticator {

    int getChallenge(String gid, String pid);

    boolean verifyChallenge(String gid, String pid, String response);

    String generateChallenge(String gid, int seed);
}
