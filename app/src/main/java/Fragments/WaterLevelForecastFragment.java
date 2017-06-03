package Fragments;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
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

    ArrayList products;
    String myJsonForecast = "";
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
        Toast.makeText(getActivity(), "Item: " + position + "\nname: " + ((WaterLevelForecast) products.get(position)).getData_previsionale() + "\n" +
                "description: " + ((WaterLevelForecast) products.get(position)).getValore(), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        products = new ArrayList<>();
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
         * SE BECCO IL DEMENTE CHE HA FATTO I JSON DEL COMUNE LO FACCIO DIVENTARE INTELLIGENTE
         * A SUON DI JSON SULLE GENGIVE PORCO DIO
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
                products.add(waterLevelForecast);
            }
            System.out.println(products.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }


        products.add(new WaterLevelForecast("data previsionale", "valore previsionale"));


        ForecastArrayAdapter adapter = new ForecastArrayAdapter(getActivity(), products);

        setListAdapter(adapter);

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
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)
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

/*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_patient_emergency_story, container, false);

        textView = (TextView) view.findViewById(R.id.fragmentPatientView);
        textView.setText("Sembrerebbe funzionare");

        String url = "http://dati.venezia.it/sites/default/files/dataset/opendata/previsione.json";
        new JsonTask().execute(url);


        return view;
    }

    TextView textView;
    ProgressDialog pd;





    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     *//*
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public static WaterLevelForecastFragment newInstance(String param1, String param2) {
        WaterLevelForecastFragment fragment = new WaterLevelForecastFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

}

