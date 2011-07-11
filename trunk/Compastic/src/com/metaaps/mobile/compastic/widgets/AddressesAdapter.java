package com.metaaps.mobile.compastic.widgets;

import java.util.List;

import com.metaaps.mobile.compastic.R;
import com.metaaps.mobile.compastic.R.drawable;
import com.metaaps.mobile.compastic.R.id;
import com.metaaps.mobile.compastic.R.layout;
import com.metaaps.mobile.compastic.widgets.entities.Place;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AddressesAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<Address> addresses;
	private Bitmap noPlaceBitmap;
	private Bitmap placeBitmap;
	
	public AddressesAdapter(Context context, List<Address> addresses) {
	    // Cache the LayoutInflate to avoid asking for a new one each time.
	    inflater = LayoutInflater.from(context);
    	this.addresses = addresses;
    	noPlaceBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.addplace);
    	placeBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.addplace);
	}
	
	/**
	 * The number of items in the list is determined by the number of speeches
	 * in our array.
	 *
	 * @see android.widget.ListAdapter#getCount()
	 */
	public int getCount() {
		if(addresses == null || addresses.size() == 0) {
			return 1;
		}
        return addresses.size();
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
		if(addresses == null || addresses.size() == 0) {
			return null;
		}
	    Address address = addresses.get(position);
	    return new Place(address.getAddressLine(0), address.getLatitude(), address.getLongitude(), "Google", true);
	}
	
	/**
	 * Use the array index as a unique id.
	 *
	 * @see android.widget.ListAdapter#getItemId(int)
	 */
	public long getItemId(int position) {
		if(addresses == null || addresses.size() == 0) {
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
	        convertView = inflater.inflate(R.layout.list_item_text, null);
	
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
	
		if(addresses == null || addresses.size() == 0) {
	        holder.text.setText("No place...");
	    } else {
	    	String add = position + ") ";
            for (int i = 0; i < addresses.get(position).getMaxAddressLineIndex(); i++) {
            	if(add != null) {
                	add += addresses.get(position).getAddressLine(i) + ", ";
            	}
            }
	        holder.text.setText(add);
	    }
	
	    return convertView;
	}
	
	static class ViewHolder {
	    TextView text;
	    ImageView icon;
	}

}
