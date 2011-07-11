package com.metaaps.mobile.compastic;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Location;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;

public class Utils {
	
	// this function was retrieved from the blog post http://burcudogan.com/2010/04/
	public static void setMapBoundsToPois(MapController mapController, List<GeoPoint> items, double hpadding, double vpadding) {

	    // If there is only on one result
	    // directly animate to that location
	    if (items.size() == 1) { // animate to the location
	        mapController.animateTo(items.get(0));
	        mapController.setZoom(5);
	    } else {
	        // find the lat, lon span
	        int minLatitude = Integer.MAX_VALUE;
	        int maxLatitude = Integer.MIN_VALUE;
	        int minLongitude = Integer.MAX_VALUE;
	        int maxLongitude = Integer.MIN_VALUE;
	
	        // Find the boundaries of the item set
	        for (GeoPoint item : items) {
	            int lat = item.getLatitudeE6(); int lon = item.getLongitudeE6();
	
	            maxLatitude = Math.max(lat, maxLatitude);
	            minLatitude = Math.min(lat, minLatitude);
	            maxLongitude = Math.max(lon, maxLongitude);
	            minLongitude = Math.min(lon, minLongitude);
	        }
	
	        // leave some padding from corners
	        // such as 0.1 for hpadding and 0.2 for vpadding
	        maxLatitude = maxLatitude + (int)((maxLatitude-minLatitude)*hpadding);
	        minLatitude = minLatitude - (int)((maxLatitude-minLatitude)*hpadding);
	
	        maxLongitude = maxLongitude + (int)((maxLongitude-minLongitude)*vpadding);
	        minLongitude = minLongitude - (int)((maxLongitude-minLongitude)*vpadding);
	
	        // Calculate the lat, lon spans from the given pois and zoom
	        mapController.zoomToSpan(Math.abs(maxLatitude - minLatitude), Math.abs(maxLongitude - minLongitude));
	
	        // Animate to the center of the cluster of points
	        mapController.animateTo(new GeoPoint(
	              (maxLatitude + minLatitude) / 2, (maxLongitude + minLongitude) / 2));
	    }
	} // end of the method

	public static GeoPoint convertToGeoPoint(Location location) {
		return new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));
	}

	public static GeoPoint convertToGeoPoint(Address address) {
		return new GeoPoint((int) (address.getLatitude() * 1E6), (int) (address.getLongitude() * 1E6));
	}

	public static void alert(Context context, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message)
		       .setCancelable(false)
		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}

    public static void toast(Context context, String text) {
    	Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
    	toast.show();
	}

}
