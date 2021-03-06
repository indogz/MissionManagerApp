package Fragments;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.matteo.app1.NavigationActivity;
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
    protected SchedaIntervento schedaIntervento, sa;


    /**
     * Il design pattern Viewholder permette di accedere ad ogni elemento di una lista senza che ci
     * sia il bisogno di ricaricarla ad ogni scroll. In pratica evita di richiamare più volte findViewById()
     * e l’inflater per aggiornare il layout e ridurre notevolmente il lag.
     * Si realizza quindi una sorta di cache per l’inflater.
     * Passi per applicare il pattern:
     * 1. Si crea una classe statica o enumerazione che andrà a contenere gli oggetti della lista
     * 2. La prima volta che viene caricato il layout bisogna chiamare l’inflater per istanziare il
     *    layout ed ottenere l’oggetto View derivante. Una volta collegate le referenze della classe
     *    agli oggetti de layout tramite findViewById(), impostiamo il ViewHolder come tag.
     * 3. Successivamente il layout non è null e quindi vuoto perciò non si deve richiamare l’inflater, ma possiamo accedere ai campi della classe statica. In questo caso ottengo l’oggetto istanziato tramite getTag() e procedo come desidero.
     */


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

        sa=((NavigationActivity)getActivity()).schedaIntervento;


        Toast.makeText(getActivity(), sa.getCognome(), Toast.LENGTH_SHORT).show();
        schedaList = new ArrayList<>();
        mService = RFService.retrofit.create(RFService.class);
        String s = "";


        String url = "http://ripasso.altervista.org/getEncodedScheda.php?nome="+sa.getCognome().toLowerCase().trim();

        /**
         * RIPRENDO UN JSON PER CUI DEVO METTERE NELL'URL I PARAMETRI
         */
        try {
            s = new JsonTask()
                    .execute(url)
                    .get();
        } catch (Exception e) {
            System.out.println("Eccezione");
        }
        System.out.println("Received: " + s);


        /**
         * I JSON DEL COMUNE NON SONO VALIDI, HO DOVUTO MODIFICARLI
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

