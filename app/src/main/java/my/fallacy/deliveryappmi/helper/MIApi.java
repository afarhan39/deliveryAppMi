package my.fallacy.deliveryappmi.helper;

import java.util.ArrayList;

import io.reactivex.Observable;
import my.fallacy.deliveryappmi.model.Delivery;
import retrofit2.http.GET;

public interface MIApi {

    //Active API
    @GET("deliveries")
    Observable<ArrayList<Delivery>> getDeliveries();
}
