package packt.mycustommap;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;


public class MainFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private FloatingActionButton locate, add;
    private Location location;
    private double latitude;
    private double longitude;
    private GoogleApiClient googleApiClient;
    private GoogleMap map;
    private com.google.android.gms.maps.model.Marker locationMarker;
    private MapFragment mapFragment;
    private SharedPreferences pref;
    private DBHandler db;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);

        db = new DBHandler(getActivity());


        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();

        pref = getActivity().getPreferences(0);
        locate = (FloatingActionButton)view.findViewById(R.id.fab_locate);
        add    = (FloatingActionButton)view.findViewById(R.id.fab_add);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment add = new AddMarker();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, add);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locate();
            }
        });

        loadMap();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadmarkers();
            }
        },1000);

        return view;
    }

    public void locate(){

        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            map = mapFragment.getMap();
            map.getUiSettings().setMapToolbarEnabled(false);
            if(locationMarker != null)
                locationMarker.remove();
            CameraPosition cameraPosition = new CameraPosition.Builder().
                    target(new LatLng(latitude,longitude)).
                    zoom(16).
                    build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            locationMarker = map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                    .title("Marker")
                    .snippet("My Location")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location)));
        }

    }
    public void loadMap(){
        mapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.content_frame_map, mapFragment);
        fragmentTransaction.commit();
    }
    //It is used to load the markers on the map
    public void loadmarkers(){
        map = mapFragment.getMap();
        Log.d("MyMap", "Count = " + db.getMarkerCount());
        if(db.getMarkerCount() > 0){
            List<Marker> markerList = db.getAllMarkers();
            for (int i = 0; i < db.getMarkerCount(); i++) {
                Marker mkr = markerList.get(i);
                map.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(mkr.getLatitude()),Double.valueOf(mkr.getLongitude())))
                        .title(mkr.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(getColor(mkr.getColor())))
                        .snippet(mkr.getAddress()));

            }
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        location = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);

        if(location != null) {
            SharedPreferences.Editor edit = pref.edit();
            edit.putLong("LATITUDE",Double.doubleToLongBits(location.getLatitude()));
            edit.putLong("LONGITUDE", Double.doubleToLongBits(location.getLongitude()));
            edit.commit();

        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    //Used to return float value from the color string
    public float getColor(String clr){
        if(clr.equals("AZURE")){
            return 210;
        }else if( clr.equals("BLUE")) {
            return 240;
        }else if( clr.equals("CYAN")) {
            return 180;
        }else if( clr.equals("GREEN")) {
            return 120;
        }else if( clr.equals("MAGNETA")) {
            return 300;
        }else if( clr.equals("ORANGE")) {
            return 30;
        }else if( clr.equals("RED")) {
            return 0;
        }else if( clr.equals("ROSE")) {
            return 330;
        }else if( clr.equals("VIOLET")) {
            return 270;
        }else if( clr.equals("YELLOW")) {
            return 60;
        }else {
            return 0;
        }}


}