package com.metaaps.mobile.compastic.widgets;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import com.metaaps.mobile.compastic.R;
import com.metaaps.mobile.compastic.R.drawable;
import com.metaaps.mobile.compastic.R.id;
import com.metaaps.mobile.compastic.R.layout;
import com.metaaps.mobile.compastic.widgets.entities.Place;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PlacesAdapter extends BaseAdapter {

	private static final String PLACES_FILE_NAME = "compastic_places";
	static List<Place> places;
	private LayoutInflater inflater;
	private Bitmap addBitmap;
	private Context context;
	private boolean withAddPlace;

	public PlacesAdapter(Context context, boolean withAddPlace) {
		super();
		this.context = context;
	    // Cache the LayoutInflate to avoid asking for a new one each time.
	    inflater = LayoutInflater.from(context);
	    if(places == null) {
	    	places = loadPlaces(context);
	    }
	    addBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.addplace);
	    this.withAddPlace = withAddPlace;
	}
	
	/**
	 * The number of items in the list is determined by the number of speeches
	 * in our array.
	 *
	 * @see android.widget.ListAdapter#getCount()
	 */
	public int getCount() {
		if(withAddPlace) {
	        return places.size() + 1;
		} else {
	        return places.size();
		}
	}
	
	/**
	 * Since the data comes from an array, just returning the index is
	 * sufficent to get at the data. If we were using a more complex data
	 * structure, we would return whatever object represents one row in the
	 * list.
	 *
	 * @see android.widget.ListAdapter#getItem(int)
	 */
	public Object getItem(int position) {
		if(position == places.size()) {
			return null;
		}
	    return places.get(position);
	}
	
	/**
	 * Use the array index as a unique id.
	 *
	 * @see android.widget.ListAdapter#getItemId(int)
	 */
	public long getItemId(int position) {
		if(position == places.size()) {
			return -1L;
		}
	    return position;
	}
	
	/**
	 * Make a view to hold each row.
	 *
	 * @see android.widget.ListAdapter#getView(int, android.view.View,
	 *      android.view.ViewGroup)
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
	    // A ViewHolder keeps references to children views to avoid unneccessary calls
	    // to findViewById() on each row.
	    ViewHolder holder;
	
	    // When convertView is not null, we can reuse it directly, there is no need
	    // to reinflate it. We only inflate a new View when the convertView supplied
	    // by ListView is null.
	    if (convertView == null) {
	        convertView = inflater.inflate(R.layout.list_item_icon_text_spinner, null);
	
	        // Creates a ViewHolder and store references to the two children views
	        // we want to bind data to.
	        holder = new ViewHolder();
	        holder.text = (TextView) convertView.findViewById(R.id.text);
	        holder.icon = (ImageView) convertView.findViewById(R.id.icon);
	
	        convertView.setTag(holder);
	    } else {
	        // Get the ViewHolder back to get fast access to the TextView
	        // and the ImageView.
	        holder = (ViewHolder) convertView.getTag();
	    }
	
		if(position == places.size()) {
	        // Bind the data efficiently with the holder.
	        holder.text.setText("Add New Place");
	        holder.icon.setImageBitmap(addBitmap);
	    } else {
	        // Bind the data efficiently with the holder.
	        holder.text.setText(places.get(position).getName());
	        holder.icon.setImageBitmap(places.get(position).getBitmap(context));
	    }
	
	    return convertView;
	}
	
	static class ViewHolder {
	    TextView text;
	    ImageView icon;
	}

	static public List<Place> loadPlaces(Context context) {
		FileInputStream fis;
		try {
			fis = context.openFileInput(PLACES_FILE_NAME);
			ObjectInputStream objIn = new ObjectInputStream(fis);
			Object obj = objIn.readObject();
			if(obj instanceof List) {
				places = (List<Place>) obj;
			}
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(places == null) {
			places = new ArrayList<Place>();
			// initialise with a set of default places
			places.add(new Place("Paris, France", 48.869683, 2.352448, "default", false));
			places.add(new Place("Madrid, Spain", 40.396764, -3.713379, "default", false));
			places.add(new Place("Manhattan, New York, USA", 40.798737, -73.962021, "default", false));
		}
		
		return places;
	}

	public static void savePlaces(Context context) {
		if(places == null) {
			return;
		}
		FileOutputStream fos;
		try {
			fos = context.openFileOutput(PLACES_FILE_NAME, Context.MODE_PRIVATE);
			ObjectOutputStream objOut = new ObjectOutputStream(fos);
			objOut.writeObject(places);
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void addPlace(Context context, Place place) {
		if(places == null) {
			places = loadPlaces(context);
		}
		// add at the beginning
		places.add(0, place);
		savePlaces(context);
	}

	public static void removePlace(Context context, Place placeremove) {
		places.remove(placeremove);
		savePlaces(context);
	}
	
}
