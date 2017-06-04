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

import java.util.ArrayList;

import Models.WaterLevelForecast;

/**
 * Created by matteo on 04/06/17.
 */

public class SchedaInterventoAdapter extends ArrayAdapter<WaterLevelForecast> {
    private final Activity context;
    private ArrayList products;
    private static final String TAG = "ViewHolder";


    static class ViewHolder{
        public ImageView image;
        public TextView name;
        public TextView description;
    }

    public SchedaInterventoAdapter (Activity context, ArrayList products){
        super(context, R.layout.row_layout, products);
        this.context=context;
        this.products=products;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ForecastArrayAdapter.ViewHolder viewHolder;

        WaterLevelForecast p = (WaterLevelForecast)products.get(position);

        // reuse view: ViewHolder pattern
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.row_layout, null);
            // configure view holder
            viewHolder = new ForecastArrayAdapter.ViewHolder();
            viewHolder.name = (TextView) rowView.findViewById(R.id.name);
            viewHolder.description = (TextView) rowView.findViewById(R.id.description);
            viewHolder.image = (ImageView) rowView
                    .findViewById(R.id.icon);
            // take memory of the view
            rowView.setTag(viewHolder);
            // Log steTag()
            Log.d(TAG, "setTag() for object in position: " + position + "; data estremale: " + p.getData_estremale());
        } else {
            // reuse the object
            viewHolder = (ForecastArrayAdapter.ViewHolder) rowView.getTag();
            // Log steTag()
            Log.d(TAG, "getTag() for object in position: " + position);
        }
        viewHolder.image.setImageResource(R.mipmap.wave);
        viewHolder.name.setText(p.getData_previsionale());
        viewHolder.description.setText(p.getValore());
        return rowView;
    }
}
