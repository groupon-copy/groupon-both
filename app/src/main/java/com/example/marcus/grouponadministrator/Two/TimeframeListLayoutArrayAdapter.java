package com.example.marcus.grouponadministrator.Two;

/**
 * Created by Marcus Chiu on 4/21/2016.
 */
/*public class TimeframeListLayoutArrayAdapter extends ArrayAdapter<TimeFrameEditList.TimeFrameEdit> implements View.OnClickListener
{
    public TimeframeListLayoutArrayAdapter(Context context, int resource, List<TimeFrameEditList.TimeFrameEdit> objects)
    {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;

        if (convertView == null)
        {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.two_fragment_main_screen_time_frame_edit, parent, false);
            holder.upperTextView = (TextView)convertView.findViewById(R.id.mainScreenTimeframeEditFragment_upperText);
            holder.lowerTextView = (TextView)convertView.findViewById(R.id.mainScreenTimeframeEditFragment_lowerText);
            holder.tagListTextView = (TextView)convertView.findViewById(R.id.mainScreenTimeframeEditFragment_tagListTextView);
            holder.onOffSwitch = (Switch)convertView.findViewById(R.id.mainScreenTimeframeEditFragment_onOffSwitch);
            holder.leftTextView = (TextView)convertView.findViewById(R.id.mainScreenTimeframeEditFragment_moveTextView);
            holder.middleBodyLL = (LinearLayout)convertView.findViewById(R.id.mainScreenTimeframeEditFragment_middleBodyLL);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        TimeFrameEditList.TimeFrameEdit t = getItem(position);
        holder.tagListTextView.setText(t.name);
        holder.upperTextView.setText(t.startHour);
        holder.lowerTextView.setText(t.name);
        holder.onOffSwitch.setChecked(t.ONorOFF);

        //set OnClickListener
        holder.onOffSwitch.setOnClickListener(this);
        holder.leftTextView.setOnClickListener(this);
        holder.middleBodyLL.setOnClickListener(this);

        return convertView;
    }

    @Override
    public void onClick(View v)
    {
        //final int position = mListView.getPositionForView((View) v.getParent());
    }

    static class ViewHolder
    {
        TextView upperTextView;
        TextView lowerTextView;
        TextView tagListTextView;
        Switch onOffSwitch;
        TextView leftTextView;
        LinearLayout middleBodyLL;
    }
}*/
