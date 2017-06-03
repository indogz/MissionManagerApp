package com.example.matteo.app1;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import Models.SchedaIntervento;

/**
 * Created by matteo on 23/05/17.
 */

public class CheckUpdateService extends Service {


    private DatabaseReference mDatabase;
    private String child_macchine = "Macchine";
    private String child_schede = "schede";
    private String child_nominativo = "nome";
    private String name = "";
    private String indirizzo = "";
    public String s = "";
    public Query lastQuery;
    public ValueEventListener listener;

    //from MyService to MainActivity
    final static String KEY_INT_FROM_SERVICE = "KEY_INT_FROM_SERVICE";
    final static String KEY_STRING_FROM_SERVICE = "KEY_STRING_FROM_SERVICE";
    final static String ACTION_UPDATE_CNT = "UPDATE_CNT";
    final static String ACTION_UPDATE_MSG = "UPDATE_MSG";

    //from MainActivity to MyService
    final static String KEY_MSG_TO_SERVICE = "KEY_MSG_TO_SERVICE";
    final static String ACTION_MSG_TO_SERVICE = "MSG_TO_SERVICE";

    private String panicBotton = "";
    private MyServiceReceiver myServiceReceiver;
    private MyServiceThread myServiceThread;
    private int cnt;
    private Bundle bundle = new Bundle();
    private String identificativoVeicolo;
    public MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(getApplicationContext(), "onCreate", Toast.LENGTH_LONG).show();
        myServiceReceiver = new MyServiceReceiver();
        super.onCreate();
        System.out.println("GRAZIE A DIO VA OLTRE L'ON CREATE");
    }

    /**
     * Servirebbe per ricevere dati dall'activity ma al momento non viene utilizzata
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "onStartCommand", Toast.LENGTH_LONG).show();

        identificativoVeicolo = intent.getStringExtra("id").toString().trim();
        Toast.makeText(getApplicationContext(), identificativoVeicolo, Toast.LENGTH_LONG).show();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_MSG_TO_SERVICE);
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(myServiceReceiver, intentFilter);

        myServiceThread = new MyServiceThread();
        myServiceThread.start();

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "onDestroy", Toast.LENGTH_LONG).show();
        myServiceThread.setRunning(false);

        //Stacco il broadcast receiver
        unregisterReceiver(myServiceReceiver);

        myServiceThread.interrupt();
        myServiceThread = null;
        panicBotton = "";
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        lastQuery.removeEventListener(listener);

        stopSelf();
        super.onDestroy();
    }

    //private Intent resultIntent = new Intent(this, NavigationActivity.class);
    protected PendingIntent pendingIntent;//= PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    public void sendNotification() {

        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!")
                        .setContentIntent(pendingIntent);
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Builds the notification and issues it.
        //Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri alarmSound = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.alarm_two_tones);
        mBuilder.setSound(alarmSound);
        mNotifyMgr.notify(1, mBuilder.build());
    }


    /**************************BROADCAST***RECEIVER*********************************************************/

    public class MyServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action.equals(ACTION_MSG_TO_SERVICE)) {
                String msg = intent.getStringExtra(KEY_MSG_TO_SERVICE);

                msg = new StringBuilder(msg).reverse().toString();

                //send back to MainActivity
                Intent i = new Intent();
                i.setAction(ACTION_UPDATE_MSG);
                i.putExtra(KEY_STRING_FROM_SERVICE, msg);
                sendBroadcast(i);
            }

            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        System.out.println("Headset is unplugged");
                        if (panicBotton.equals("t")) {
                            panicBotton = "";
                            startPanicButton();
                        }
                        break;
                    case 1:
                        System.out.println("Headset is plugged");
                        panicBotton += "t";
                        System.out.println("MEEEEDDDDDDDDDDDIIIIIIIIAAAAAA " + mediaPlayer);
                        if (mediaPlayer != null) {
                            mediaPlayer.release();
                        }
                        break;
                    default:
                        System.out.println("I have no idea what the headset state is");
                }
                System.out.println("*************************** " + panicBotton);

            }
        }

        public void startPanicButton() {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.panic_button);
            mediaPlayer.start();
        }
    }


    /***************************************THREAD***************************************************/

    private class MyServiceThread extends Thread {

        private boolean running;
        String descrizione = "";
        String codice;

        public void setRunning(boolean running) {
            this.running = running;
        }

        @Override
        public void run() {
            cnt = 0;
            running = true;

            mDatabase = FirebaseDatabase.getInstance().getReference();
            lastQuery = mDatabase.child(child_macchine).child(identificativoVeicolo).child(child_schede).orderByKey().limitToLast(1);

            final SchedaIntervento schedaIntervento = new SchedaIntervento();
            listener = lastQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    name = indirizzo = "";

                    for (DataSnapshot snap : dataSnapshot.getChildren()) {

                        name += " ";
                        // System.out.println(snap.getKey());

                        descrizione = (snap.child("descrizioneEvento").getValue().toString());
                        schedaIntervento.setDescrizione(descrizione);

                        name += snap.child("first_name").getValue().toString();
                        name += ", " + snap.child("last_name").getValue().toString();
                        schedaIntervento.setNome(name);

                        //codice.setText(snap.child("codice").getValue().toString());

                        codice = (snap.child("codice").getValue().toString());
                        schedaIntervento.setCodice(codice);

                        indirizzo += snap.child("street").getValue().toString();
                        indirizzo += " " + snap.child("house_number").getValue().toString();
                        indirizzo += ", " + snap.child("city").getValue().toString();
                        schedaIntervento.setIndirizzo(indirizzo);

                        System.out.println("*****" + indirizzo);
                        System.out.println("*****" + codice);
                        System.out.println("*****" + descrizione);
                        System.out.println("*****" + name);

                        String key = snap.getKey();
                        if (snap.child("primo").getValue().toString().trim().equals("true")) {
                            System.out.println("dentro");
                            sendNotification();
                            //setPrimoFalse(key);
                        }
                    }

                    Intent intent = new Intent();
                    intent.setAction(ACTION_UPDATE_MSG);
                    intent.putExtra(KEY_STRING_FROM_SERVICE, descrizione);
                    intent.putExtra("indirizzo", schedaIntervento.getIndirizzo());
                    intent.putExtra("codice", schedaIntervento.getCodice());
                    intent.putExtra("descrizione", schedaIntervento.getDescrizione());
                    intent.putExtra("nome", schedaIntervento.getNome());
                    intent.putExtra("codice", schedaIntervento.getCodice());

                    sendBroadcast(intent);
                    System.out.println("Funziona" + indirizzo);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    databaseError.getMessage();
                }
            });

        }
    }


}


