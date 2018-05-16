package my.fallacy.deliveryappmi.helper;

import android.content.Context;
import android.support.annotation.NonNull;

import com.alkathirikhalid.util.Connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import my.fallacy.deliveryappmi.BuildConfig;
import my.fallacy.deliveryappmi.model.Delivery;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MIHelper {

    private MIApi middleWareRetrofit;

    public MIHelper(Context context) {
        OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.interceptors().add(httpLoggingInterceptor);
        }
        httpClient.interceptors().add(new TokenInterceptor(context));

        middleWareRetrofit = new Retrofit.Builder()
                .baseUrl("https://staging.massiveinfinity.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient.build())
                .build()
                .create(MIApi.class);
    }

    public Observable<ArrayList<Delivery>> getDeliveries() {
        return middleWareRetrofit.getDeliveries();
    }

    private class TokenInterceptor implements Interceptor {
        private final Context context;

        private TokenInterceptor(Context context) {
            this.context = context;
        }

        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            if (!Connection.isConnected(context))
                throw new NoInternetException();

            return chain.proceed(chain.request());
        }
    }

    public class NoInternetException extends IOException {
        public NoInternetException() {
        }
    }
}
