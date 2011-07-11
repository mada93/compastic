package com.metaaps.mobile.compastic;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.metaaps.mobile.compastic.widgets.CompassView;
import com.metaaps.mobile.compastic.widgets.PlacesAdapter;
import com.metaaps.mobile.compastic.widgets.entities.Place;
import com.metaaps.mobile.compastic.widgets.map.LineOverlay;
import com.metaaps.mobile.compastic.widgets.map.MapMarker;

public class Compastic extends MapActivity {
    private static final int ADD_PLACE = 0x101;
	private SensorManager sensorManager;
	private Sensor sensor;
    private CompassView compassView;
    private boolean compass;
	List<Place> places = new ArrayList<Place>();
	private MapView mapView;
	private MapController mapController;
	
    private SensorEventListener listener;
    private SensorEventListener magneticFieldListener;
    private LocationManager locationService;
	private Spinner spinner;
	private ViewFlipper viewFlipper;
	private ImageButton modeButton;
	private boolean alertDisplayed = false;
	private boolean toastDisplayed = false;
	private LocationListener locationListener;
	private Location currentLocation;
	private int indexToast = 0;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // get the orientation sensor
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        // get the location service
    	locationService = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    	locationListener = new LocationListener() {
			
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
			public void onLocationChanged(Location location) {
				setCurrentLocation(location);
			}

		};
		// set the location listeners
    	locationService.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 25, locationListener);
    	locationService.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 25, locationListener);
    	
        // get views
    	// compass view
    	compassView = (CompassView) findViewById(R.id.compassview);
    	// place spinner
        spinner = (Spinner) findViewById(R.id.listplaces);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
				if(id == -1L) {
					addNewPlace();
				} else {
					refreshCompass();
	            	refreshMap();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
        refreshPlacesList();
        
        viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
        modeButton = (ImageButton) findViewById(R.id.modebutton);
        modeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setMode(!compass);
				viewFlipper.showNext();
			}

		});
        setMode(true);
        
        // create map and initialise controls
	    mapView = (MapView) findViewById(R.id.mapview);
	    mapView.setBuiltInZoomControls(true);
	    mapController = mapView.getController();
	    mapController.setZoom(2);
	    
	    listener = new SensorEventListener() {

			public void onSensorChanged(SensorEvent event) {
	            float[] values = event.values;
	            if (compassView != null) {
	            	compassView.setNorth(- values[0]);
	            	compassView.invalidate();
	            }
	        }

			public void onAccuracyChanged(Sensor sensor, int accuracy) {
	        }
	    };
	    
	    magneticFieldListener = new SensorEventListener() {

			public void onSensorChanged(SensorEvent event) {
	            float[] values = event.values;
        		if(values.length < 3) {
        			return;
        		}
            	if(Math.sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2]) > SensorManager.MAGNETIC_FIELD_EARTH_MAX * 1.5) {
            		if(alertDisplayed == false) {
	            		Utils.alert(Compastic.this, "Abnormal magnetic field! check for magnets or iron objects around you and recalibrate the device by doing an '8 shape' pattern with your phone.");
	            		alertDisplayed = true;
            		} else {
            			if(toastDisplayed == false) { //indexToast%100 == 0) {
            				Utils.toast(Compastic.this, "Abnormal magnetic field! ");
    	            		toastDisplayed = true;
            			}
            		}
            	} else {
            		// field back to normal
            		toastDisplayed = false;
            	}
	        }

			public void onAccuracyChanged(Sensor sensor, int accuracy) {
	        }
	    };
	    
	    // used to display welcome screen at the first initialisation
        SharedPreferences settings = getSharedPreferences("compastic", 0);
        int justInstalled = settings.getInt("installed", -1);
        if(justInstalled == -1) {
        	displayWelcomeScreen();
            Editor editor = settings.edit();
            editor.putInt("installed", 1);
            boolean saved = editor.commit();
        }
    }
    
    private void displayWelcomeScreen() {
    	final Dialog dialog = new Dialog(this);

    	dialog.setContentView(R.layout.welcome_dialog);
    	dialog.setTitle("Welcome to Compastic");

    	ImageButton start = (ImageButton) dialog.findViewById(R.id.start);
    	start.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});
    	dialog.show();
	}

