package org.apache.peer.security;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Jian Fang (John.Jian.Fang@gmail.com)
 *
 * Date: Jul 3, 2009
 */
public class SecurityUtil_UnitTest {

    @Test
    public void testCompressDecompress(){
        String hello = "Hello, SAcct. This is a test message!";

        byte[] compressed = SecurityUtil.compress(hello.getBytes());
        byte[] decompressed = SecurityUtil.decompress(compressed);
        System.out.println("Orignial size " + hello.length() + ", compressed size " + compressed.length);
        assertEquals(hello, new String(decompressed));
        String response = "Response: {\"isSuccessful\":true,\"returnValue\":[\"Hello world\"],\"errorCode\":null,\"command\":\"hello\",\"rnd\":\"Nzg0MjA2MTk0NDY3NjI4OTQ1MjczMjk2MjM5NDA3NDAwOTUxNDA0ODM4NTA2MTM5NDk1OTkxODYzNTY0MDY4Mg==\"}";
        compressed = SecurityUtil.compress(response.getBytes());
        decompressed = SecurityUtil.decompress(compressed);
        System.out.println("Orignial size " + response.length() + ", compressed size " + compressed.length);
        assertEquals(response, new String(decompressed));
    }
}
