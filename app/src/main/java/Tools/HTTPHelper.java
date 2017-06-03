package Tools;

import android.content.Context;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by matteo on 28/05/17.
 */

@SuppressWarnings("deprecation")
public class HTTPHelper {

    private OkHttpClient okHttpClient;
    private Request request;
    private RequestBody requestBody;
    private static String url = "http://www.ripasso.altervista.org/request.php?";


    public static String httpPostRequest(Context context, String data) {
        HttpClient httpclient = new DefaultHttpClient();
        System.out.println("Client creato");
        HttpPost httppost = new HttpPost(url);
        System.out.println("HttpPost creato");

        try {
            /**
             * NameValuePair is a special <Key, Value> pair which is used to represent parameters
             * in http request, i.e. www.example.com?key=value
             * .Technically it's like a Map based on a List
             */
            
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            System.out.println("Lista creata");
            nameValuePairs.add(new BasicNameValuePair("nome", data.trim()));
            System.out.println("Lista aggiornata");

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            System.out.println("Entita' settata");
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            System.out.println("Richiesta eseguita");

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            System.out.println("ECCEZIONE NUMERO UNO");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("ECCEZIONE NUMERO DUE");
        }

        return "Fino alla fine ci è arrivato";

    }
}
