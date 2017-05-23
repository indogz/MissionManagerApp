package com.example.matteo.app1;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.widget.Toast;

/**
 * Created by matteo on 23/05/17.
 */

public class NfcConnectionChecker {

    private boolean isAvailable;
    private Context context;

    public NfcConnectionChecker(Context context) {

        this.context = context;
    }

    private void haveNetworkConnection(Context context) {
        isAvailable = false;
        NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        if (adapter != null && adapter.isEnabled()) {
            //Yes NFC available
            isAvailable=true;
        } else {
            //Your device doesn't support NFC
            isAvailable=false;
        }
    }

    public boolean isConnectionAvailable() {
        haveNetworkConnection(context);
        return isAvailable;
    }

}
