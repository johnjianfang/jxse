package org.apache.peer.access;


import org.apache.commons.codec.digest.DigestUtils;
import org.apache.peer.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TokenAuthenticator implements Authenticator {

    private static final Logger log = LoggerFactory.getLogger(TokenAuthenticator.class);

    private final Map<String, String> tokens;

    private final Map<String, Integer> seeds;

    private final Random random;

    public TokenAuthenticator() {
        tokens = new ConcurrentHashMap<String, String>();
        seeds = new ConcurrentHashMap<String, Integer>();
        random = new Random(StringUtils.reverse(System.currentTimeMillis()));
    }

    public void updateToken(String gid, String token) {
        tokens.put(gid, token);
    }

    @Override
    public int getChallenge(String gid, String pid) {
        Integer seed = random.nextInt(10000);
        seeds.put(getKey(gid, pid), seed);

        return seed;
    }

    @Override
    public boolean verifyChallenge(String gid, String pid, String response) {
        Integer seed = seeds.get(getKey(gid, pid));
        if (seed == null) {
            log.warn("Cannot find challenge seed for " + gid + ":" + pid);
            return false;
        }

        String challenge = createChallenge(gid, seed);

        return challenge.equals(response);
    }

    public String createChallenge(String gid, int seed) {
        String token = tokens.get(gid);
        if (token == null) {
            log.warn("Cannot find token for group " + gid);
            throw new RuntimeException("Cannot find token for group " + gid);
        }

        return DigestUtils.md5Hex(seed + ":" + token);
    }

    public String getKey(String gid, String pid) {
        return gid + "_" + pid;
    }
}
