package my.fallacy.deliveryappmi;

import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, DeliveryAdapter.DeliveryAdapterListener {

    private GoogleMap mMap;
    private DeliveryAdapter deliveryAdapter;
    private Realm realm;

    private MIHelper miHelper;
    private CompositeDisposable compositeDisposable;

    @Nullable
    @BindView(R.id.sliding_layout)
    protected SlidingUpPanelLayout slidingUpPanelLayout;

    @Nullable
    @BindView(R.id.viewDragView)
    protected View viewDragView;

    @Nullable
    @BindView(R.id.tv1)
    protected TextView tv1;

    @BindView(R.id.rvDelivery)
    protected RecyclerView rvDelivery;

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

    @OnClick(R.id.btnRefresh)
    void onRefresh() {
        callApi();
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


        initRealm();
        callApi();
        initSlideLayout();
    }

    private void initRealm() {
        Realm.init(this);
        realm = Realm.getDefaultInstance();
    }

    private void callApi() {
        miHelper = new MIHelper(this);
        compositeDisposable = new CompositeDisposable();


        DisposableObserver<ArrayList<Delivery>> disposableObserver = new DisposableObserver<ArrayList<Delivery>>() {
            @Override
            public void onNext(ArrayList<Delivery> deliveries) {
                if (!deliveries.isEmpty()) {
                    writeToRealm(deliveries);
                    addMarkers(deliveries);
                }
                else
                    Toast.makeText(MapsActivity.this, "Empty data", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                // Clear disposable container
                Toast.makeText(MapsActivity.this, "Something went wrong. Reload from db", Toast.LENGTH_SHORT).show();
                readFromRealm();
                compositeDisposable.clear();
            }

            @Override
            public void onComplete() {
                // Clear disposable container
                compositeDisposable.clear();
            }
        };
        miHelper.getDeliveries().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
        compositeDisposable.add(disposableObserver);
    }

    private void writeToRealm(final ArrayList<Delivery> deliveries) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                bgRealm.insertOrUpdate(deliveries);
            }
        });
    }

    private void readFromRealm() {
        //fetching the data
        RealmResults<Delivery> results = realm.where(Delivery.class).findAllAsync();
        results.load();

        ArrayList<Delivery> deliveries = new ArrayList<>();
        deliveries.addAll(realm.copyFromRealm(results));

        if (!deliveries.isEmpty()) {
            addMarkers(deliveries);
        }
    }

    private void addMarkers(ArrayList<Delivery> deliveries) {
        for (Delivery delivery : deliveries) {
            LatLng latLng = new LatLng(delivery.getLocation().getLat(), delivery.getLocation().getLng());
            mMap.addMarker(new MarkerOptions().position(latLng).title(delivery.getDescription()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
        }

        setupAdapter(deliveries);
    }

    private void initSlideLayout() {
        if (slidingUpPanelLayout != null) {
            slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
                @Override
                public void onPanelSlide(View panel, float slideOffset) { }

                @Override
                public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                    switch (newState) {
                        case COLLAPSED:
                            assert tv1 != null;
                            tv1.setText("Show List");
                            break;
                        case EXPANDED:
                            assert tv1 != null;
                            tv1.setText("List of Deliveries");
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }

    private void setupAdapter(ArrayList<Delivery> deliveries) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvDelivery.setLayoutManager(linearLayoutManager);
        deliveryAdapter = new DeliveryAdapter(this, deliveries, this);
        rvDelivery.setAdapter(deliveryAdapter);
    }


    @Override
    public void onCopyRightClicked(int position, Delivery delivery) {
        //todo close slider and zoom to location
        Toast.makeText(this, delivery.getLocation().getAddress(), Toast.LENGTH_SHORT).show();

        if (slidingUpPanelLayout != null) {
            if (slidingUpPanelLayout.getPanelState().equals(SlidingUpPanelLayout.PanelState.EXPANDED)) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        }
    }
}
