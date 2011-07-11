package com.metaaps.mobile.compastic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.metaaps.mobile.compastic.widgets.AddressesAdapter;
import com.metaaps.mobile.compastic.widgets.PlacesAdapter;
import com.metaaps.mobile.compastic.widgets.entities.Place;
import com.metaaps.mobile.compastic.widgets.map.Marker;

public class AddPlace extends MapActivity {

	private MapView mapView;
	private MapController mapController;
	private ListView placesList;
	private Button searchButton;
	private EditText placeNameEdit;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addplace);
        
        // create map and initialise controls
	    mapView = (MapView) findViewById(R.id.mapview);
	    mapView.setBuiltInZoomControls(true);
	    mapController = mapView.getController();
	    mapController.setZoom(2);
	    
	    // find views
	    placesList = (ListView) findViewById(R.id.addresses);
	    placesList.setAdapter(new AddressesAdapter(this, null));
	    
	    placeNameEdit = (EditText) findViewById(R.id.input_search_query);
	    
	    searchButton = (Button) findViewById(R.id.button_go);
	    searchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String placeName = getPlaceName();
				displayPlaces(placeName);
				InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				mgr.hideSoftInputFromWindow(placeNameEdit.getWindowToken(), 0);
			}

		});
	    
	    placesList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> view, View dayView, int position, long id) {
		    	Place place = (Place) ((AddressesAdapter) placesList.getAdapter()).getItem(position);
		    	if(place == null) {
		    		return;
		    	}
		    	PlacesAdapter.addPlace(AddPlace.this, place);
				Intent intent = new Intent();
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	    
    }
	
	private String getPlaceName() {
		return placeNameEdit.getText().toString();
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	private void displayPlaces(String locationName) {
        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        try {
        	List<Address> places = geoCoder.getFromLocationName(locationName, 10);
        	placesList.setAdapter(new AddressesAdapter(this, places));
        	// add places to the map
    	    // clear previous overlays
            List<Overlay> listOfOverlays = mapView.getOverlays();
            if(listOfOverlays != null) {
                listOfOverlays.clear();
                mapView.invalidate();
            }
            // add place markers
            ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();
            int index = 0;
            for(Address address : places) {
                Marker marker = new Marker(this, address, index++);
                listOfOverlays.add(marker);
                points.add(Utils.convertToGeoPoint(address));
            }
            Utils.setMapBoundsToPois(mapController, points, 0.1, 0.1);
        } catch (IOException e) {                
            e.printStackTrace();
            Utils.alert(this, "Could not retrieve locations, do you have an Internet connection running?");
        }   
	}

}
