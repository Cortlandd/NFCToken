package com.tokens.nfc.nfctokens;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Created by Kai on 2017-01-11.
 */

public class Record {
    File data;
    File signature;
    File publicKey;

    public Record(File data, File signature, File publicKey) {
        this.data = data;
        this.signature = signature;
        this.publicKey = publicKey;
    }

    public String getData() {
        return new String(ArchiveManager.readFile(data));
    }

    public String getSignature() {
        return new String(ArchiveManager.readFile(signature));
    }

    public String getPublicKey() {
        return new String(ArchiveManager.readFile(publicKey));
    }
}
