package Controller;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by matteo on 17/05/17.
 */

public class InternetConnessionChecker {

    private boolean isAvailable;
    private Context context;

    /**
     * Necessita del context perchè poi verrano utilizzati metodi che lo richiedono come argomento
     * @param context
     */
    public InternetConnessionChecker(Context context) {
        this.context = context;
    }

    /**
     *Class that answers queries about the state of network connectivity.
     * It also notifies applications when network connectivity changes.
     * Get an instance of this class by calling Context.getSystemService(Context.CONNECTIVITY_SERVICE).
     * Ref: https://developer.android.com/reference/android/net/ConnectivityManager.html
     * @param context
     */
    private void haveNetworkConnection(Context context) {
        isAvailable = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

        /**
         * In qualche maniera prende le informazioni dal connectivity manager
         */

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                isAvailable = true;
                Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                isAvailable = true;
                Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * This method just return if a connection is available
     * @return
     */
    public boolean isConnectionAvailable() {
        haveNetworkConnection(context);
        return isAvailable;
    }


}
