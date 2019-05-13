package packt.mycustommap;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MarkerList extends Fragment {

    private ListView list;
    private TextView text;
    private DBHandler db;
    private List<Marker> mkrData;
    private SharedPreferences pref;
    private ArrayList<HashMap<String, String>> users = new ArrayList<HashMap<String, String>>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.marker_list, container, false);

        list = (ListView)view.findViewById(R.id.mList);
        text = (TextView)view.findViewById(R.id.txt);

        pref = getActivity().getPreferences(0);


        db = new DBHandler(getActivity());
        if(db.getMarkerCount() !=0 ) {
            text.setVisibility(View.GONE);
            mkrData = db.getAllMarkers();
            for (int i = 0; i < db.getMarkerCount(); i++) {
                Marker markerData = mkrData.get(i);
                String name = markerData.getName();
                String address = markerData.getAddress();
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("name",name);
                map.put("address",address);
                users.add(map);

            }
            ListAdapter adapter = new SimpleAdapter(getActivity(), users,
                    R.layout.marker_list_single,
                    new String[] { "name","address" }, new int[] {
                    R.id.txtName, R.id.txtAddress});
            list.setAdapter(adapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Marker mkr = mkrData.get(position);
                    int mk_id = mkr.getID();
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putInt("ID",mk_id);
                    edit.commit();
                    Fragment single = new SingleFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, single);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.addToBackStack(null);
                    ft.commit();

                }
            });

        }else{
            text.setText(R.string.no_marker);
            text.setVisibility(View.VISIBLE);
        }

        return view;
    }
}