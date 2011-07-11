package com.metaaps.mobile.compastic.widgets.entities;

import java.io.Serializable;

import com.metaaps.mobile.compastic.R;
import com.metaaps.mobile.compastic.R.drawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;

public class Place implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String name;
	double lat;
	double lon;
	String provider;

	private boolean myPlace;
	
	public Place() {
	}
	
	public Place(String name, Location location, boolean myPlace) {
		super();
		this.name = name;
		lat = location.getLatitude();
		lon = location.getLongitude();
		provider = location.getProvider();
		this.myPlace = myPlace;
	}

	public Place(String name, double lat, double lon, String provider, boolean myPlace) {
		super();
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		provider = null;
		this.myPlace = myPlace;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Location getLocation() {
		Location location = new Location(provider);
		location.setLatitude(lat);
		location.setLongitude(lon);
		return location;
	}

	public Bitmap getBitmap(Context context) {
		return myPlace ? BitmapFactory.decodeResource(context.getResources(), R.drawable.myplace) : BitmapFactory.decodeResource(context.getResources(), R.drawable.place);
	}
	
}
