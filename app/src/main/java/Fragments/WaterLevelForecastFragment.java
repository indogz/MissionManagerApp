package Fragments;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matteo.app1.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import Controller.ForecastArrayAdapter;
import Controller.RFService;
import Models.WaterLevelForecast;

public class WaterLevelForecastFragment extends ListFragment implements AdapterView.OnItemClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    ArrayList forecstList;
    String myJsonForecast = "";
    ForecastArrayAdapter adapter;
    private RFService mService;
    protected WaterLevelForecast waterLevelForecast;


    public WaterLevelForecastFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(), "Previsione n: " + position + "\nPrevista il: " + ((WaterLevelForecast) forecstList.get(position)).getData_previsionale() + "\n" +
                "description: " + ((WaterLevelForecast) forecstList.get(position)).getValore(), Toast.LENGTH_SHORT).show();
        ImageView imageView = (ImageView) view.findViewById(R.id.icon);
        if (adapter.getItem(position).getTipo_estremale() != null && adapter.getItem(position).getTipo_estremale().equalsIgnoreCase("max")) {
            imageView.setImageResource(R.mipmap.arrowup);
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        forecstList = new ArrayList<>();
        mService = RFService.retrofit.create(RFService.class);
        String s = "";
        try {
            s = new JsonTask()
                    .execute(url)
                    .get();
        } catch (Exception e) {
            System.out.println("Eccezione");
        }
        System.out.println("Received: " + s);


        /**
         * I JSON DEL COMUNE NON SONO VALIDI, NECESSITANO DI MODIFICA COME SOTTO
         */
        try {
            s = s.replace("[", "{ \"dati\":[");
            s = s.replace("]", "]}");
            System.out.println("Nuovo s " + s);

            JSONObject ob = new JSONObject(s);
            JSONArray arr = ob.getJSONArray("dati");

            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);

                WaterLevelForecast waterLevelForecast = new WaterLevelForecast();
                waterLevelForecast.setTipo_estremale(o.getString("TIPO_ESTREMALE"));
                waterLevelForecast.setData_estremale(o.getString("DATA_PREVISIONE"));
                waterLevelForecast.setData_previsionale(o.getString("DATA_ESTREMALE"));
                waterLevelForecast.setValore(o.getString("VALORE"));
                System.out.println(i + " " + o.getString("VALORE"));
                forecstList.add(waterLevelForecast);
            }
            System.out.println(forecstList.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        forecstList.add(new WaterLevelForecast("data previsionale", "valore previsionale"));

        adapter = new ForecastArrayAdapter(getActivity(), forecstList);

        setListAdapter(adapter);

        adapter.getCount();

        getListView().setOnItemClickListener(this);
    }


    TextView textView;
    ProgressDialog pd;
    String url = "http://dati.venezia.it/sites/default/files/dataset/opendata/previsione.json";

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);
                }
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
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
            if (pd.isShowing()) {
                pd.dismiss();
            }
            System.out.println("Result" + result);
            myJsonForecast = result;
            System.out.println("Result" + myJsonForecast);
        }
    }
}

