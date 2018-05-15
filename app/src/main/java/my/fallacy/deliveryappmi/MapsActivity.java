package my.fallacy.deliveryappmi;

import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<Delivery> deliveries = new ArrayList<>();

    @Nullable
    @BindView(R.id.sliding_layout)
    protected SlidingUpPanelLayout slidingUpPanelLayout;

    @Nullable
    @BindView(R.id.viewDragView)
    protected View viewDragView;

    @Nullable
    @BindView(R.id.tv1)
    protected TextView tv1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_neo);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getDeliveries();
        addMarkers();
    }

    private void getDeliveries() {
        Location location1 = new Location(1.3520156, 103.9638572, "Changi");
        Delivery delivery1 = new Delivery("Deliver documents to Tracy",
                "https://s3-ap-southeast-1.amazonaws.com/mibackpack/map-marker-2-128.png",
                location1);

        Location location2 = new Location(1.301805, 103.8356084, "Changi");
        Delivery delivery2 = new Delivery("Deliver parcel to Vea",
                "https://s3-ap-southeast-1.amazonaws.com/mibackpack/map-marker-2-128.png",
                location2);

        deliveries.add(delivery1);
        deliveries.add(delivery2);
    }

    private void addMarkers() {
        for (Delivery delivery : deliveries) {
            LatLng latLng = new LatLng(delivery.getLocation().getLat(), delivery.getLocation().getLng());
            mMap.addMarker(new MarkerOptions().position(latLng).title(delivery.getDescription()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

            if (tv1 != null)
            tv1.setText(delivery.getDescription());
        }
    }
}
