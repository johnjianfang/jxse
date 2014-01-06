package org.apache.peer.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

public class ConsistentHash {

    private static final Logger log = LoggerFactory.getLogger(ConsistentHash.class);

    protected TreeMap<Long, String> buckets;

    protected MessageDigest md5;

    protected final int numberOfPoints;

    protected Random random;

    public int getNumberOfPoints() {
        return numberOfPoints;
    }

    public ConsistentHash() throws NoSuchAlgorithmException {
        //TreeMap is not thread safe for structure updates
        buckets = new TreeMap<Long, String>();
        numberOfPoints = 100;
        //MessageDigest is not thread safe
        md5 = MessageDigest.getInstance("MD5");
        random = new Random(System.currentTimeMillis());
    }

    public ConsistentHash(int numberOfPoints) throws NoSuchAlgorithmException {
        this.numberOfPoints = numberOfPoints;
        //TreeMap is not thread safe for structure updates
        buckets = new TreeMap<Long, String>();

        //MessageDigest is not thread safe
        md5 = MessageDigest.getInstance("MD5");
        random = new Random(System.currentTimeMillis());
    }

    public long hash(String key){
        return md5Hash(key);
    }

    public synchronized long md5Hash(String key) {
        md5.reset();
        md5.update(key.getBytes());
        byte[] bKey = md5.digest();
        return ((long) (bKey[3] & 0xFF) << 24) | ((long) (bKey[2] & 0xFF) << 16)
                | ((long) (bKey[1] & 0xFF) << 8) | (long) (bKey[0] & 0xFF);
    }

    public long djb2Hash(String key){
        long hash = 5381;
        for(char c: key.toCharArray()){
            hash = ((hash << 5) + hash) + c;
        }

        return hash;
    }

    public long plHash(String key){
        long hash = 0;
        for(char c: key.toCharArray()){
            hash = hash * 101 + c;
        }

        return hash;
    }

    public synchronized long lookupKey(long hashValue) {
        // ceilingKey returns the least key greater than or equal to the given key,
        // or null if no such key.
        Long key = buckets.ceilingKey(hashValue);

        // if none found, it must be at the end, return the lowest in the tree
        // (we go over the end the continuum to the first entry)
        if (key == null)
            key = buckets.firstKey();
        return key;
    }

    public synchronized String lookupValue(long hashValue){
        // ceilingKey returns the least key greater than or equal to the given key,
        // or null if no such key.
        Long key = buckets.ceilingKey(hashValue);

        // if none found, it must be at the end, return the lowest in the tree
        // (we go over the end the continuum to the first entry)
        if (key == null)
            key = buckets.firstKey();

        return buckets.get(key);
    }

    public synchronized void addPeer(String pid){
        for(int i=0; i< numberOfPoints; i++){
            long value = hash(pid + "_" + i);
            buckets.put(value, pid);
            log.debug("Added server " + pid + " to bucket " + value);
        }
    }

    public void addPeers(List<String> pids) {
        if (pids != null && (!pids.isEmpty())) {
            for(String serverId: pids){
                addPeer(serverId);
            }
        }
    }

    public synchronized void removePeer(String pid){
        for(int i=0; i< numberOfPoints; i++){
            long value = hash(pid + "_" +  i);
            buckets.remove(value);
            log.debug("Removed server " + pid + " from bucket " + value);
        }
    }

    public void removePeers(List<String> pids){
        if(pids != null && (!pids.isEmpty())){
            for(String serverId: pids){
                removePeer(serverId);
            }
        }
    }

    public synchronized String getPeerWithSeed(String key){
        if(buckets.isEmpty()){
            log.warn("No servers are found");
            return null;
        }

        if(buckets.size() == 1){
            return buckets.firstEntry().getValue();
        }

        int seed = random.nextInt(numberOfPoints);

        long hashValue = hash(key + "_" + seed);

        return lookupValue(hashValue);
    }

    public synchronized String getPeerWithRetries(String key){
        if(buckets.isEmpty()){
            log.warn("No servers are found");
            return null;
        }

        if(buckets.size() == 1){
            return buckets.firstEntry().getValue();
        }

        int tries = 0;
        long hashValue = 0;
        while(tries++ < numberOfPoints){
            hashValue += hash(key + "_" + tries);
            Long foundKey = buckets.ceilingKey(hashValue);
            if(foundKey != null){
                return buckets.get(foundKey);
            }
        }

        return buckets.firstEntry().getValue();
    }

    public synchronized String getPeer(String key){
        if(buckets.isEmpty()){
            log.warn("No servers are found");
            return null;
        }

        if(buckets.size() == 1){
            return buckets.firstEntry().getValue();
        }

        long hashValue = hash(key);

        return lookupValue(hashValue);
    }
}
