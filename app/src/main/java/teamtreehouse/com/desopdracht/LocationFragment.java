package teamtreehouse.com.desopdracht;


import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class LocationFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,LocationListener {

    private GoogleMap map;
    private MapView mapView;
    private LatLng PLACE;
    protected List<ParseObject> mMessages;
    protected String[] mLocations;
    protected final static String TAG = LocationFragment.class.getSimpleName();


    public List<ParseObject> getPictures() {
        return mPictures;
    }

    public void setPictures(List<ParseObject> pictures) {
        mPictures = pictures;
    }

    protected List<ParseObject> mPictures;
    protected Context mContext;

    private List<ParseObject> fotos;

    private GoogleApiClient mGoogleApiClient;

    private static final LocationRequest REQUEST = LocationRequest.create().setInterval(5000).setFastestInterval(16).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    public LocationFragment(){
        //verplicht
    }

    public static LocationFragment newInstance(ParseObject fotos){
         LocationFragment fragment = new LocationFragment();

        Bundle bundle = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //mGoogleApiClient = new GoogleApiClient.Builder(mContext).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PLACE = new LatLng(50.832,3.22);
        View v = inflater.inflate(R.layout.activity_support_map_fragment, container, false);
        getMap(v, savedInstanceState);
        map.setMyLocationEnabled(true);

        return v;
    }

    private void getMap(View v, Bundle savedInstanceState){
        mapView = (MapView) v.findViewById(R.id.location_map);
        mapView.onCreate(savedInstanceState);

        mapView.onResume();

        MapsInitializer.initialize(getActivity().getApplicationContext());

        map = mapView.getMap();
    }

    @Override
    public void onResume() {
        super.onResume();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
                getActivity().setProgressBarIndeterminateVisibility(false);

                if (e == null) {
                    // We found messages!
                    mMessages = messages;

                    String[] usernames = new String[mMessages.size()];
                    String[] locations = new String[mMessages.size()];

                    int i = 0;
                    for(ParseObject message : mMessages) {
                        usernames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
                        locations[i] = message.getString(ParseConstants.KEY_LOCATION);
                        if(locations[i] != null){
                            Double Lat = Double.parseDouble(locations[i].substring(15, 24).replaceAll(",","."));
                            Double Long = Double.parseDouble(locations[i].substring(25,33).replaceAll(",","."));
                            Log.d(TAG, "Latitude:" + Lat +" Longitude" + Long);
                            LatLng ltlg = new LatLng(Lat,Long);
                            MarkerOptions marker;
                            marker = new MarkerOptions().position(ltlg).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                            map.addMarker(marker);
                        }

                        i++;
                    }
                }
            }
        });


    }

    @Override
    public void onConnected(Bundle bundle) {
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,REQUEST,mContext);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //
    }
}
