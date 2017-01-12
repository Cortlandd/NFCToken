package com.tokens.nfc.nfctokens;

import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity
        implements
            SendFragment.OnSendMessage,
        ListingFragment.ListingInterface {

    NfcAdapter nfcAdapter;

    ViewPager viewPager;
    PageAdapter pageAdapter;

    ArchiveManager am;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_pager);

        am = new ArchiveManager(getExternalFilesDir(null));
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        pageAdapter = new PageAdapter(getSupportFragmentManager(), am);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pageAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    // Callback for SendMessage fragment
    public void onSendMessage(String msg) {
        NdefRecord ndefRBody = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], msg.getBytes());

        File keyFile = new File(am.appDir, "keys/MyPrivateKey.priv");
        byte bPrivKey[] = am.readFile64(keyFile);

        byte bSig[] = am.signMessage(msg.getBytes(), bPrivKey);

        NdefRecord ndefRSig = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], bSig);

        NdefRecord arrNdefR[] = new NdefRecord[]{ ndefRBody, ndefRSig };
        NdefMessage ndefMsg = new NdefMessage(arrNdefR);

        nfcAdapter.setNdefPushMessage(ndefMsg, this);

        Button sendButton = (Button) findViewById(R.id.button_send);
        sendButton.setText("Sending...");
    }

    // Callback for ListingFragment
    public void onListItemClick(Record r) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("com.tokens.nfc.nfctokens.data", r.data);
        intent.putExtra("com.tokens.nfc.nfctokens.sig", r.signature);
        intent.putExtra("com.tokens.nfc.nfctokens.pub", r.publicKey);

        startActivity(intent);
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);

        NdefMessage msg = (NdefMessage) rawMsgs[0];
        NdefRecord ndefRecords[] = msg.getRecords();
        String strMsg = new String(ndefRecords[0].getPayload());
        byte[] bSig = ndefRecords[1].getPayload();

        Timestamp stamp = new Timestamp(System.currentTimeMillis());
        Date date = new Date(stamp.getTime());
        Log.d("Date ", date.toString());

        String strFirst10 = new String(first10Char(strMsg));
        String strTimestamp = stamp.toString().replace(' ', '-');
        String filename = strFirst10 + "-" + strTimestamp;

        File dataFile = am.writeFile(filename, strMsg.getBytes());
        am.writeFile64(filename+".sig", bSig);

        Record record = am.makeRecord(dataFile);
        onListItemClick(record);
    }

    String first10Char(String s) {
        String res = "";
        for (char c : s.toCharArray()) {
            if (res.length() >= 10)
                break;

            if ((c>=97 && c<=122) || c>=65 && c<=90) {
                res += c;
            }
        }
        return res;
    }

    public void showErr(String errmsg) {
        TextView textView = (TextView) findViewById(R.id.errText);
        textView.setText(errmsg);
    }

    void genKey(View v) {
        am.genKey();
    }

    void signAll(View v) {
        ArrayList<File> files = am.arraylistFiles();
        File keyFile = new File(am.appDir, "keys/MyPrivateKey.priv");
        byte bPrivKey[] = am.readFile64(keyFile);
        for (File f : files) {
            byte bytes[] = am.readFile(f);
            byte sig[] = am.signMessage(bytes, bPrivKey);
            am.writeFile64(f.getName()+".sig", sig);
        }
    }
}


/*

    public NdefRecord createTextRecord(String payload, Locale locale, boolean encodeInUtf8) {
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");

        byte[] textBytes = payload.getBytes(utfEncoding);
        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);

        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;

        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

        NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], data);

        return record;
    }
 */