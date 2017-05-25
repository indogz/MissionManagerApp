package com.example.matteo.app1;


import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.LauncherApps;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.SoundEffectConstants;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static android.R.attr.id;

/**
 * Created by matteo on 23/05/17.
 */

public class CheckUpdateService extends Service {

    private static final int ONGOING_NOTIFICATION_ID = 1;
    private DatabaseReference mDatabase;
    private String child_macchine = "Macchine";
    private String child_schede = "schede";
    private String child_nominativo = "nome";
    private String name = "";
    private String indirizzo = "";
    String s = "";
    Query lastQuery;
    ValueEventListener listener;

    //from MyService to MainActivity
    final static String KEY_INT_FROM_SERVICE = "KEY_INT_FROM_SERVICE";
    final static String KEY_STRING_FROM_SERVICE = "KEY_STRING_FROM_SERVICE";
    final static String ACTION_UPDATE_CNT = "UPDATE_CNT";
    final static String ACTION_UPDATE_MSG = "UPDATE_MSG";

    //from MainActivity to MyService
    final static String KEY_MSG_TO_SERVICE = "KEY_MSG_TO_SERVICE";
    final static String ACTION_MSG_TO_SERVICE = "MSG_TO_SERVICE";

    MyServiceReceiver myServiceReceiver;
    MyServiceThread myServiceThread;
    int cnt;
    Bundle bundle = new Bundle();
    private String identificativoVeicolo;

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


        //identificativoVeicolo=intent.getStringExtra("id").toString().trim();

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

        identificativoVeicolo=intent.getStringExtra("id").toString().trim();
        Toast.makeText(getApplicationContext(), identificativoVeicolo, Toast.LENGTH_LONG).show();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_MSG_TO_SERVICE);
        registerReceiver(myServiceReceiver, intentFilter);

        myServiceThread = new MyServiceThread();
        myServiceThread.start();

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "onDestroy", Toast.LENGTH_LONG).show();
        myServiceThread.setRunning(false);
        unregisterReceiver(myServiceReceiver);

        myServiceThread.interrupt();
        myServiceThread=null;

        lastQuery.removeEventListener(listener);

        stopSelf();
        super.onDestroy();
    }


    protected PendingIntent pendingIntent;
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
        }
    }


    /*******************THREAD**********************************************************************/

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


            listener=lastQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    name = indirizzo = "";


                    for (DataSnapshot snap : dataSnapshot.getChildren()) {

                        name += " ";


                        System.out.println(snap.getKey());

                        descrizione = (snap.child("descrizioneEvento").getValue().toString());

                        name += snap.child("first_name").getValue().toString();
                        name += ", " + snap.child("last_name").getValue().toString();

                        //codice.setText(snap.child("codice").getValue().toString());


                        codice = (snap.child("codice").getValue().toString());

                        indirizzo += snap.child("street").getValue().toString();
                        indirizzo += " " + snap.child("house_number").getValue().toString();
                        indirizzo += ", " + snap.child("city").getValue().toString();

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
                    intent.putExtra("indirizzo", indirizzo);
                    intent.putExtra("codice", codice);
                    intent.putExtra("descrizione", descrizione);
                    intent.putExtra("nome", name);
                    intent.putExtra("codice",codice);

                    sendBroadcast(intent);
                    System.out.println("Funziona" + indirizzo);                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    databaseError.getMessage();
                }
            });

        }
    }





}


