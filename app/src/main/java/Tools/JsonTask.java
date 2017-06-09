package Tools;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by matteo on 04/06/17.
 */

/**
 * The three types used by an asynchronous task are the following:
 *  Params, the type of the parameters sent to the task upon execution.
 *  Progress, the type of the progress units published during the background computation.
 *  	android.os.AsyncTask<Params, Progress, Result>
 *  See more at: https://developer.android.com/reference/android/os/AsyncTask.html
 */
public class JsonTask extends AsyncTask<String, String, String> {

    ProgressDialog pd;
    String url = "http://dati.venezia.it/sites/default/files/dataset/opendata/previsione.json";
    String myResult="";

    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected String doInBackground(String... params) {
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;

        try {
            URL url = new URL(params[0]);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            InputStream inputStream = httpURLConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + "\n");
                Log.d("Response: ", "> " + line);
            }
            return stringBuffer.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        System.out.println("Result" + result);
        myResult = result;
        System.out.println("Result" + myResult);
    }


    public String getResult(){
        return myResult;
    }
}
