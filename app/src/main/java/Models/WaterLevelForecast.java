package Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by matteo on 02/06/17.
 */

public class WaterLevelForecast {


    @SerializedName("DATA_PREVISIONE")
    @Expose
    private String data_previsionale;
    @SerializedName("DATA_ESTREMALE")
    @Expose
    private String data_estremale;
    @SerializedName("TIPO_ESTREMALE")
    @Expose
    private String tipo_estremale;
    @SerializedName("VALORE")
    @Expose
    private String valore;
    private String unita_misura=" cm";


    public WaterLevelForecast(JSONObject jsonObject) {
    }

    public WaterLevelForecast() {
    }

    public WaterLevelForecast(String data_previsionale, String valore) {
        this.data_previsionale = data_previsionale;
        this.valore = valore;
    }

    public WaterLevelForecast(String data_previsionale, String data_estremale, String tipo_estremale, String valore, String unita_misura) {
        this.data_previsionale = data_previsionale;
        this.data_estremale = data_estremale;
        this.tipo_estremale = tipo_estremale;
        this.valore = valore;
        this.unita_misura = unita_misura;
    }


    public String getData_previsionale() {
        return data_previsionale;
    }

    public void setData_previsionale(String data_previsionale) {
        this.data_previsionale = data_previsionale;
    }

    public String getData_estremale() {
        return data_estremale;
    }

    public void setData_estremale(String data_estremale) {
        this.data_estremale = data_estremale;
    }

    public String getTipo_estremale() {
        return tipo_estremale;
    }

    public void setTipo_estremale(String tipo_estremale) {
        this.tipo_estremale = tipo_estremale;
    }

    public String getValore() {
        return valore+getUnita_misura();
    }

    public void setValore(String valore) {
        this.valore = valore;
    }

    public String getUnita_misura() {
        return unita_misura;
    }

    public void setUnita_misura(String unita_misura) {
        this.unita_misura = unita_misura;
    }

    @Override
    public String toString() {
        return "WaterLevelForecast{" +
                "data_previsionale='" + data_previsionale + '\'' +
                ", data_estremale='" + data_estremale + '\'' +
                ", tipo_estremale='" + tipo_estremale + '\'' +
                ", valore='" + valore + '\'' +
                ", unita_misura='" + unita_misura + '\'' +
                '}';
    }
}

