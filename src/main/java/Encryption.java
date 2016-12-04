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
    
    public static final int e = 733;
    private BigInteger p;
    private BigInteger q;
    private BigInteger myPub;
    private BigInteger myPriv;
    private BigInteger otherPub;
    
    public Encryption() {
        this.p = BigInteger.probablePrime(512, new Random());
        this.q = BigInteger.probablePrime(512, new Random());
        this.myPub = p.multiply(q);
        //BigInteger totient = p.subtract(new BigInteger("1")).multiply(q.subtract(new BigInteger("1")));
        this.myPriv = q.modInverse(p);
    }
    
    public String decrypt(byte[] ciphertext) {
        ByteArrayInputStream in = new ByteArrayInputStream(ciphertext);
        String result = "";
        byte[] buffer = new byte[256];
        int len;
        try {
            while((len = in.read(buffer)) > 0) {
                BigInteger tc = (new BigInteger(buffer)).modPow(myPriv, myPub);
                byte[] c = tc.toByteArray();
                result += new String(c);
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
            byte[] buffer = new byte[256];
            int len;
            int count = 0;
            while ((len = in.read(buffer)) > 0) { 
                BigInteger tc = (new BigInteger(buffer)).modPow(new BigInteger(Integer.toString(e)), otherPub);
                byte[] c = tc.toByteArray();
                out.write(c, count * 1024, c.length);
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
}
