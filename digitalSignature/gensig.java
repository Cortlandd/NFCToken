import java.io.*;
import java.security.*;
import java.util.Base64;

import java.io.*;
import java.security.*;
 
class GenSig {
 
    public static void main(String[] args) {
 
        /* Generate a DSA signature */
 
        if (args.length != 1) {
            System.out.println("Usage: GenSig nameOfFileToSign");
        }
        else try{

            /* Generate a key pair */
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");

            keyGen.initialize(1024, random);

            KeyPair pair = keyGen.generateKeyPair();
            PrivateKey priv = pair.getPrivate();
            PublicKey pub = pair.getPublic();

            /* Create a Signature object and initialize it with the private key */
            Signature dsa = Signature.getInstance("SHA1withDSA", "SUN"); 

            dsa.initSign(priv);

            /* Update and sign the data */
            FileInputStream fis = new FileInputStream(args[0]);
            BufferedInputStream bufin = new BufferedInputStream(fis);
            byte[] buffer = new byte[1024];
            int len;
            while (bufin.available() != 0) {
                len = bufin.read(buffer);
                dsa.update(buffer, 0, len);
            };
 
            bufin.close();
 
            /* Now that all the data to be signed has been read in, 
                    generate a signature for it */
 
            byte[] realSig = dsa.sign();
			
			Base64.Encoder enc = Base64.getEncoder();
			
            /* Save the signature in a file */
            FileOutputStream sigfos = new FileOutputStream("sig");
            sigfos.write(enc.encode(realSig));
            sigfos.close();
 
			byte[] prikey = enc.encode(priv.getEncoded());
			FileOutputStream prifos = new FileOutputStream("key.pri");
			prifos.write(prikey);
			prifos.close();
         
            /* Save the public key in a file */
            byte[] key = enc.encode(pub.getEncoded());
            FileOutputStream keyfos = new FileOutputStream("key.pub");
            keyfos.write(key);
            keyfos.close();
 
        } catch (Exception e) {
            System.err.println("Caught exception " + e.toString());
        }
 
    };
 
}
