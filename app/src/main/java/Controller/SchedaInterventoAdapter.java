package Controller;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matteo.app1.R;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import Models.SchedaIntervento;
import Models.WaterLevelForecast;

/**
 * Created by matteo on 04/06/17.
 */

public class SchedaInterventoAdapter extends ArrayAdapter<SchedaIntervento> {
    private final Activity context;
    private ArrayList products;
    private static final String TAG = "ViewHolder";


    static class ViewHolder {
        public ImageView image;
        public TextView name;
        public TextView description;
        public TextView id_mezzo;
        public TextView descrizione;

    }

    public SchedaInterventoAdapter(Activity context, ArrayList products) {
        super(context, R.layout.fragment_patient_emergency_story, products);
        this.context = context;
        this.products = products;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        SchedaInterventoAdapter.ViewHolder viewHolder;

        SchedaIntervento p = (SchedaIntervento) products.get(position);

        // reuse view: ViewHolder pattern
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.fragment_patient_emergency_story, null);
            // configure view holder
            viewHolder = new SchedaInterventoAdapter.ViewHolder();
            viewHolder.name = (TextView) rowView.findViewById(R.id.name);
            viewHolder.description = (TextView) rowView.findViewById(R.id.description);
            viewHolder.image = (ImageView) rowView.findViewById(R.id.icon);
            viewHolder.id_mezzo = (TextView) rowView.findViewById(R.id.id_mezzo);
            viewHolder.descrizione = (TextView) rowView.findViewById(R.id.descrizione);


            // take memory of the view
            rowView.setTag(viewHolder);
            // Log steTag()
            Log.d(TAG, "setTag() for object in position: " + position + "; data estremale: " + p.getCodice());
        } else {
            // reuse the object
            viewHolder = (SchedaInterventoAdapter.ViewHolder) rowView.getTag();
            // Log steTag()
            Log.d(TAG, "getTag() for object in position: " + position);
        }

        try {
            p.decryptAll(p.getAes_key());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        viewHolder.image.setImageResource(R.mipmap.wave);
        viewHolder.name.setText(p.getNome());
        viewHolder.description.setText("Rensponder: " + p.getAes_key());
        viewHolder.descrizione.setText(p.getDescrizione());
        viewHolder.id_mezzo.setText(p.getDataOra());

        return rowView;
    }
}
