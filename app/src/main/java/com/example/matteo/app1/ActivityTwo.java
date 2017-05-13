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
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Macchine").child("5");


        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        identificativoVeicolo.setText("Mike Sierra " + id);
        System.out.println("activity two");

        //mDatabase.child("5");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                cleanField();

                name=indirizzo="";
                for (DataSnapshot snap : dataSnapshot.getChildren()) {

                    System.out.println(snap);

                    if (snap.getKey().equals("descrizioneEvento")) {
                        descrizione.setText(snap.getValue().toString().trim());
                    }

                    if (snap.getKey().equals("first_name")) {
                        name += snap.getValue().toString().trim();
                        name += " ";
                    }
                    if (snap.getKey().equals("last_name")) {
                        name += snap.getValue().toString().trim();
                        name += " ";
                    }

                    if (snap.getKey().equals("codice")) {
                        codice.setText(snap.getValue().toString().trim());
                    }

                    if (snap.getKey().equals("street")) {
                        indirizzo += snap.getValue().toString().trim();
                        indirizzo += " ";
                    }
                    if (snap.getKey().equals("house_number")) {
                        indirizzo += snap.getValue().toString().trim();
                        indirizzo += " ";
                    }
                    if (snap.getKey().equals("city")) {
                        indirizzo += snap.getValue().toString().trim();
                        indirizzo += " ";
                    }
                    System.out.println(indirizzo);
                    strada.setText(indirizzo);
                    nome.setText(name);

                    if (snap.getKey().equals("primo")) {
                        if (snap.getValue().toString().trim().equals("true")) {
                            sendNotification();
                            setPrimoFalse();
                        }

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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




/*
        mtextView.addTextChangedListener(new TextWatcher() {

        String name = mNameFiled.getText().toString().trim();
                String email = mNameFiled.getText().toString().trim();

                HashMap <String, String> dataMap = new HashMap<String, String>();

                dataMap.put("Name", name);
                dataMap.put("Email", email);

                //create a child in root obj
                //assign some value to that child
                mDatabase.push().setValue(dataMap).addOnCompleteListener(new OnCompleteListener<Void>(){
                    public void onComplete(@NonNull Task<Void> task){
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Stored", Toast.LENGTH_SHORT).show();
                            mtextView.setText("Text");
                        } else{
                            Toast.makeText(MainActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                da qui metodo vero
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Toast.makeText(MainActivity.this, "Before", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Toast.makeText(MainActivity.this, "onText", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void afterTextChanged(Editable s) {
                Toast.makeText(MainActivity.this, "After", Toast.LENGTH_SHORT).show();

            }
        });*/
    }
    public void cleanField(){
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

    public void setPrimoFalse() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Macchine").child(id).child("primo");
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


