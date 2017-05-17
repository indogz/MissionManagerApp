package com.example.matteo.app1;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.example.matteo.app1.R.attr.icon;

public class ActivityTwo extends AppCompatActivity {

    private Button mFirebaseBtn;
    private DatabaseReference mDatabase;
    private TextView mtextView;
    private TextView identificativoVeicolo;

    private TextView nome;
    private TextView strada;
    private TextView codice;
    private TextView descrizione;

    private EditText mNameFiled;
    private EditText mEmailField;

    private DatabaseReference ref;

    public String id;


    protected NfcAdapter nfcAdapter;
    protected PendingIntent pendingIntent;
    protected IntentFilter[] intentFilters;
    protected ToggleButton tglReadWrite;
    protected EditText txtTagContent;
    protected String tagContent;
    protected byte[] language;
    String indirizzo = "";
    String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);

        id = "";

        mFirebaseBtn = (Button) findViewById(R.id.firebase_btn);
        identificativoVeicolo = (TextView) findViewById(R.id.textView);
        nome = (TextView) findViewById(R.id.nome);
        strada = (TextView) findViewById(R.id.strada);
        codice = (TextView) findViewById(R.id.codice);
        descrizione = (TextView) findViewById(R.id.descrizione);
        //punta alla root

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        identificativoVeicolo.setText("Mike Sierra " + id);
        System.out.println(id);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Query lastQuery = mDatabase.child("Macchine").child(id).child("schede").orderByKey().limitToLast(1);

        mDatabase.child("Macchine").child(id).child("nome").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                identificativoVeicolo.setText(dataSnapshot.getValue().toString().trim());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        System.out.println("activity two");
        System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°" + lastQuery);

        lastQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cleanField();
                name = indirizzo = "";
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    name += " ";

                    System.out.println(snap.getKey());
                    String key=snap.getKey();
                    descrizione.setText(snap.child("descrizioneEvento").getValue().toString());

                    name += snap.child("first_name").getValue().toString();
                    name += ", " + snap.child("last_name").getValue().toString();

                    codice.setText(snap.child("codice").getValue().toString());

                    indirizzo += snap.child("street").getValue().toString();
                    indirizzo += " " + snap.child("house_number").getValue().toString();
                    indirizzo += ", " + snap.child("city").getValue().toString();

                    strada.setText(indirizzo);
                    nome.setText(name);



                    if (snap.child("primo").getValue().toString().trim().equals("true")) {
                        System.out.println("dentro");
                            sendNotification();
                            setPrimoFalse(key);
                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Qualche tipo di errore");
            }
        });

        strada.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Toast.makeText(ActivityTwo.this, "onText", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void cleanField() {
        strada.setText("");
        nome.setText("");
        descrizione.setText("");
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
    }

    public void setPrimoFalse(String key) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Macchine").child(id).child("schede").child(key).child("primo");
        System.out.println(ref);
        ref.setValue("false");
    }

    public void sendNotification() {
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!")
                        .setContentIntent(pendingIntent);
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL);
        mBuilder.setSound(alarmSound);
        mNotifyMgr.notify(1, mBuilder.build());
    }

}


