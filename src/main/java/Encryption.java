import java.math.BigInteger;
import java.util.Random;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.*;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class Encryption {
    private static final int BLOCKSIZE = 32; // in bytes
    private static final int KEYSIZE = 1024; // in bits
    private static final BigInteger e = new BigInteger("733");
    private BigInteger p;
    private BigInteger q;
    private BigInteger myPub;
    private BigInteger myPriv;
    
    public Encryption() {
        Random sRand = new SecureRandom();
        this.p = BigInteger.probablePrime(KEYSIZE, sRand);
        this.q = BigInteger.probablePrime(KEYSIZE, sRand);
        this.myPub = p.multiply(q);
        BigInteger totient = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        // private key = (priv)e == 1 (mod totient(pq))
        BigInteger[] ans = Encryption.extendedEuclidean(e, totient);
        //System.out.println("gcd = " + ans[0].toString() + "\na = " + ans[1].toString() + "\nb = " + ans[2].toString());
        //System.out.println("VERIFICATION: " + ans[1].multiply(e).mod(totient).toString());
        this.myPriv = ans[1];
    }
    
    public String decrypt(byte[] ciphertext) {
        /*ByteArrayInputStream in = new ByteArrayInputStream(ciphertext);
        byte[] result = null;
        String cleartext = "";
        byte[] buffer = new byte[BLOCKSIZE];
        int len;
        while((len = in.read(buffer, 0, BLOCKSIZE)) > 0) {
            System.out.println("dBlock");
            BigInteger tc = (new BigInteger(buffer)).modPow(myPriv, myPub);
            byte[] c = tc.toByteArray();
            if (result == null) {
                result = c;
            } else {
                byte[] t = result;
                result = new byte[t.length + c.length];
                System.arraycopy(t, 0, result, 0, t.length);
                System.arraycopy(c, 0, result, t.length, c.length);
            }
        }
        cleartext = new String(result, StandardCharsets.UTF_8);
        return cleartext;*/
        return new String((new BigInteger(ciphertext)).modPow(myPriv, myPub).toByteArray());
    }
    
    public BigInteger getPublicKey() {
        return this.myPub;
    }
    
    public static byte[] encrypt(BigInteger otherPub, String message) {
        /*try {
            ByteArrayInputStream in = new ByteArrayInputStream(message.getBytes("utf-8"));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[BLOCKSIZE];
            int len;
            int count = 0;
            while ((len = in.read(buffer, 0, BLOCKSIZE)) > 0) {
                System.out.println("eBLOCK");
                BigInteger tc = (new BigInteger(buffer)).modPow(e, otherPub);
                byte[] c = tc.toByteArray();
                out.write(c, count * BLOCKSIZE, BLOCKSIZE);
                count += 1;
            }
            System.out.println(out.size() + "");
            return out.toByteArray();
        } catch (UnsupportedEncodingException e) {
            System.out.println("Malformed message sent to encrypt");
        }
        return null;*/
        return (new BigInteger(message.getBytes())).modPow(e, otherPub).toByteArray();
    }
    
    private static final BigInteger[] extendedEuclidean(BigInteger a, BigInteger b) {
        BigInteger[] ans = new BigInteger[3];
        BigInteger q;
        if (b.equals(new BigInteger("0"))) {
            ans[0] = a;
            ans[1] = new BigInteger("1");
            ans[2] = new BigInteger("0");
        } else {
            q = a.divide(b);
            ans = extendedEuclidean(b, a.mod(b));
            BigInteger t = ans[1].subtract(ans[2].multiply(q));
            ans[1] = ans[2];
            ans[2] = t;
        }
        return ans;
    }
}
