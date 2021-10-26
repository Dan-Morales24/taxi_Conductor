package com.example.taxi_conductor.ui.home;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.taxi_conductor.EventBus.DriverRequestRecived;
import com.example.taxi_conductor.EventBus.NotifyToRiderEvent;
import com.example.taxi_conductor.MapFragment;
import com.example.taxi_conductor.Model.RiderModel;
import com.example.taxi_conductor.Model.TripPlanModel;
import com.example.taxi_conductor.R;
import com.example.taxi_conductor.Remote.IGoogleAPI;
import com.example.taxi_conductor.Remote.RetrofitClient;
import com.example.taxi_conductor.Utils.ConductorUtils;
import com.example.taxi_conductor.Utils.LocationUtils;
import com.example.taxi_conductor.databinding.FragmentHomeBinding;
import com.example.taxi_conductor.home.NavigationConductorActivity;
import com.example.taxi_conductor.reference.Common;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.kusu.loadingbutton.LoadingButton;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private FirebaseAuth mAuth;

    @BindView(R.id.chip_decline)
    Chip chip_decline;
    @BindView(R.id.layout_accept)
    CardView layout_accept;
    @BindView(R.id.circularProgressBar)
    CircularProgressBar circularProgressBar;
    @BindView(R.id.txt_estimate_time)
    TextView txt_estimate_time;
    @BindView(R.id.txt_estimate_distance)
    TextView txt_estimate_distance;
    @BindView(R.id.root_layout)
    FrameLayout root_layout;
    @BindView(R.id.txt_rating)
    TextView txt_rating;
    @BindView(R.id.txt_type_uber)
    TextView txt_type_uber;
    @BindView(R.id.img_round)
    ImageView img_round;
    @BindView(R.id.layout_start_uber)
    CardView layout_start_uber;
    @BindView(R.id.txt_rider_name)
    TextView txt_rider_name;
    @BindView(R.id.txt_start_uber_estimate_distance)
    TextView txt_start_uber_estimate_distance;
    @BindView(R.id.txt_start_uber_estimate_time)
    TextView txt_start_uber_estimate_time;
    @BindView(R.id.img_phone_call)
    ImageView img_phone_call;
    @BindView(R.id.btn_start_uber)
    LoadingButton btn_start_uber;
    @BindView(R.id.btn_complete_trip)
    LoadingButton btn_complete_trip;
    @BindView(R.id.btn_conductor_arrived)
    LoadingButton btn_conductor_arrived;
    @BindView(R.id.layout_notify_rider)
    LinearLayout layout_notify_rider;
    @BindView(R.id.txt_notify_rider)
    TextView txt_notify_rider;
    @BindView(R.id.txt_notify_rider_time)
    TextView txt_notify_rider_time;
    @BindView(R.id.progress_notify)
    ProgressBar progress_notify;
   @BindView(R.id.suspended_layout)
   LinearLayout suspended_layout;



    private String tripNumberId = "";
    private boolean isTripStart = false, onlineSystemAlreadyRegister = false;
    private GeoFire pickupGeoFire, destinationGeoFire;
    private GeoQuery pickupGeoQuery, destinationGeoQuery;
    private CountDownTimer waiting_timer;

    private GeoQueryEventListener pickupGeoQueryListener = new GeoQueryEventListener() {


        @Override
        public void onKeyEntered(String key, GeoLocation location) {
            Toast.makeText(getContext(), "Destination Entered!!!", Toast.LENGTH_SHORT).show();
            btn_start_uber.setEnabled(true);
            ConductorUtils.sendNotifyToRider(getContext(), root_layout, key);
            if (pickupGeoQuery != null) {

                pickupGeoFire.removeLocation(key);
                pickupGeoFire = null;
                pickupGeoQuery.removeAllListeners();
            }

        }

        @Override
        public void onKeyExited(String key) {
            btn_start_uber.setEnabled(false);

        }

        @Override
        public void onKeyMoved(String key, GeoLocation location) {

        }

        @Override
        public void onGeoQueryReady() {

        }

        @Override
        public void onGeoQueryError(DatabaseError error) {

        }

    };
    private GeoQueryEventListener destinationGeoQueryListener = new GeoQueryEventListener() {
        @Override
        public void onKeyEntered(String key, GeoLocation location) {
            btn_complete_trip.setEnabled(true);
            if (destinationGeoQuery != null) {
                destinationGeoFire.removeLocation(key);
                destinationGeoFire = null;
                destinationGeoQuery.removeAllListeners();

            }

        }

        @Override
        public void onKeyExited(String key) {

        }

        @Override
        public void onKeyMoved(String key, GeoLocation location) {

        }

        @Override
        public void onGeoQueryReady() {

        }

        @Override
        public void onGeoQueryError(DatabaseError error) {

        }
    };
    private String cityName="";
    private DrawerLayout drawerLayout;
    private ImageView image_expand;
    private NavigationView navigationView;


    //@OnClick para el CardView de cuando llega la peticion del viaje y ejecutar el metodo
    @OnClick(R.id.chip_decline)
    void onDeclineClick() {
        if (driverRequestRecived != null) {
            if (TextUtils.isEmpty(tripNumberId)) {

                if (countDownEvent != null)
                    countDownEvent.dispose();
                chip_decline.setVisibility(View.GONE);
                layout_accept.setVisibility(View.GONE);
                mMap.clear();
                circularProgressBar.setProgress(0);
                ConductorUtils.sendDeclineRequest(root_layout, getContext(), driverRequestRecived.getKey());
                driverRequestRecived = null;


            } else {

                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(mapFragment.getView(), R.string.permission_require, Snackbar.LENGTH_SHORT).show();
                    return;
                }
                fusedLocationProviderClient.getLastLocation()
                        .addOnFailureListener(e -> {
                            Snackbar.make(mapFragment.getView(), e.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }).addOnSuccessListener(location -> {

                    chip_decline.setVisibility(View.GONE);
                    layout_start_uber.setVisibility(View.GONE);
                    mMap.clear();
                    ConductorUtils.sendDeclineAndRemoveTripRequest(root_layout, getContext(),
                            driverRequestRecived.getKey(), tripNumberId);
                    tripNumberId = ""; //Set tripNumberId to empty
                    driverRequestRecived = null;
                    makeDriverOnline(location);

                });


            }



        }
    }

    @OnClick(R.id.btn_start_uber)
    void onStartUberClick() {
        // Limpiar la ruta
        if (blackPolyline != null) blackPolyline.remove();
        if (greypolyline != null) greypolyline.remove();

        //cancelar despues del time out
        if (waiting_timer != null) waiting_timer.cancel();
        layout_notify_rider.setVisibility(View.GONE);
        if (driverRequestRecived != null) {

            LatLng destinationLatLng = new LatLng(

                    Double.parseDouble(driverRequestRecived.getDestinationLocation().split(",")[0]),
                    Double.parseDouble(driverRequestRecived.getDestinationLocation().split(",")[1])

            );
            mMap.addMarker(new MarkerOptions()
                    .position(destinationLatLng)
                    .title(driverRequestRecived.getDestinationLocationString())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            drawPathFromCurrentLocation(driverRequestRecived.getDestinationLocation());

        }
        btn_start_uber.setVisibility(View.GONE);
        chip_decline.setVisibility(View.GONE);
        btn_complete_trip.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_complete_trip)
    void onCompleteTripClick() {
        Toast.makeText(getContext(), "Complete trip facke action", Toast.LENGTH_SHORT).show();
        Map<String, Object> update_trip = new HashMap<>();
        update_trip.put("viaje completado", true);
        FirebaseDatabase.getInstance()
                .getReference(Common.TRIP)
                .child(tripNumberId)
                .updateChildren(update_trip)
                .addOnFailureListener(e ->
                        Snackbar.make(mapFragment.requireView(), e.getMessage(), Snackbar.LENGTH_SHORT).show())
                .addOnSuccessListener(aVoid -> {

                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Snackbar.make(mapFragment.requireView(), getContext().getString(R.string.permission_require), Snackbar.LENGTH_LONG);
                        return;
                    }
                    fusedLocationProviderClient.getLastLocation()
                            .addOnFailureListener(e -> {
                                Snackbar.make(mapFragment.requireView(), e.getMessage(), Snackbar.LENGTH_SHORT).show();

                            }).addOnSuccessListener(location -> {

                                ConductorUtils.sendCompleteTripToRider(mapFragment.requireView(),getContext(),driverRequestRecived.getKey(),
                                tripNumberId);
                                mMap.clear();
                                tripNumberId ="";
                                isTripStart = false;
                                chip_decline.setVisibility(View.GONE);
                                layout_accept.setVisibility(View.GONE);
                                circularProgressBar.setProgress(0);
                                layout_start_uber.setVisibility(View.GONE);
                                layout_notify_rider.setVisibility(View.GONE);
                                progress_notify.setProgress(0);
                                btn_complete_trip.setEnabled(false);
                                btn_complete_trip.setVisibility(View.GONE);
                                btn_start_uber.setEnabled(false);
                                btn_start_uber.setVisibility(View.VISIBLE);
                                destinationGeoFire = null;
                                pickupGeoFire = null;
                                driverRequestRecived = null;
                                makeDriverOnline(location);
                            });

                });

    }

    private void drawPathFromCurrentLocation(String destinationLocation) {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(requireView(), getString(R.string.permission_require), Snackbar.LENGTH_LONG).show();
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnFailureListener(e -> Snackbar.make(requireView(), e.getMessage(), Snackbar.LENGTH_LONG).show()).addOnSuccessListener(location -> {

            compositeDisposable.add(iGoogleAPI.getDirections("driving",
                    "less_driving",
                    new StringBuilder()
                            .append(location.getLatitude())
                            .append(",")
                            .append(location.getLongitude())
                            .toString(),
                    destinationLocation,
                    getString(R.string.google_maps_key))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(returnResult -> {
                        Log.d("API_RETURN", returnResult);

                        //Request API


                        // Toast.makeText(this,"Origen: "+selectedPlaceEvent.getDestinationString(),Toast.LENGTH_SHORT).show();

                        try {
                            JSONObject jsonObject = new JSONObject(returnResult);
                            JSONArray jsonArray = jsonObject.getJSONArray("routes");
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject route = jsonArray.getJSONObject(i);
                                JSONObject poly = route.getJSONObject("overview_polyline");
                                String polyline = poly.getString("points");
                                polylineList = Common.decodePoly(polyline);

                            }

                            polylineOptions = new PolylineOptions();
                            polylineOptions.color(Color.GRAY);
                            polylineOptions.width(12);
                            polylineOptions.startCap(new SquareCap());
                            polylineOptions.jointType(JointType.ROUND);
                            polylineOptions.addAll(polylineList);
                            greypolyline = mMap.addPolyline(polylineOptions);

                            blackpolylineOptions = new PolylineOptions();
                            blackpolylineOptions.color(Color.BLACK);
                            blackpolylineOptions.width(12);
                            blackpolylineOptions.startCap(new SquareCap());
                            blackpolylineOptions.jointType(JointType.ROUND);
                            blackpolylineOptions.addAll(polylineList);
                            blackPolyline = mMap.addPolyline(blackpolylineOptions);


                            LatLng origin = new LatLng(location.getLatitude(), location.getLongitude());
                            LatLng destination = new LatLng(Double.parseDouble(destinationLocation.split(",")[0]),
                                    Double.parseDouble(destinationLocation.split(",")[1]));

                            LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                    .include(origin)
                                    .include(destination)
                                    .build();

                            createGeoFireDestinationLocation(driverRequestRecived.getKey(),destination);

                            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 160));
                            mMap.moveCamera(CameraUpdateFactory.zoomTo(mMap.getCameraPosition().zoom - 1));


                        } catch (Exception e) {
                            // aqui esta el errpr
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    })
            );


        });



    }

    private void createGeoFireDestinationLocation(String key, LatLng destination) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Common.TRIP_DESTINATION_LOCATION_REF);
        destinationGeoFire = new GeoFire(ref);
        destinationGeoFire.setLocation(key, new GeoLocation(destination.latitude, destination.longitude),
                (key1, error) -> {


        });
    }

    //Rutas
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private IGoogleAPI iGoogleAPI;
    private Polyline blackPolyline, greypolyline;
    private PolylineOptions polylineOptions, blackpolylineOptions;
    private List<LatLng> polylineList;
    private DriverRequestRecived driverRequestRecived;
    private Disposable countDownEvent;

    private GoogleMap mMap;

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    //location
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    SupportMapFragment mapFragment;

    private boolean isFirstTime = true;

    //Online System
    DatabaseReference OnlineRef, currentUserRef, driversLocationRef;
    GeoFire geoFire;
    ValueEventListener onlineValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists() && currentUserRef != null) {
                currentUserRef.onDisconnect().removeValue();
                isFirstTime = true;
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Snackbar.make(mapFragment.getView(), error.getMessage(), Snackbar.LENGTH_SHORT).show();

        }
    };





    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_home, container, false);


        init();
        initViews(view);

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference prices_ref = FirebaseDatabase.getInstance()
                .getReference(Common.DRIVER_INFO_REFERENCE).child(userId);
        prices_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String precioBase = snapshot.child("status").getValue(String.class);
                if(precioBase.equals("Enable")){
                    suspended_layout.setVisibility(View.GONE);
                }else {

                    suspended_layout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getContext(),"Error al consultar status del conductor:  "+error,Toast.LENGTH_LONG).show();
                
            }
        });



        //obtener el mapa y notificar cuando el mapa sea lanzado
        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

     //   suspended_layout.setVisibility(View.VISIBLE);


        return view;
    }

    private void initViews(View root) {


        ButterKnife.bind(this, root);


    }


    private void init() {




        iGoogleAPI = RetrofitClient.getInstance().create(IGoogleAPI.class);
        OnlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        buildLocationRequest();
        buildLocationCallback();

        updateLocation();

    }

    private void updateLocation() {
        if (fusedLocationProviderClient == null) {

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(getView(), getString(R.string.permission_require), Snackbar.LENGTH_SHORT).show();
                return;
            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());


        }

    }

    private void buildLocationCallback() {

        if (locationCallback == null) {
            locationCallback = new LocationCallback() {


                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);

                    LatLng newPosition = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult
                            .getLastLocation().getLongitude());

                    if(pickupGeoFire != null)
                    {
                        pickupGeoQuery = pickupGeoFire.queryAtLocation(new GeoLocation(locationResult.getLastLocation().getLatitude(),
                                locationResult.getLastLocation().getLongitude()),Common.MIN_RANGE_PICKUP_IN_KM);
                        pickupGeoQuery.addGeoQueryEventListener(pickupGeoQueryListener);

                    }

                    if(destinationGeoFire != null){
                        destinationGeoQuery = destinationGeoFire.queryAtLocation(new GeoLocation(locationResult.getLastLocation().getLatitude(),
                                locationResult.getLastLocation().getLongitude()),Common.MIN_RANGE_PICKUP_IN_KM);
                        destinationGeoQuery.addGeoQueryEventListener(destinationGeoQueryListener);
                    }

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPosition, 18f));


                    if(!isTripStart) {
                        makeDriverOnline(locationResult.getLastLocation());


                    }
                    else{
                        if (!TextUtils.isEmpty(tripNumberId)){

                            Map<String,Object> update_data = new HashMap<>();
                            update_data.put("currentLat",locationResult.getLastLocation().getLatitude());
                            update_data.put("currentLng",locationResult.getLastLocation().getLongitude());

                            FirebaseDatabase.getInstance()
                                    .getReference(Common.TRIP)
                                    .child(tripNumberId)
                                    .updateChildren(update_data)
                                    .addOnFailureListener(e ->
                                            Snackbar.make(mapFragment.getView(),e.getMessage(),Snackbar.LENGTH_SHORT).show())
                                    .addOnSuccessListener(aVoid -> {

                                    });


                        }


                    }


                }
            };

        }

    }

    private void makeDriverOnline(Location location) {


        String saveCityName = cityName; // guardamos el nombre de la ciudad en una variable
        cityName = LocationUtils.getAdressFromLocation(getContext(),location);
        if(!cityName.equals(saveCityName)){
            if (currentUserRef != null)// checkCurrentRef
                currentUserRef.removeValue() // Delete current Ref Driver Location old Position
                        .addOnFailureListener(e -> {

                            Snackbar.make(mapFragment.getView(),e.getMessage(),Snackbar.LENGTH_LONG).show();
                        }).addOnSuccessListener(aVoid -> {

                            updateDriverLocation(location);
                        });


        }
            else
            updateDriverLocation(location);



    }

    private void updateDriverLocation(Location location) {

        if(!TextUtils.isEmpty(cityName)) // verify city name
        {
            driversLocationRef = FirebaseDatabase.getInstance().getReference(Common.DRIVERS_LOCATION_REFERENCE)
                    .child(cityName);
            currentUserRef = driversLocationRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            geoFire = new GeoFire(driversLocationRef);
            geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    new GeoLocation(location.getLatitude(),
                            location.getLongitude()),
                    (key, error) -> {
                        if (error != null)
                            Snackbar.make(mapFragment.getView(), error.getMessage(), Snackbar.LENGTH_SHORT).show();
                    });

            registerOnlineSystem();
        }

        else
            Snackbar.make(mapFragment.getView(),getString(R.string.service_unavailable_here),Snackbar.LENGTH_LONG).show();



    }

    private void buildLocationRequest() {
        if (locationRequest == null) {
            locationRequest = new LocationRequest();
            locationRequest.setSmallestDisplacement(50f);
            locationRequest.setInterval(15000);
            locationRequest.setFastestInterval(10000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        }


    }


    @Override
    public void onDestroy() {

        geoFire.removeLocation(FirebaseAuth.getInstance().getCurrentUser().getUid());
        binding = null;
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        OnlineRef.removeEventListener(onlineValueListener);

        if (EventBus.getDefault().hasSubscriberForEvent(DriverRequestRecived.class))
            EventBus.getDefault().removeStickyEvent(DriverRequestRecived.class);

        if (EventBus.getDefault().hasSubscriberForEvent(NotifyToRiderEvent.class))
            EventBus.getDefault().removeStickyEvent(NotifyToRiderEvent.class);

        EventBus.getDefault().unregister(this);
        compositeDisposable.clear();
        onlineSystemAlreadyRegister=false;
        super.onDestroy();

    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        registerOnlineSystem();
        super.onResume();
    }

    private void registerOnlineSystem() {
        if(!onlineSystemAlreadyRegister)
        {
            OnlineRef.addValueEventListener(onlineValueListener);
            onlineSystemAlreadyRegister = true;

        }

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        Dexter.withContext(getContext())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {

                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            Snackbar.make(getView(), getString(R.string.permission_require), Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        mMap.setMyLocationEnabled(true);
                        mMap.getUiSettings().setMyLocationButtonEnabled(true);
                        mMap.setOnMyLocationButtonClickListener(() -> {

                            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return false;
                            }

                            fusedLocationProviderClient.getLastLocation()
                                    .addOnFailureListener(e -> Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_SHORT)
                                            .show()).addOnSuccessListener(location -> {
                                                    LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 18f));




                            });

                            return true;
                        });


                        //boton del Layuout
                        View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).getParent())
                                .findViewById(Integer.parseInt("2"));
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                        // right bottom
                        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                        params.setMargins(0, 0, 0, 50);


                        //Move Location
                        buildLocationRequest();
                        buildLocationCallback();
                        updateLocation();

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                        Snackbar.make(getView(), permissionDeniedResponse.getPermissionName() + "need enable", Snackbar
                                .LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                    }
                })
                .check();

        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.uber_maps_style));
            if (!success)
                Snackbar.make(getView(), "Error al cargar el estilo de mapa, contacte con soporte", Snackbar.LENGTH_SHORT).show();

        } catch (Exception e) {

            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_SHORT).show();

        }

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onDriverRequestReceive(DriverRequestRecived event) {

        driverRequestRecived = event;
        //Get Current Location
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(requireView(), getString(R.string.permission_require), Snackbar.LENGTH_LONG).show();
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnFailureListener(e -> Snackbar.make(requireView(), e.getMessage(), Snackbar.LENGTH_LONG).show()).addOnSuccessListener(location -> {

            compositeDisposable.add(iGoogleAPI.getDirections("driving",
                    "less_driving",
                    new StringBuilder()
                            .append(location.getLatitude())
                            .append(",")
                            .append(location.getLongitude())
                            .toString(),
                    event.getPickupLocation(),
                    getString(R.string.google_maps_key))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(returnResult -> {
                        Log.d("API_RETURN", returnResult);

                        //Request API


                        // Toast.makeText(this,"Origen: "+selectedPlaceEvent.getDestinationString(),Toast.LENGTH_SHORT).show();

                        try {
                            JSONObject jsonObject = new JSONObject(returnResult);
                            JSONArray jsonArray = jsonObject.getJSONArray("routes");
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject route = jsonArray.getJSONObject(i);
                                JSONObject poly = route.getJSONObject("overview_polyline");
                                String polyline = poly.getString("points");
                                polylineList = Common.decodePoly(polyline);

                            }

                            polylineOptions = new PolylineOptions();
                            polylineOptions.color(Color.GRAY);
                            polylineOptions.width(12);
                            polylineOptions.startCap(new SquareCap());
                            polylineOptions.jointType(JointType.ROUND);
                            polylineOptions.addAll(polylineList);
                            greypolyline = mMap.addPolyline(polylineOptions);

                            blackpolylineOptions = new PolylineOptions();
                            blackpolylineOptions.color(Color.BLACK);
                            blackpolylineOptions.width(12);
                            blackpolylineOptions.startCap(new SquareCap());
                            blackpolylineOptions.jointType(JointType.ROUND);
                            blackpolylineOptions.addAll(polylineList);
                            blackPolyline = mMap.addPolyline(blackpolylineOptions);

                            //Animator to Line
                            ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
                            valueAnimator.setDuration(1100);
                            valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
                            valueAnimator.setInterpolator(new LinearInterpolator());
                            valueAnimator.addUpdateListener(value -> {

                                List<LatLng> points = greypolyline.getPoints();
                                int porcentValue = (int) value.getAnimatedValue();
                                int size = points.size();
                                int newPoints = (int) (size * (porcentValue / 100.0f));
                                List<LatLng> p = points.subList(0, newPoints);
                                blackPolyline.setPoints(p);

                            });

                            valueAnimator.start();

                            LatLng origin = new LatLng(location.getLatitude(), location.getLongitude());
                            LatLng destination = new LatLng(Double.parseDouble(event.getPickupLocation().split(",")[0]),
                                    Double.parseDouble(event.getPickupLocation().split(",")[1]));

                            LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                    .include(origin)
                                    .include(destination)
                                    .build();


                            JSONObject object = jsonArray.getJSONObject(0);
                            JSONArray legs = object.getJSONArray("legs");
                            JSONObject legObjects = legs.getJSONObject(0);
                            JSONObject time = legObjects.getJSONObject("duration");
                            String duration = time.getString("text");
                            JSONObject distance_estimate = legObjects.getJSONObject("distance");
                            String distance = distance_estimate.getString("text");
                            txt_estimate_time.setText(duration);
                            txt_estimate_distance.setText(distance);

                            mMap.addMarker(new MarkerOptions()
                                    .position(destination)
                                    .icon(BitmapDescriptorFactory.defaultMarker())
                                    .title("Tu estas aqui"));

                            createGeoFirePickupLocation(event.getKey(),destination);


                            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 160));
                            mMap.moveCamera(CameraUpdateFactory.zoomTo(mMap.getCameraPosition().zoom - 1));

                            //showLayout
                            chip_decline.setVisibility(View.VISIBLE);
                            layout_accept.setVisibility(View.VISIBLE);

                            countDownEvent = Observable.interval(100, TimeUnit.MILLISECONDS)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .doOnNext(x -> {
                                        circularProgressBar.setProgress(circularProgressBar.getProgress() + 1f);
                                    }).takeUntil(aLong -> aLong == 100)
                                    .doOnComplete(() -> {
                                        circularProgressBar.setProgress(0);

                                        createTripPlan(event, duration, distance);
                                        // despues del tiempo del que no se ejecuto la cancelacion del viaje;

                                        Toast.makeText(getContext(), "Fake accept action", Toast.LENGTH_SHORT).show();
                                    }).subscribe();

                        } catch (Exception e) {
                            // aqui esta el errpr
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    })
            );


        });
    }

    private void createGeoFirePickupLocation(String key, LatLng destination) {

        DatabaseReference ref = FirebaseDatabase
                .getInstance()
                .getReference(Common.TRIP_PICKUP_REF);
        pickupGeoFire = new GeoFire(ref);
        pickupGeoFire.setLocation(key, new GeoLocation(destination.latitude, destination.longitude),
                (key1, error) ->{

            if(error != null)
                Snackbar.make(root_layout,error.getMessage(),Snackbar.LENGTH_LONG).show();
                else
                    Log.d("Juan Daniel",key1+"Fue creado con exito en GeoFire");
        });

    }


    private void createTripPlan(DriverRequestRecived event, String duration, String distance) {

        setProcessLayout(true);
        //Sincronizar el tiempo con el servidor y el dispocitivo
        FirebaseDatabase.getInstance()
                .getReference(".info/serverTimeOffset")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long timeOffset = snapshot.getValue(Long.class);

                        FirebaseDatabase.getInstance().getReference(Common.RIDER_INFO)
                                .child(event.getKey())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (snapshot.exists()) {

                                            RiderModel riderModel = snapshot.getValue(RiderModel.class);
                                            //get Location
                                            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                Snackbar.make(mapFragment.getView(), getContext().getString(R.string.permission_require), Snackbar.LENGTH_SHORT).show();
                                                return;
                                            }
                                            fusedLocationProviderClient.getLastLocation()
                                                    .addOnFailureListener(e -> Snackbar.make(mapFragment.getView(), e.getMessage(), Snackbar.LENGTH_SHORT).show())
                                                    .addOnSuccessListener(location -> {

                                                        TripPlanModel tripPlanModel = new TripPlanModel();
                                                        tripPlanModel.setDriver(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                        tripPlanModel.setMenuDrawer("Disable");

                                                        tripPlanModel.setDriverModel(Common.currentRide);
                                                        tripPlanModel.setRiderModel(riderModel);
                                                        tripPlanModel.setOrigin(event.getPickupLocation());
                                                        tripPlanModel.setOriginString(event.getPickupLocationString());
                                                        tripPlanModel.setDestination(event.getDestinationLocation());
                                                        tripPlanModel.setDestinationString(event.getDestinationLocationString());
                                                        tripPlanModel.setDistancePickup(distance);
                                                        tripPlanModel.setDurationPickup(duration);
                                                        tripPlanModel.setCurrentLat(location.getLatitude());
                                                        tripPlanModel.setCurrentLng(location.getLongitude());

                                                        tripNumberId = Common.createuniqueTripNumber(timeOffset);

                                                        FirebaseDatabase.getInstance().getReference(Common.TRIP)
                                                                .child(tripNumberId)
                                                                .setValue(tripPlanModel)
                                                                .addOnFailureListener(e -> {

                                                                    Snackbar.make(mapFragment.getView(), e.getMessage(), Snackbar.LENGTH_SHORT).show();


                                                                }).addOnSuccessListener(aVoid -> {

                                                            txt_rider_name.setText(riderModel.getFirstName());
                                                            txt_start_uber_estimate_time.setText(duration);
                                                            txt_start_uber_estimate_distance.setText(distance);
                                                            setOfflineModeForDriver(event, duration, distance);

                                                        });

                                                    });

                                        } else

                                            Snackbar.make(mapFragment.getView(), getContext().getString(R.string.rider_not_found) + "" + event.getKey(), Snackbar.LENGTH_SHORT).show();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                        Snackbar.make(mapFragment.getView(), error.getMessage(), Snackbar.LENGTH_SHORT).show();


                                    }
                                });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Snackbar.make(mapFragment.getView(), error.getMessage(), Snackbar.LENGTH_SHORT).show();

                    }
                });


    }

    private void setOfflineModeForDriver(DriverRequestRecived event, String duration, String distance) {

        ConductorUtils.sendAcceptRequestToRider(mapFragment.getView(),getContext(),event.getKey(),tripNumberId);

        // desconectarse
        if (currentUserRef != null)
            currentUserRef.removeValue();

        setProcessLayout(false);
        layout_accept.setVisibility(View.GONE);
        layout_start_uber.setVisibility(View.VISIBLE);
        isTripStart=true;


    }


    private void setProcessLayout(boolean isProcess) {
        int color = -1;
        if (isProcess) {
            color = ContextCompat.getColor(getContext(), R.color.dark_gray);
            circularProgressBar.setIndeterminateMode(true);
            txt_rating.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_star_24_dark_gray, 0);
        }

            else {
            color = ContextCompat.getColor(getContext(), R.color.green);
            circularProgressBar.setIndeterminateMode(false);
            circularProgressBar.setProgress(0);
            txt_rating.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_star_24, 0);

            }

            txt_estimate_time.setTextColor(color);
            txt_estimate_distance.setTextColor(color);
            ImageViewCompat.setImageTintList(img_round, ColorStateList.valueOf(color));
            txt_rating.setTextColor(color);
            txt_type_uber.setTextColor(color);



    }



    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onNotifyToRyder(NotifyToRiderEvent event){

        layout_notify_rider.setVisibility(View.VISIBLE);
        progress_notify.setMax(Common.WAIT_TIME_IN_MIN * 60);
       waiting_timer = new CountDownTimer(Common.WAIT_TIME_IN_MIN*60*1000,1000) {
            @Override
            public void onTick(long l) {

                progress_notify.setProgress(progress_notify.getProgress()+1);
                txt_notify_rider_time.setText(String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(l) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(l)),
                        TimeUnit.MILLISECONDS.toSeconds(l) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l))));

            }

            @Override
            public void onFinish() {
                Snackbar.make(root_layout,getString(R.string.time_over),Snackbar.LENGTH_SHORT).show();
            }
        }.start();


    }

}