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


public class Encryption {
    private static final int BLOCKSIZE = 256; // in bytes
    private static final int KEYSIZE = 512; // in bytes
    private static final BigInteger e = new BigInteger("733");
    private BigInteger p;
    private BigInteger q;
    private BigInteger myPub;
    private BigInteger myPriv;
    
    public Encryption() {
        this.p = BigInteger.probablePrime(KEYSIZE * 8, new Random());
        this.q = BigInteger.probablePrime(KEYSIZE * 8, new Random());
        this.myPub = p.multiply(q);
        BigInteger totient = p.subtract(new BigInteger("1")).multiply(q.subtract(new BigInteger("1")));
        // private key = (priv)e == 1 (mod totient(pq))
        BigInteger[] ans = Encryption.extendedEuclidean(e, totient);
        //System.out.println("gcd = " + ans[0].toString() + "\na = " + ans[1].toString() + "\nb = " + ans[2].toString());
        //System.out.println("VERIFICATION: " + ans[1].multiply(e).mod(totient).toString());
        this.myPriv = ans[1];
    }
    
    public String decrypt(byte[] ciphertext) {
        ByteArrayInputStream in = new ByteArrayInputStream(ciphertext);
        String result = "";
        byte[] buffer = new byte[BLOCKSIZE];
        int len;
        try {
            while((len = in.read(buffer)) > 0) {
                BigInteger tc = (new BigInteger(buffer)).modPow(myPriv, myPub);
                byte[] c = tc.toByteArray();
                result += new String(c, "utf-8");
            }
        } catch (IOException e) {
            System.out.println("Could not perform IO in decrypt");
        } 
        return result;
    }
    
    public BigInteger getPublicKey() {
        return this.myPub;
    }
    
    public static byte[] encrypt(BigInteger otherPub, String message) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(message.getBytes("utf-8"));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[BLOCKSIZE];
            int len;
            int count = 0;
            while ((len = in.read(buffer)) > 0) { 
                BigInteger tc = (new BigInteger(buffer)).modPow(e, otherPub);
                byte[] c = tc.toByteArray();
                out.write(c, count * BLOCKSIZE, c.length);
                count += 1;
            }
            return out.toByteArray();
        } catch (UnsupportedEncodingException e) {
            System.out.println("Malformed message sent to encrypt");
        } catch (IOException e) {
            System.out.println("Could not perform IO in encrypt");
        }
        return null;
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
