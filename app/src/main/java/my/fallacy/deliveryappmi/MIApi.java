package my.fallacy.deliveryappmi;

import java.util.ArrayList;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;

public interface MIApi {

    //Active API
    @GET("deliveries")
    Observable<ArrayList<Delivery>> getDeliveries();
}
