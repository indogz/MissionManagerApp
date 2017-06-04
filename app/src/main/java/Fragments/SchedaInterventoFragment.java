package Fragments;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.matteo.app1.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import Controller.RFService;
import Controller.SchedaInterventoAdapter;
import Models.SchedaIntervento;
import Tools.JsonTask;


public class SchedaInterventoFragment extends ListFragment implements AdapterView.OnItemClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    ArrayList schedaList;
    //String myJsonForecast = "";
    SchedaInterventoAdapter adapter;
    private RFService mService;
    protected SchedaIntervento schedaIntervento;


    public SchedaInterventoFragment() {
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
        Toast.makeText(getActivity(), "Previsione n: " + position + "\nPrevista il: " + ((SchedaIntervento) schedaList.get(position)).getCodice() + "\n" +
                "description: " + ((SchedaIntervento) schedaList.get(position)).getCodice(), Toast.LENGTH_SHORT).show();

        ImageView imageView = (ImageView) view.findViewById(R.id.icon);


    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        schedaList = new ArrayList<>();
        mService = RFService.retrofit.create(RFService.class);
        String s = "";
        String url = "http://ripasso.altervista.org/getEncodedScheda.php?";
        try {
            s = new JsonTask()
                    .execute(url)
                    .get();
        } catch (Exception e) {
            System.out.println("Eccezione");
        }
        System.out.println("Received: " + s);


        /**
         * SE I JSON VENGONO FATTI BENE COME LI FACCIO IO POI I PROGRAMMATORI NON DEVONO BESTEMMIARE
         */
        try {

            System.out.println("Nuovo s " + s);

            JSONObject ob = new JSONObject(s);
            JSONArray arr = ob.getJSONArray("records");

            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);

                SchedaIntervento schedaIntervento = new SchedaIntervento();

                schedaIntervento.setNome(o.getString("nome"));
                schedaIntervento.setCognome(o.getString("cognome"));
                schedaIntervento.setCodice(o.getString("codice_colore"));
                schedaIntervento.setAes_key(o.getString("id_mezzo"));
                schedaIntervento.setDescrizione(o.getString("descrizione"));
                schedaIntervento.setDataOra(o.getString("orario"));


                schedaList.add(schedaIntervento);
            }
            System.out.println(schedaList.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }


        //schedaList.add(new SchedaIntervento("data previsionale", "valore previsionale"));

        adapter = new SchedaInterventoAdapter(getActivity(), schedaList);

        setListAdapter(adapter);

        adapter.getCount();

        getListView().setOnItemClickListener(this);
    }
}

