package com.example.matteo.app1;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
    protected String indirizzo = "";
    protected String name = "";

    private String child_macchine = "Macchine";
    private String child_schede = "schede";
    private String child_nominativo = "nome";

    protected MyMainReceiver myMainReceiver;
    protected Context context;

    protected Button btn_operativo;
    protected Button btn_smontante;

    Animation animation=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);

        id = "";
        setComponents();
        context = this.context;

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        identificativoVeicolo.setText("Mike Sierra " + id);

        btn_operativo = (Button) findViewById(R.id.btn_operativo);
        btn_smontante = (Button) findViewById(R.id.btn_deny);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        Query lastQuery = mDatabase.child(child_macchine).child(id).child(child_schede).orderByKey().limitToLast(1);

        mDatabase.child(child_macchine).child(id).child(child_nominativo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                identificativoVeicolo.setText(dataSnapshot.getValue().toString().trim());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        System.out.println("activity two");

        animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in

        btn_operativo.startAnimation(animation);


        lastQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //cleanField();
                name = indirizzo = "";
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                   /* name += " ";

                    System.out.println(snap.getKey());
                    String key = snap.getKey();
                    descrizione.setText(snap.child("descrizioneEvento").getValue().toString());

                    name += snap.child("first_name").getValue().toString();
                    name += ", " + snap.child("last_name").getValue().toString();

                    //5codice.setText(snap.child("codice").getValue().toString());

                    indirizzo += snap.child("street").getValue().toString();
                    indirizzo += " " + snap.child("house_number").getValue().toString();
                    indirizzo += ", " + snap.child("city").getValue().toString();

                    strada.setText(indirizzo);
                    nome.setText(name);
*/
                    String key = snap.getKey();
                    if (snap.child("primo").getValue().toString().trim().equals("true")) {
                        // System.out.println("dentro");
                        //sendNotification();
                        setPrimoFalse(key);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Qualche tipo di errore");
                Toast.makeText(ActivityTwo.this, databaseError.getDetails().toString().trim(), Toast.LENGTH_SHORT).show();
            }
        });

        strada.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Toast.makeText(ActivityTwo.this, "onText", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onStart() {
        myMainReceiver = new MyMainReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CheckUpdateService.ACTION_UPDATE_CNT);
        intentFilter.addAction(CheckUpdateService.ACTION_UPDATE_MSG);
        registerReceiver(myMainReceiver, intentFilter);

        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(myMainReceiver);
        Toast.makeText(getApplicationContext(), "onStopDelbottone", Toast.LENGTH_LONG).show();

        super.onStop();
    }


    private class MyMainReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();


            if (action.equals(CheckUpdateService.ACTION_UPDATE_CNT)) {
                int int_from_service = intent.getIntExtra(CheckUpdateService.KEY_INT_FROM_SERVICE, 0);
                System.out.println(int_from_service);
                codice.setText(String.valueOf(int_from_service));
            } else if (action.equals(CheckUpdateService.ACTION_UPDATE_MSG)) {
                String string_from_service = intent.getStringExtra(CheckUpdateService.KEY_STRING_FROM_SERVICE);

                String nominativo, ind, cod, descr;

                nominativo = intent.getStringExtra("nome");
                ind = intent.getStringExtra("indirizzo");
                cod = intent.getStringExtra("codice").toString().trim();
                descr = intent.getStringExtra("descrizione");

                System.out.println("Nome: " + nominativo);
                System.out.println("Indirizzo: " + ind);
                System.out.println("Descrizione: " + descr);


                nome.setText(nominativo);
                strada.setText(ind);
                codice.setText(cod);
                descrizione.setText(descr);


                System.out.println(string_from_service);

            }
        }
    }


    public void StartService(View v) {
        Intent intent = new Intent(this, CheckUpdateService.class);
        intent.putExtra("id",id);

        btn_operativo.setBackgroundColor(getResources().getColor(R.color.colorOperativo));
        btn_smontante.setBackgroundColor(Color.GRAY);

        btn_operativo.startAnimation(animation);
        btn_smontante.clearAnimation();
        startService(intent);

    }

    public void StopService(View view) {
        Intent intent = new Intent(this, CheckUpdateService.class);
        btn_operativo.setBackgroundColor(Color.GRAY);
        btn_smontante.setBackgroundColor(getResources().getColor(R.color.colorSmontante));

        btn_smontante.startAnimation(animation);
        btn_operativo.clearAnimation();

        stopService(intent);
    }


    public void cleanField() {
        strada.setText("");
        nome.setText("");
        descrizione.setText("");
    }

    public void setComponents() {
        mFirebaseBtn = (Button) findViewById(R.id.firebase_btn);
        identificativoVeicolo = (TextView) findViewById(R.id.textView);
        nome = (TextView) findViewById(R.id.nome);
        strada = (TextView) findViewById(R.id.strada);
        codice = (TextView) findViewById(R.id.codice);
        descrizione = (TextView) findViewById(R.id.descrizione);

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


