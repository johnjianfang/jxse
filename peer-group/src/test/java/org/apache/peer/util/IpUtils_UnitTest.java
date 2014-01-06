package org.apache.peer.util;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IpUtils_UnitTest {
    @Test
    public void testGetIpAddress() {
        String ip = IpUtils.getIpAddress("10.29.167.187");
        assertEquals("10.29.167.187", ip);
    }

    @Test
    public void testGetNewIpAddress() {
        String ip = IpUtils.getIpAddress("10.29.167.187", 100);
        assertEquals("10.29.167.100", ip);
    }
}