//    @Override
//    protected void onPause() {
//        sensorManager.unregisterListener(listener);
//        sensorManager.unregisterListener(magneticFieldListener);
//    	super.onPause();
//    }
//    
	@Override
    protected void onResume()
    {
        super.onResume();

        sensorManager.registerListener(listener, sensor,
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(magneticFieldListener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop()
    {
        sensorManager.unregisterListener(listener);
        sensorManager.unregisterListener(magneticFieldListener);
        super.onStop();
    }

	private void setCurrentLocation(Location location) {
        if (compassView != null) {
        	currentLocation = location;
        	refreshCompass();
        	refreshMap();
        }
	}
	
	private void addNewPlace() {
		Intent intent = new Intent();
		intent.setClassName("com.metaaps.mobile.compastic", AddPlace.class.getName());
		startActivityForResult(intent, ADD_PLACE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		
		if(resultCode == Activity.RESULT_OK) {
			switch(requestCode) {
			case ADD_PLACE:
				// update the list
				spinner.setSelection(0);
				break;
			}
		}
		refreshPlacesList();
	}

	private void refreshPlacesList() {
		int position = spinner.getSelectedItemPosition();
		if(position == AdapterView.INVALID_POSITION || spinner.getSelectedItem() == null) {
			position = 0;
		}
		spinner.setAdapter(new PlacesAdapter(this, true));
		spinner.setSelection(position);
		refreshCompass();
		refreshMap();
	}
	
	private void refreshCompass() {
        if (compassView != null) {
        	compassView.setCurrentLocation(currentLocation);
        	compassView.setCurrentPlace((Place) spinner.getSelectedItem());
        	compassView.invalidate();
        }
	}

	private void refreshMap() {
		if(mapView != null) {
	    	// add places to the map
		    // clear previous overlays
	        List<Overlay> listOfOverlays = mapView.getOverlays();
	        listOfOverlays.clear();
	        mapView.invalidate();
	        Location currentPlace = ((Place) spinner.getSelectedItem()).getLocation();
	        listOfOverlays.add(new MapMarker(this, currentPlace, true));
	        ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();
	        points.add(Utils.convertToGeoPoint(currentPlace));
	        if(currentLocation != null) {
		        listOfOverlays.add(new MapMarker(this, currentLocation, false));
		        // draw line between the two
		        listOfOverlays.add(new LineOverlay(this, currentPlace, currentLocation));
		        points.add(Utils.convertToGeoPoint(currentLocation));
	        } else {
	        	Utils.toast(this, "Unknown Current Location!");
	        }
	        Utils.setMapBoundsToPois(mapController, points, 0.1, 0.1);
		}
	}

	private void setMode(boolean compass) {
		this.compass = compass;
		modeButton.setBackgroundResource(compass ? R.drawable.ic_menu_mapmode : R.drawable.ic_menu_compass);
		((TextView) findViewById(R.id.textviewas)).setText(compass ? "View as Map" : "View as Compass");
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the currently selected menu XML resource.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings: {
            	final Dialog dialog = new Dialog(this);

            	dialog.setContentView(R.layout.places_dialog);
            	dialog.setTitle("Remove Places");

            	ImageButton start = (ImageButton) dialog.findViewById(R.id.okbutton);
            	start.setOnClickListener(new OnClickListener() {
    				
    				@Override
    				public void onClick(View v) {
    					dialog.cancel();
    				}
    			});
            	ListView listView = (ListView) dialog.findViewById(R.id.placeslist);
            	final PlacesAdapter adapter = new PlacesAdapter(this, false);
            	listView.setAdapter(adapter);
            	listView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long id) {
						Place place = (Place) adapter.getItem(position);
				    	PlacesAdapter.removePlace(Compastic.this, place);
    					dialog.cancel();
    					refreshPlacesList();
					}

				});
            	dialog.show();
            } break;
            case R.id.about: {
            	final Dialog dialog = new Dialog(this);

            	dialog.setContentView(R.layout.about_dialog);
            	dialog.setTitle("About Compastic");

            	ImageButton start = (ImageButton) dialog.findViewById(R.id.okbutton);
            	start.setOnClickListener(new OnClickListener() {
    				
    				@Override
    				public void onClick(View v) {
    					dialog.cancel();
    				}
    			});
            	dialog.show();
            } break;
            default:
                break;
        }
        
        return false;
    }
    
}
