package Controller;

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

    /**
     * Necessita del context perchè poi verrano utilizzati metodi che lo richiedono come argomento
     *
     * @param context
     */
    public NfcConnectionChecker(Context context) {
        this.context = context;
    }


    private void haveNetworkConnection(Context context) {
        isAvailable = false;

        /**
         * Nfc manager si istanzia mediante getSystemService del context
         */
        NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);

        /**
        * If your application uses NFC functionality, but that functionality is not crucial to your
        * application, you can omit the uses-feature element and check for NFC avalailbility at runtime
        * by checking to see if getDefaultAdapter() is null.
        */

        NfcAdapter adapter = manager.getDefaultAdapter();
        if (adapter != null && adapter.isEnabled()) {
            //Yes NFC available
            isAvailable = true;
        } else {
            //Your device doesn't support NFC
            isAvailable = false;
        }
    }

    public boolean isConnectionAvailable() {
        haveNetworkConnection(context);
        return isAvailable;
    }

}
