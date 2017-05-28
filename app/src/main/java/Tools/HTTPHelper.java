package Tools;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by matteo on 28/05/17.
 */

public class HTTPHelper {

    private OkHttpClient okHttpClient;
    private Request request;
    private RequestBody requestBody;


    public HTTPHelper(){
        okHttpClient=new OkHttpClient();
        request= new Request.Builder()
                .url("http://www.ripasso.altervista.org/request.php?")
                .build();
    }




}
