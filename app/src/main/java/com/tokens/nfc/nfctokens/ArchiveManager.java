package com.tokens.nfc.nfctokens;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;

import android.util.Base64;
/**
 * Created by Kai on 1/8/2017.
 */

public class ArchiveManager {

    File appDir;
    File keysDir;

    ArchiveManager(File appDir) {
        this.appDir = appDir;
        if (!(appDir.canRead() && appDir.canWrite())) {
            return;
        }

        keysDir = new File(appDir, "/keys");
        if (!keysDir.canRead()) {
            keysDir.mkdir();
        }
    }
    /*
     * List all records that should be displayed in the ListingFragment
     */
    @SuppressLint("LongLogTag")
    ArrayList<Record> list() {
        File arrFiles[] = appDir.listFiles();
        ArrayList<Record> result = new ArrayList<>();
        for (File f : arrFiles) {
            if (f.isFile() && !f.getName().endsWith(".sig")) {
                Record r = makeRecord(f);
                if (r != null)
                    result.add(r);
            }
        }

        return result;
    }

    // Same as list, but return an arraylist of files instead of records.
    ArrayList<File> arraylistFiles(){
        File arrFiles[] = appDir.listFiles();
        ArrayList<File> result = new ArrayList<>();
        for (File f : arrFiles) {
            if (f.isFile() && !f.getName().endsWith(".sig")) {
                result.add(f);
            }
        }
        return result;
    }

    /*
     * Construct a Record given a data file
     */
    public Record makeRecord(File f) {
        try {
            byte[] message = readFile(f);
            File sigFile = new File(f.getAbsolutePath() + ".sig");
            byte[] bSig = null;
            try {
                bSig = readFile64(sigFile);
            } catch (Exception e) {
                Log.e("Failed sig file", e.toString());
            }
            File keyFile = verifyMessage(message, bSig);

            return new Record(f, sigFile, keyFile);
        } catch (Exception e) {
            Log.e("Failed to read file", e.toString());
        }
        return null;
    }

    File[] listKeys() {
        return keysDir.listFiles();
    }

    public byte[] signMessage(byte[] bMessage, byte[] bPrivKey) {
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bPrivKey);
            KeyFactory keyFactory = KeyFactory.getInstance("DSA");
            PrivateKey privKey = keyFactory.generatePrivate(keySpec);
            Signature sig = Signature.getInstance("DSA");
            sig.initSign(privKey);
            sig.update(bMessage);
            return sig.sign();
        } catch (Exception e) {
            Log.e("Decryption failed ", e.toString());
        }
        return null;
    }

    File verifyMessage(byte[] bMessage, byte[] bSignature) {
        if (bSignature==null)
            return null;

        File[] keyFiles = listKeys();
        for (File f : keyFiles) {
            if (!f.getName().endsWith(".pub"))
                continue;

            byte[] bKey = readFile64(f);
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec (bKey);
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("DSA");
                PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
                Signature sig = Signature.getInstance("DSA");
                sig.initVerify(pubKey);
                sig.update(bMessage);
                if (sig.verify(bSignature)) {
                    return f;
                }
            } catch (Exception e) {
                Log.e("No such algorithm ", e.toString());
            }
        }
        return null;
    }

    public void genKey() {
        KeyPairGenerator keyGen;
        try {
            keyGen = KeyPairGenerator.getInstance("DSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            keyGen.initialize(1024, random);
        } catch (Exception e) {
            Log.e("Failed to get instance", e.toString());
            return;
        }

        KeyPair pair = keyGen.generateKeyPair();
        PrivateKey priv = pair.getPrivate();
        PublicKey pub = pair.getPublic();

        writeFile64("keys/MyPrivateKey.priv", priv.getEncoded());
        writeFile64("keys/MyPublicKey.pub", pub.getEncoded());

        byte bPrivKey[] = priv.getEncoded();
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bPrivKey);
            KeyFactory keyFactory = KeyFactory.getInstance("DSA");
            PrivateKey privKey = keyFactory.generatePrivate(keySpec);
            Signature sig = Signature.getInstance("DSA");
            sig.initSign(privKey);
        } catch (Exception e) {
            Log.e("Failed to sign", e.toString());
        }
    }

    static byte[] readFile(File file) {
        try {
            FileInputStream stream = new FileInputStream(file);
            byte[] bytes = new byte[stream.available()];
            stream.read(bytes);
            stream.close();
            return bytes;
        } catch (IOException e) {
            Log.e("FileIO", "Failed to create FileInputStream from file " + file.toString());
        }
        return null;
    }

    byte[] readFile64(File file) {
        byte[] bytes64 = readFile(file);
        byte[] bytes = Base64.decode(bytes64, Base64.DEFAULT);
        return bytes;
    }

    File writeFile(String filename, byte[] data) {
        File newFile = new File(appDir, filename);
        try {
            newFile.createNewFile();
            FileOutputStream stream = new FileOutputStream(newFile);
            stream.write(data);
            stream.close();
            return newFile;
        } catch (Exception e) {
            Log.e("failed to create file", e.toString());
        }

        return null;
    }

    File writeFile64(String filename, byte[] data) {
        byte[] b64 = Base64.encode(data, Base64.DEFAULT);
        return writeFile(filename, b64);
    }
}
