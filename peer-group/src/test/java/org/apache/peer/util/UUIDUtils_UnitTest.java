package org.apache.peer.util;


import org.junit.Test;

import java.util.UUID;

import static junit.framework.Assert.assertNotNull;

public class UUIDUtils_UnitTest {
    @Test
    public void testGetNewID() {
        for (int i=0; i<10; i++) {
            UUID uuid = UUIDUtils.newSeqUUID();
            assertNotNull(uuid);
            System.out.println(uuid.toString());
        }
    }
}
