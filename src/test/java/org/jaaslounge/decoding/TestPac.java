package org.jaaslounge.decoding;

import java.io.IOException;
import java.io.InputStream;

import javax.crypto.spec.SecretKeySpec;

import junit.framework.TestCase;

import org.jaaslounge.decoding.pac.Pac;
import org.junit.Before;
import org.junit.Test;

public class TestPac extends TestCase {

    private byte[] rc4Data;
    private byte[] desData;
    private byte[] corruptData;
    private SecretKeySpec rc4Key;
    private SecretKeySpec desKey;
    private SecretKeySpec corruptKey;

    @Before
    public void setUp() throws IOException {
        InputStream file;
        byte[] keyData;

        file = this.getClass().getClassLoader().getResourceAsStream("rc4-pac-data");
        rc4Data = new byte[file.available()];
        file.read(rc4Data);
        file.close();

        file = this.getClass().getClassLoader().getResourceAsStream("des-pac-data");
        desData = new byte[file.available()];
        file.read(desData);
        file.close();

        corruptData = new byte[]{5, 4, 2, 1, 5, 4, 2, 1, 3};

        file = this.getClass().getClassLoader().getResourceAsStream("rc4-key-data");
        keyData = new byte[file.available()];
        file.read(keyData);
        rc4Key = new SecretKeySpec(keyData, "ArcFourHmac");
        file.close();

        file = this.getClass().getClassLoader().getResourceAsStream("des-key-data");
        keyData = new byte[file.available()];
        file.read(keyData);
        desKey = new SecretKeySpec(keyData, "DES");
        file.close();

        corruptKey = new SecretKeySpec(new byte[]{5, 4, 2, 1, 5, 4, 2, 1, 3}, "");
    }

    @Test
    public void testRc4Pac() {
        try {
            Pac pac = new Pac(rc4Data, rc4Key);

            assertNotNull(pac);
            assertNotNull(pac.getLogonInfo());

            assertEquals("user.test", pac.getLogonInfo().getUserName());
            assertEquals("User Test", pac.getLogonInfo().getUserDisplayName());
            assertEquals(0, pac.getLogonInfo().getBadPasswordCount());
            assertEquals(32, pac.getLogonInfo().getUserFlags());
            assertEquals(46, pac.getLogonInfo().getLogonCount());
            assertEquals("DOMAIN", pac.getLogonInfo().getDomainName());
            assertEquals("WS2008", pac.getLogonInfo().getServerName());

        } catch(DecodingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testDesPac() {
        try {
            Pac pac = new Pac(desData, desKey);

            assertNotNull(pac);
            assertNotNull(pac.getLogonInfo());

            assertEquals("user.test", pac.getLogonInfo().getUserName());
            assertEquals("User Test", pac.getLogonInfo().getUserDisplayName());
            assertEquals(0, pac.getLogonInfo().getBadPasswordCount());
            assertEquals(32, pac.getLogonInfo().getUserFlags());
            assertEquals(48, pac.getLogonInfo().getLogonCount());
            assertEquals("DOMAIN", pac.getLogonInfo().getDomainName());
            assertEquals("WS2008", pac.getLogonInfo().getServerName());

        } catch(DecodingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testCorruptPac() {
        Pac pac = null;
        try {
            pac = new Pac(corruptData, rc4Key);
            fail("Should have thrown DecodingException.");
        } catch(DecodingException e) {
            assertNotNull(e);
            assertNull(pac);
        }
    }

    @Test
    public void testEmptyPac() {
        Pac pac = null;
        try {
            pac = new Pac(new byte[0], rc4Key);
            fail("Should have thrown DecodingException.");
        } catch(DecodingException e) {
            assertNotNull(e);
            assertNull(pac);
        }
    }

    @Test
    public void testNullPac() {
        Pac pac = null;
        try {
            pac = new Pac(null, rc4Key);
            fail("Should have thrown NullPointerException.");
        } catch(DecodingException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } catch(NullPointerException e) {
            assertNotNull(e);
            assertNull(pac);
        }
    }

    @Test
    public void testCorruptKey() {
        Pac pac = null;
        try {
            pac = new Pac(rc4Data, corruptKey);
            fail("Should have thrown DecodingException.");
        } catch(DecodingException e) {
            assertNotNull(e);
            assertNull(pac);
        }
    }
}
