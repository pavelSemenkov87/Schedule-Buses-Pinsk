package pavelsemenkov.bus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    GoogleMap map;
    final String TAG = "myLogs";
    private int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        init();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        map.getUiSettings().setMapToolbarEnabled(false);
        map.addMarker(new MarkerOptions().position(new LatLng(52.125864, 26.0868)).title("Marker in Pinsk"));
        /*CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(.125864, .0868))
                .zoom(13)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        map.moveCamera(cameraUpdate);*/
        /*LatLng sydney = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }
    private void init() {
    }


    public void onClickTest(View view) {
        if(i==0){
            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            i++;
        }
        else if(i==1){
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            i=0;
        }
    }
}
