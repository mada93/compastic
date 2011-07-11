package com.metaaps.mobile.compastic.widgets.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.location.Address;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.metaaps.mobile.compastic.R;

/**
 * Overlay class equivalent to a Marker
 * 
 * @author Thomas Lefort
 *
 */
public class Marker extends com.google.android.maps.Overlay
{
    private GeoPoint geoPoint;
	private static Bitmap bitmap;
	private Context context;
	private String placeName;
	private int index;
    
    public Marker(Context context, Address address, int index) {
    	this.context = context;
    	if(bitmap == null) {
        	this.bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.mapbkicon);
    	}
    	this.geoPoint = new GeoPoint((int) (address.getLatitude() * 1E6), (int) (address.getLongitude() * 1E6));
    	this.index = index;
	}

	@Override
    public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) 
    {
        super.draw(canvas, mapView, shadow);                   

        //---translate the GeoPoint to screen pixels---
        Point screenPts = new Point();
        mapView.getProjection().toPixels(this.geoPoint, screenPts);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(bitmap.getHeight() / 2);
        paint.setColor(Color.DKGRAY);
        paint.setTextAlign(Align.CENTER);
        //---add the marker---
        canvas.drawBitmap(bitmap, screenPts.x - bitmap.getWidth() / 2, screenPts.y - bitmap.getHeight(), null);         
        canvas.drawText(index + "", screenPts.x, screenPts.y - bitmap.getHeight() / 2, paint);
        return true;
    }

}
