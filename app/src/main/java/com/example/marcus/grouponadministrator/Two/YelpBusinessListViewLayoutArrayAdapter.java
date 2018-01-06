package com.example.marcus.grouponadministrator.Two;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.marcus.grouponadministrator.R;
import com.squareup.picasso.Picasso;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.Category;

import java.util.List;

/**
 * Created by Marcus Chiu on 4/21/2016.
 */
public class YelpBusinessListViewLayoutArrayAdapter extends ArrayAdapter<Business>
{
    public YelpBusinessListViewLayoutArrayAdapter(Context context, int resource, List<Business> objects)
    {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;

        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.two_fragment_yelp_business, parent, false);
            holder = new ViewHolder();
            holder.nameTextView = (TextView)convertView.findViewById(R.id.yelpBusinessFragment_nameTextView);
            holder.distanceTextView = (TextView)convertView.findViewById(R.id.yelpBusinessFragment_distanceTextView);
            holder.locationTextView = (TextView)convertView.findViewById(R.id.yelpBusinessFragment_locationTextView);
            holder.imageView = (ImageView)convertView.findViewById(R.id.yelpBusinessFragment_image);
            holder.numReviewsTextView = (TextView)convertView.findViewById(R.id.yelpBusinessFragment_numReviewsTextView);
            holder.ratingBar = (RatingBar)convertView.findViewById(R.id.yelpBusinessFragment_ratingBar);
            holder.tagsTextView = (TextView)convertView.findViewById(R.id.yelpBusinessFragment_tagsTextView);
            holder.isOpenTextView = (TextView)convertView.findViewById(R.id.yelpBusinessFragment_isOpenTextView);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        //get Business Object
        Business b = getItem(position);

        if(b != null)
        {
            holder.nameTextView.setText(b.name());
            holder.distanceTextView.setText(String.format("%.1f", (b.distance()/1609.34f)) + "mi");
            holder.numReviewsTextView.setText(Integer.toString(b.reviewCount()));
            holder.ratingBar.setRating((float) (b.rating().doubleValue()));

            //set tags/categories
            String categories = "";
            for(Category s: b.categories()) categories += s.name() + " ";
            holder.tagsTextView.setText(categories);

            if(b.isClosed())
            {
                holder.isOpenTextView.setText("CLOSED");
                holder.isOpenTextView.setTextColor(ContextCompat.getColor(convertView.getContext(), R.color.businessClosedText));
            }
            else
            {
                holder.isOpenTextView.setText("OPEN");
                holder.isOpenTextView.setTextColor(ContextCompat.getColor(convertView.getContext(), R.color.businessOpenText));
            }

            if(b.location() != null)
                holder.locationTextView.setText(b.location().displayAddress().get(0));
            else
            {
                //set something else
            }

            //load image externally into ImageView TODO can tweak resize
            Picasso.with(convertView.getContext())
                    .load(b.imageUrl())
                    //.centerCrop() //for some reason it crashes without resize()
                    //.resize(500,500)
                    .placeholder(R.drawable.ghoomo)
                    .error(R.drawable.no_image)
                    .into(holder.imageView);
        }

        return convertView;
    }

    static class ViewHolder
    {
        ImageView imageView;
        TextView nameTextView;
        TextView distanceTextView;
        TextView locationTextView;
        TextView numReviewsTextView;
        TextView isOpenTextView;
        RatingBar ratingBar;
        TextView tagsTextView;
    }
}
