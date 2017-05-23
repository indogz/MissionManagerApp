package com.example.matteo.app1;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by matteo on 23/05/17.
 */

public class CheckUpdateService extends IntentService {


    static boolean stop = false;

    public CheckUpdateService() {
        super("CheckUpdateService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        doStuff();
    }

    public static void doStuff() {
        int n = 0;
        while (true) {
            if (stop == true) {
                return;
            }
            Log.i("PROVA SERVICE", "Evento n." + n++);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }

    }

    public static void forceToStop() {
        stop = true;
    }

    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        Log.i("PROVA SERVICE", "Distruzione service");
    }


}
