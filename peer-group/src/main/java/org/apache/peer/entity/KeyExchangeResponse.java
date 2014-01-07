package org.apache.peer.entity;

import javax.crypto.SecretKey;
import java.io.Serializable;

/**
 * @author Jian Fang (John.Jian.Fang@gmail.com)
 *
 * Date: Jul 2, 2009
 */
public class KeyExchangeResponse implements Serializable {
    private static final long serialVersionUID = 8746325572870549010L;
    
    private byte[] pubKeyEnc;

    private SecretKey key;

    public byte[] getPubKeyEnc() {
        return pubKeyEnc;
    }

    public void setPubKeyEnc(byte[] pubKeyEnc) {
        this.pubKeyEnc = pubKeyEnc;
    }

    public SecretKey getKey() {
        return key;
    }

    public void setKey(SecretKey key) {
        this.key = key;
    }
}
