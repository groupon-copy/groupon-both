package com.example.marcus.grouponadministrator.One;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.marcus.grouponadministrator.R;
import com.example.marcus.grouponadministrator.Database.Deal;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class DealListLayoutArrayAdapter extends ArrayAdapter<Deal>
{
    Context context;

    public DealListLayoutArrayAdapter(Context context, int resource, List<Deal> objects)
    {
        super(context, resource, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;

        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.one_fragment_deal_large, parent, false);
            holder = new ViewHolder();
            holder.boldTextView = (TextView)convertView.findViewById(R.id.mainFeedDealFragment_boldText);
            holder.fineTextView = (TextView)convertView.findViewById(R.id.mainFeedDealFragment_fineText);
            holder.bottomTextView = (TextView)convertView.findViewById(R.id.mainFeedDealFragment_bottomText);
            holder.imageView = (ImageView)convertView.findViewById(R.id.mainFeedDealFragment_imageView);
            holder.currentPriceTextView = (TextView)convertView.findViewById(R.id.mainFeedDealFragment_CurrentPrice);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        //get deal object
        Deal deal = getItem(position);

        //display deal data onto views
        holder.boldTextView.setText(deal.getBoldText());
        holder.fineTextView.setText(deal.getHighlightText());
        holder.bottomTextView.setText(deal.getValidUntil());
        holder.currentPriceTextView.setText("$" + deal.getCurrentPrice());

        //load image externally into ImageView TODO can tweak resize
        Picasso.with(context)
                .load(deal.getImageURL())
                //.centerCrop() //for some reason it crashes without resize()
                //.resize(500,500)
                .placeholder(R.drawable.ghoomo)
                .error(R.drawable.no_image)
                .into(holder.imageView);

        return convertView;
    }

    static class ViewHolder
    {
        ImageView imageView;
        TextView boldTextView;
        TextView fineTextView;
        TextView bottomTextView;
        TextView currentPriceTextView;
    }
}
