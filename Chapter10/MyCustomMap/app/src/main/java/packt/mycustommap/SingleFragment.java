package packt.mycustommap;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class SingleFragment extends Fragment {

    private MapFragment mapFragment;
    private SharedPreferences pref;
    private GoogleMap map;
    private DBHandler db;
    private FloatingActionButton delete,cancel;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.single_fragment, container, false);

        db = new DBHandler(getActivity());

        pref = getActivity().getPreferences(0);

        loadMap();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadmarker();
            }
        },1000);

        delete = (FloatingActionButton)view.findViewById(R.id.fab_delete);
        cancel = (FloatingActionButton)view.findViewById(R.id.fab_cancel);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteMarker();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment list = new MarkerList();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, list);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        return view;
    }

    public void loadMap(){
        mapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.content_frame_map_single, mapFragment);
        fragmentTransaction.commit();
    }
    //This method is used to load marker on Google map
    public void loadmarker(){
        map = mapFragment.getMap();
        map.getUiSettings().setMapToolbarEnabled(false);
        Marker mkr = db.getMarker(pref.getInt("ID",0));
        map.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(mkr.getLatitude()), Double.valueOf(mkr.getLongitude())))
                .title(mkr.getName())
                .snippet(mkr.getAddress()));
        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(new LatLng(Double.valueOf(mkr.getLatitude()), Double.valueOf(mkr.getLongitude()))).
                zoom(16).
                build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }
    // This method is called when the delete fab is clicked
    public void deleteMarker(){
        Marker mkr = db.getMarker(pref.getInt("ID",0));
        db.deleteMarker(mkr);
        Fragment list = new MarkerList();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, list);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.addToBackStack(null);
        ft.commit();
        Toast.makeText(getActivity(),R.string.marker_deleted,Toast.LENGTH_SHORT).show();

    }
}