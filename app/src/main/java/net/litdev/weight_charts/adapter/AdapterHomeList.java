package net.litdev.weight_charts.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import net.litdev.weight_charts.entity.WeightData;

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
        return null;
    }
}
