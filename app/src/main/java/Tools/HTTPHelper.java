package Tools;

import android.renderscript.Sampler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;

import Controller.RetrofitService;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.HTTP;

/**
 * Created by matteo on 28/05/17.
 */

public class HTTPHelper {

    //private OkHttpClient okHttpClient;
    //private Request request;
    //private RequestBody requestBody;


    private RetrofitService mService;


    public HTTPHelper(){
        mService= RetrofitService.retrofit.create(RetrofitService.class);

    }

}
