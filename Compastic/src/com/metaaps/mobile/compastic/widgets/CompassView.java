package com.metaaps.mobile.compastic.widgets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.metaaps.mobile.compastic.Compastic;
import com.metaaps.mobile.compastic.R;
import com.metaaps.mobile.compastic.R.drawable;
import com.metaaps.mobile.compastic.widgets.entities.Declinations;
import com.metaaps.mobile.compastic.widgets.entities.Place;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.hardware.GeomagneticField;
import android.location.Location;
import android.util.AttributeSet;
import android.view.View;

public class CompassView extends View {
	
	List<Place> places = new ArrayList<Place>();
	private Location currentLocation;
	private float north = (float) (- Math.PI / 6);
	private Paint paint;
	private Path arrowPath = null;
	private Bitmap canvasBitmap;
	private Place currentPlace;
	
	public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
	
	@Override protected void onDraw(Canvas canvas) {

    	paint = new Paint();
    	
        canvas.drawColor(Color.TRANSPARENT);

        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);

        drawCompass(canvas);
        drawArrow(canvas, currentPlace);
        drawCenter(canvas);
    }

	private void drawCenter(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();
        int cx = w / 2;
        int cy = h / 2;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int radius = w / 15;
        paint.setColor(Color.WHITE);
        paint.setStyle(Style.FILL);
        canvas.drawArc(new RectF(cx - radius, cy - radius, cx + radius, cy + radius), 0, 360, false, paint);
        paint.setColor(Color.BLACK);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(2);
        canvas.drawArc(new RectF(cx - radius, cy - radius, cx + radius, cy + radius), 0, 360, false, paint);
	}

	private void drawArrow(Canvas canvas, Place place) {
        int w = getWidth();
        int h = getHeight();
        int cx = w / 2;
        int cy = h / 2;

        if (currentLocation != null && currentPlace != null) {
        	if(arrowPath == null) {
        		int height = (int) (Math.min(w / 2, h / 2));
        		int width = height / 5;
                int arrowWidth = 2 * width;
                int arrowHeight = arrowWidth / 2;
                arrowPath = new Path();
                // Construct an arrow path
                arrowPath.moveTo(- width / 2, height / 3);
                arrowPath.lineTo(width / 2, height / 3);
                arrowPath.lineTo(width / 2, - height * 2 / 3);
				arrowPath.lineTo(arrowWidth / 2, - height * 2 / 3);
				arrowPath.lineTo(0, - height * 2 / 3 - arrowHeight);
                arrowPath.lineTo(- arrowWidth / 2, - height * 2 / 3);
                arrowPath.lineTo(- width / 2, - height * 2 / 3);
                arrowPath.lineTo(- width / 2, height / 3);
                arrowPath.close();
        	}

        	canvas.save();
            canvas.translate(cx, cy);
        	// calculate bearing
        	float bearing = currentLocation.bearingTo(place.getLocation());
        	// now get the declination angle
        	float declination = 0.0f;
    		GeomagneticField geoMagneticField = new GeomagneticField((float) currentLocation.getLatitude(), (float) currentLocation.getLongitude(), (float) currentLocation.getAltitude(), new Date().getTime());
    		declination = geoMagneticField.getDeclination();
        	// calculate the new angle to the true north in degrees
        	float MNBearing = (float) (north + declination + bearing);
        	// rotate to be aligned with the true north
            canvas.rotate(MNBearing);
            // draw the arrow
            paint.setColor(Color.parseColor("#bdc0dc"));
            paint.setStyle(Style.FILL);
            canvas.drawPath(arrowPath, paint);
            paint.setColor(Color.BLACK);
            paint.setStyle(Style.STROKE);
            paint.setStrokeWidth(2);
            canvas.drawPath(arrowPath, paint);
        	float distance = currentLocation.distanceTo(place.getLocation());
        	String distanceStr = "Distance: ";
        	if(distance < 10000) {
        		distanceStr += (int) distance + " m";
        	} else {
        		distanceStr += (int) (distance / 1000) + " km";
        	}
        	canvas.restore();
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setTextSize(15);
            paint.setColor(Color.DKGRAY);
            paint.setTextAlign(Align.LEFT);
        	canvas.drawText(distanceStr, 5, 25, paint);
            paint.setTextSize(15);
        	canvas.drawText("Your Loc: (" + ((int) (currentLocation.getLatitude() * 100)) / 100.0 + "º, " + ((int) (currentLocation.getLongitude() * 100)) / 100.0 + "º, " + (int) currentLocation.getAltitude() + "m)", 5, h - 20, paint);
        } else if(currentLocation == null) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setTextSize(15);
            paint.setColor(Color.RED);
            paint.setTextAlign(Align.LEFT);
        	canvas.drawText("Current Location is Unknown!", 5, h - 20, paint);
        }
	}

	private void drawCompass(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();
        int cx = w / 2;
        int cy = h / 2;
        
        if(canvasBitmap == null) {
        	canvasBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.compass);
        }

    	canvas.save();
        canvas.translate(cx, cy);
    	// rotate to be aligned with the true north
        canvas.rotate(north);
        float maxwidth = (float) (canvasBitmap.getWidth() * Math.sqrt(2));
        float maxheight = (float) (canvasBitmap.getHeight() * Math.sqrt(2));
        float ratio = Math.min(w / maxwidth, h / maxheight);
        int width = (int) (canvasBitmap.getWidth() * ratio);
        int height = (int) (canvasBitmap.getHeight() * ratio);
        // draw the compass
        canvas.drawBitmap(canvasBitmap, new Rect(0, 0, canvasBitmap.getWidth(), canvasBitmap.getHeight()), new Rect(- width / 2, - height/2, width / 2, height / 2), paint);
    	canvas.restore();
	}

	public void setNorth(float north) {
		this.north = north;
	}

	public void setCurrentLocation(Location location) {
		this.currentLocation = location;
	}

	public void setCurrentPlace(Place place) {
		currentPlace = place;
	}

}
