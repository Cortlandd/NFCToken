package com.tokens.nfc.nfctokens;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SendFragment extends Fragment {

    protected View rootView;
    OnSendMessage mainActivity;

    public interface OnSendMessage {
        void onSendMessage(String msg);
    }

    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);
        mainActivity = (OnSendMessage)ctx;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.send_fragment, container, false);
        Bundle args = getArguments();

        Button sendButton = (Button) rootView.findViewById(R.id.button_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText textBox = (EditText) rootView.findViewById(R.id.editText);
                Context ctx = view.getContext();
                String message = textBox.getText().toString();

                mainActivity.onSendMessage(message);
            }
        });

        return rootView;
    }
}
