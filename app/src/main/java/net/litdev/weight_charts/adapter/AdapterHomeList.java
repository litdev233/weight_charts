package net.litdev.weight_charts.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.litdev.weight_charts.R;
import net.litdev.weight_charts.entity.WeightData;
import net.litdev.weight_charts.utils.UtilsDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by litde on 2016/5/16.
 */
public class AdapterHomeList extends BaseAdapter {
    private List<WeightData> lists;
    private Context context;

    public AdapterHomeList(List<WeightData> list,Context context){
        this.lists = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return lists == null ? 0 : lists.size();
    }

    @Override
    public WeightData getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        WeightData model = getItem(position);
        if(convertView == null){
            holder =new ViewHolder();
            convertView = View.inflate(context, R.layout.item_home,null);
            holder.lab_weight = (TextView) convertView.findViewById(R.id.lab_weight);
            holder.lab_datetime = (TextView) convertView.findViewById(R.id.lab_datetime);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.lab_weight.setText(model.getWeight()+" kg");
        String date_str=  model.getAddTime().getDate();
        String short_time = UtilsDate.format(date_str);
        Date date = null;
        try {
            date = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(date_str);
        } catch (ParseException e) {
            e.printStackTrace();
            date = new Date();
        }
        String test= new SimpleDateFormat("MM-dd HH:mm").format(date);

        holder.lab_datetime.setText(short_time+"  ("+ test +")");
        return convertView;
    }

    public static class ViewHolder
    {
        public TextView lab_weight;
        public TextView lab_datetime;
    }
}
