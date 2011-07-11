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
import com.google.android.maps.Overlay;

public class LineOverlay extends Overlay {

    private GeoPoint startGeoPoint;
    private GeoPoint stopGeoPoint;
    
    public LineOverlay(Context context, Location startLocation, Location stopLocation) {
    	this.startGeoPoint = new GeoPoint((int) (startLocation.getLatitude() * 1E6), (int) (startLocation.getLongitude() * 1E6));
    	this.stopGeoPoint = new GeoPoint((int) (stopLocation.getLatitude() * 1E6), (int) (stopLocation.getLongitude() * 1E6));
	}

	@Override
    public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) 
    {
        super.draw(canvas, mapView, shadow);                   

        //---translate the GeoPoint to screen pixels---
        Point startScreenPts = new Point();
        mapView.getProjection().toPixels(this.startGeoPoint, startScreenPts);
        Point stopScreenPts = new Point();
        mapView.getProjection().toPixels(this.stopGeoPoint, stopScreenPts);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(3);
        paint.setStrokeCap(Paint.Cap.ROUND);
        //---add the marker---
        canvas.drawLine(startScreenPts.x, startScreenPts.y, stopScreenPts.x, stopScreenPts.y, paint);         
        return true;
    }

}
