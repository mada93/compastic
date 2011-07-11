package com.metaaps.mobile.compastic.widgets.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.metaaps.mobile.compastic.Compastic;
import com.metaaps.mobile.compastic.R;
import com.metaaps.mobile.compastic.Utils;
import com.metaaps.mobile.compastic.R.drawable;

/**
 * Overlay class equivalent to a Marker
 * 
 * @author Thomas Lefort
 *
 */
public class MapMarker extends com.google.android.maps.Overlay
{
    private GeoPoint geoPoint;
	private Bitmap bitmap;
    
    public MapMarker(Context context, Location location, boolean start) {
    	if(bitmap == null) {
        	this.bitmap = start ? BitmapFactory.decodeResource(context.getResources(), R.drawable.mapiconstart) : BitmapFactory.decodeResource(context.getResources(), R.drawable.mapiconstop);
    	}
    	this.geoPoint = Utils.convertToGeoPoint(location);
	}

	@Override
    public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) 
    {
        super.draw(canvas, mapView, shadow);                   

        //---translate the GeoPoint to screen pixels---
        Point screenPts = new Point();
        mapView.getProjection().toPixels(this.geoPoint, screenPts);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //---add the marker---
        canvas.drawBitmap(bitmap, screenPts.x - bitmap.getWidth() / 2, screenPts.y - bitmap.getHeight() / 2, null);         
        return true;
    }

} 

