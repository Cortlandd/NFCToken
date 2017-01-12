package com.tokens.nfc.nfctokens;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

public class DetailsActivity extends AppCompatActivity {

    boolean expanded = false;
    String msgLongSig = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detials);

        Bundle bundle = getIntent().getExtras();
        File data = (File)bundle.get("com.tokens.nfc.nfctokens.data");
        File sig = (File)bundle.get("com.tokens.nfc.nfctokens.sig");
        File pub = (File)bundle.get("com.tokens.nfc.nfctokens.pub");

        byte bytes[] = ArchiveManager.readFile(data);

        msgLongSig = "Signature: \n" + new String(ArchiveManager.readFile(sig)) + "\n";
        if (pub != null) {
            msgLongSig += "Public Key: \n" + new String(ArchiveManager.readFile(pub)) + "\n";
        }

        TextView tvDetails = (TextView) findViewById(R.id.tv_details);
        tvDetails.setText(new String(bytes));

        LinearLayout llvert = (LinearLayout) findViewById(R.id.signature_vert);
        ImageView ivLock = (ImageView) findViewById(R.id.img_lock_sig);
        TextView tvShort = (TextView) findViewById(R.id.signature_short);
        if (pub != null) {
            ivLock.setImageResource(R.drawable.ic_lock_black_24dp);
            ivLock.setColorFilter(Color.argb(255, 50, 210, 30));
            tvShort.setText("Signature verified with public key " + pub.getName());
        } else {
            ivLock.setImageResource(R.drawable.ic_lock_open_black_24dp);
            ivLock.setColorFilter(Color.argb(255, 230, 50, 20));
            tvShort.setText("Cannot verify digital signature.");
        }

        llvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expanded = !expanded;
                redrawExpandable(view);
            }
        });
    }

    void redrawExpandable(View view) {
        TextView tvLong = (TextView) findViewById(R.id.signature_long);
        if (expanded) {
            tvLong.setText(msgLongSig);
        } else {
            tvLong.setText("");
        }
    }

}
