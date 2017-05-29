package Controller;

import Models.SchedaIntervento;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * Created by matteo on 29/05/17.
 */

public interface RetrofitService {

    public static final String BASE_URL = "https://raw.githubusercontent.com/";

    @GET("opendatajson/football.json/master/2016-17/it.1.json")
    Call<SchedaIntervento> getPojo(); //get the Pojo object

    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
