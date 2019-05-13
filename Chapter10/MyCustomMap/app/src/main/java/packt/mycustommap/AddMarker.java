package packt.mycustommap;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddMarker extends Fragment {

    private EditText mk_name,mk_address;
    private MapFragment mapFragment;
    private SharedPreferences pref;
    private GoogleMap map;
    private double latitude;
    private double longitude;
    private Spinner color;
    private DBHandler db;
    private String nameText;
    private TextView lat,lng;
    private FloatingActionButton save,discard;
    private com.google.android.gms.maps.model.Marker locationMarker;
    static Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_marker, container, false);
        pref = getActivity().getPreferences(0);
        db = new DBHandler(getActivity());
        handler = new Handler(Looper.getMainLooper()){


                @Override
                public void handleMessage(Message msg) {
                    mk_address.setText((String)msg.obj);

            };
        };

        mk_name = (EditText)view.findViewById(R.id.marker_name);
        mk_address = (EditText)view.findViewById(R.id.marker_address);

        lat = (TextView)view.findViewById(R.id.latText);
        lng = (TextView)view.findViewById(R.id.lngText);
        color = (Spinner)view.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.colors));

        color.setAdapter(adapter);
        color.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                        int position = color.getSelectedItemPosition();
                        SharedPreferences.Editor edit = pref.edit();
                        edit.putString("COLOR",getResources().getStringArray(R.array.colors)[+position]);
                        edit.commit();
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                    }
                }
        );

        save = (FloatingActionButton)view.findViewById(R.id.fab_save);
        discard = (FloatingActionButton)view.findViewById(R.id.fab_discard);
        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment main = new MainFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, main);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameText = mk_name.getText().toString();
                if (!nameText.equals("")){
                    long id = db.addMarker(new packt.mycustommap.Marker(nameText
                            ,String.valueOf(Double.longBitsToDouble(pref.getLong("LATITUDE_NEW",0)))
                            ,String.valueOf(Double.longBitsToDouble(pref.getLong("LONGITUDE_NEW",0)))
                            ,mk_address.getText().toString()
                            ,pref.getString("COLOR","")));
                    if(id != -1){
                        Toast.makeText(getActivity(),R.string.toast_add_success,Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getActivity(),R.string.toast_add_error,Toast.LENGTH_SHORT).show();

                    }}
                else{
                    Toast.makeText(getActivity(),R.string.toast_field_empty,Toast.LENGTH_SHORT).show();

                }
            }
        });
        loadMap();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initializeMap();
            }
        },1000);
        return view;
    }
    public void loadMap(){
        mapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.content_frame_addmap, mapFragment);
        fragmentTransaction.commit();
    }
    public void initializeMap(){
        map = mapFragment.getMap();
        if (pref.getLong("LATITUDE",0) != 0 || pref.getLong("LONGITUDE",0) != 0) {
            latitude = Double.longBitsToDouble(pref.getLong("LATITUDE",0));
            longitude = Double.longBitsToDouble(pref.getLong("LONGITUDE", 0));

            SharedPreferences.Editor edit = pref.edit();
            edit.putLong("LATITUDE_NEW",Double.doubleToLongBits(latitude));
            edit.putLong("LONGITUDE_NEW",Double.doubleToLongBits(longitude));
            edit.commit();

            lat.setText("Latitude : "+latitude);
            lng.setText("Longitude : "+longitude);
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
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location)));
            final Location loc = new Location("");
            loc.setLatitude(latitude);
            loc.setLongitude(longitude);
            Thread thread = new Thread(new Runnable(){
                @Override
                public void run(){
                    getAddress(loc);
                }
            });
            thread.start();

            map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    final Location loc = new Location("");
                    lat.setText("Latitude : "+marker.getPosition().latitude);
                    lng.setText("Longitude : "+marker.getPosition().longitude);
                    loc.setLatitude(marker.getPosition().latitude);
                    loc.setLongitude(marker.getPosition().longitude);
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putLong("LATITUDE_NEW",Double.doubleToLongBits(marker.getPosition().latitude));
                    edit.putLong("LONGITUDE_NEW",Double.doubleToLongBits(marker.getPosition().longitude));
                    edit.commit();
                    Thread thread = new Thread(new Runnable(){
                        @Override
                        public void run(){
                            getAddress(loc);
                        }
                    });
                    thread.start();

                }
            });
        }
    }

    public void getAddress(Location location){

            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = null;
            String addressText = null;
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException | IllegalArgumentException e) {
                e.printStackTrace();
            }

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                addressText = String.format(
                        "%s, %s, %s, %s", address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getLocality(),
                        address.getCountryName(),
                        address.getPostalCode());

            }

            if (addressText != null) {
                Message message = Message.obtain();
                message.obj = addressText;
                handler.sendMessage(message);
            }

    }


}