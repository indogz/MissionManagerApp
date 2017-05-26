package com.example.matteo.app1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URISyntaxException;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


;import java.net.URISyntaxException;

import SocketIO.ChatApplication;


public class SocketIOActivity extends AppCompatActivity {

    private EditText mInputMessageView;
    private Socket mSocket;

    {
        try {
            mSocket = IO.socket("https://missionmanager1.herokuapp.com/");
        } catch (URISyntaxException e) {
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket_io);
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.connect();
        System.out.println(mSocket.connected());
        mInputMessageView = (EditText) findViewById(R.id.editTextInput);
        attemptSend();
    }

    private void attemptSend() {
        String message = mInputMessageView.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }

        mInputMessageView.setText("");
        mSocket.emit("new message", message);
    }


    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
           SocketIOActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSocket.emit("add user", "ciao");
                }
            });
        }
    };
}







