package sailloft.dickielewis.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import sailloft.dickielewis.R;

/**
 * Created by David's Laptop on 4/4/2015.
 */
public class MashListAdapter extends ArrayAdapter<HashMap<String, String>> {
protected Context mContext;
protected ArrayList<HashMap<String, String>> mTimers;

public MashListAdapter(Context context, ArrayList<HashMap<String, String>> timers){
        super(context, R.layout.timer_item, timers);
        mContext = context;
        mTimers = timers;
        }

@Override
public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.timer_item, null);
        holder = new ViewHolder();
        holder.timeLabel = (TextView)convertView.findViewById(R.id.timeLabel);
        holder.addInfoLabel = (TextView)convertView.findViewById(R.id.addInfoLabel);

        convertView.setTag(holder);
        }
        else{
        holder = (ViewHolder)convertView.getTag();


        }
        HashMap<String, String> timer = mTimers.get(position);

        holder.timeLabel.setText(timer.get("KEY_TEMP") + "\u2109");
        holder.addInfoLabel.setText("Hold for " + timer.get("KEY_MASH_LENGTH")+ " minutes");

        return convertView;
        }

private static class ViewHolder{

    TextView timeLabel;
    TextView addInfoLabel;


}
    public void refill(ArrayList<HashMap<String, String>> timers){
        mTimers.clear();
        mTimers.addAll(timers);
        notifyDataSetChanged();
    }
}