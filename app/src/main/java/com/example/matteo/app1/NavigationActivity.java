package com.example.matteo.app1;

import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.NotificationCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.MapsInitializer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import Fragments.SchedaInterventoFragment;
import Fragments.WaterLevelForecastFragment;
import Models.SchedaIntervento;
import Tools.AESHelper;
import Tools.HTTPHelper;

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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
    private String addressToSend = "Piazza Ferretto Mestre";
    private String child_macchine = "Macchine";
    private String child_schede = "schede";
    private String child_nominativo = "nome";
    private SchedaIntervento schedaIntervento;
    private TextView myConsoleView;
    private Animation animation = null;
    private AESHelper aesHelper;
    private LinearLayout scrollView;
    private ClipData.Item item; //TENTATIVO DI FARE IL LOGOUT

    protected NfcAdapter nfcAdapter;
    protected PendingIntent pendingIntent;
    protected IntentFilter[] intentFilters;
    protected ToggleButton tglReadWrite;
    protected EditText txtTagContent;
    protected String tagContent;
    protected byte[] language;
    protected String indirizzo = "";
    protected String name = "";
    protected MyMainReceiver myMainReceiver;
    protected Context context;
    protected Button btn_operativo;
    protected Button btn_smontante;

    public String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);


        /**
         * In teoria bisognerebbe evitare di svolegere alcune operazioni nel main thread, specialmente richieste sincrone
         * che potrebbero bloccarlo.
         * Tuttavia l'helper non bloccherà mai il thread perchè in caso di mancata di risposta dal web service perchè
         * non raggiungibile (non perchè non prevede la callback) questo lancerà un'eccezione opportunamente gestita.
         * In questo modo ho ritenuto accettabile il compromesso di fare la chiamata al web service nel main thread.
         */
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //AUTOGENERATED
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        schedaIntervento = new SchedaIntervento();
        aesHelper = new AESHelper();
        scrollView = (LinearLayout) findViewById(R.id.consoleScrollView);
        myConsoleView = (TextView) findViewById(R.id.consoleText);

        addStringToConsole("Waiting for status...");

        if (findViewById(R.id.fragment_container) != null) {
            WaterLevelForecastFragment f1 = new WaterLevelForecastFragment();
            android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, f1);
            ft.commit();
        }

        id = "";
        setComponents(); //Method which permits to have a clearer onCreate, just get resources from the xml

        //Just to have a context variable, never used
        context = this.context;

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        schedaIntervento.setAes_key(id); //Set key to encrypt and decrypt data
        schedaIntervento.setId_destinatario(id);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "RX Registred", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                try {
                    schedaIntervento.encryptAll(aesHelper);
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }

                Gson gson = new Gson();
                String myJson = gson.toJson(schedaIntervento);
                System.out.println(myJson);

                addStringToConsole("Json sent: " + myJson);
                addStringToConsole("Indirizzo passato: " + addressToSend);
                addStringToConsole(HTTPHelper.httpPostRequest(NavigationActivity.this, schedaIntervento));
            }
        });

        //AUTOGENERATED
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        identificativoVeicolo.setText("Mike Sierra " + id);

        btn_operativo = (Button) findViewById(R.id.btn_operativo);
        btn_smontante = (Button) findViewById(R.id.btn_deny);

        //TAKE THE REFERENCE TO REMOTE FIREBASE REALTIME DATABASE BASED ON NODEJS AND SOCKET.IO
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

        //BUTTON BLINKER
        animation = new AlphaAnimation(1, 0);                   // Change alpha from fully visible to invisible
        animation.setDuration(500);                             // duration - half a second
        animation.setInterpolator(new LinearInterpolator());    // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE);           // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE);             // Reverse animation at the end so the button will fade back in
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
                Toast.makeText(NavigationActivity.this, databaseError.getDetails().toString().trim(), Toast.LENGTH_SHORT).show();
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
        addStringToConsole("onStopDelBottone");

        super.onStop();
    }


    /*****************************BROADCAST*****RECEIVER****FROM****SERVICE**************+++++++++*/
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

                schedaIntervento.setNome(intent.getStringExtra("nome"));
                schedaIntervento.setIndirizzo(intent.getStringExtra("indirizzo"));
                schedaIntervento.setDescrizione(intent.getStringExtra("descrizione"));
                schedaIntervento.setCodice(intent.getStringExtra("codice").toString().trim());
                schedaIntervento.setCognome(intent.getStringExtra("cognome").toString().trim());
                schedaIntervento.setComune(intent.getStringExtra("comune").toString().trim());
                schedaIntervento.setVia(intent.getStringExtra("via").toString().trim());
                schedaIntervento.setCivico(intent.getStringExtra("civico").toString().trim());

                //SET THE ADDERESS WHERE TO GO
                addressToSend = intent.getStringExtra("indirizzo");

                addStringToConsole(addressToSend);

                nome.setText(schedaIntervento.getNome());
                strada.setText(schedaIntervento.getIndirizzo());
                codice.setText(schedaIntervento.getCodice());
                descrizione.setText(schedaIntervento.getDescrizione());
/*                if (schedaIntervento.getPrimo().equals("true")) {addStringToConsole("Response needed");addStringToConsole("Call managed yet");}*/

                System.out.println(string_from_service);
            }
        }
    }


    public void StartService(View v) {
        Intent intent = new Intent(this, CheckUpdateService.class);
        intent.putExtra("id", id);

        btn_operativo.setBackgroundColor(getResources().getColor(R.color.colorOperativo));
        btn_smontante.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        addStringToConsole("Status: operativo\n Get ready for the next response");

        btn_operativo.startAnimation(animation);
        btn_smontante.clearAnimation();
        //qua viene startato il service
        startService(intent);

    }

    public void StopService(View view) {
        Intent intent = new Intent(this, CheckUpdateService.class);
        btn_operativo.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        btn_smontante.setBackgroundColor(getResources().getColor(R.color.colorSmontante));
        addStringToConsole("Status: smontante\n Call won't be received");

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            Intent intent = new Intent(NavigationActivity.this, SocketIOActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(NavigationActivity.this, MapsActivity.class);
            intent.putExtra("address", addressToSend);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(NavigationActivity.this, ToolsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

            SchedaInterventoFragment newFragment = new SchedaInterventoFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack if needed
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void addStringToConsole(String str) {
      /*  myConsoleView.setText(myConsoleView.getText() + "\n" + str);
        scrollView.post(new Runnable() {
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });*/


        myConsoleView.setText(str.trim());
    }


}
