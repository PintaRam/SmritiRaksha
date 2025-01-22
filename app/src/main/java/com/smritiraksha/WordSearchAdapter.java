package com.smritiraksha;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.List;

public class WordSearchAdapter extends BaseAdapter {
    private Context context;
    private List<Character> gridData;

    public WordSearchAdapter(Context context, List<Character> gridData) {
        this.context = context;
        this.gridData = gridData;
    }

    @Override
    public int getCount() {
        return gridData.size();
    }

    @Override
    public Object getItem(int position) {
        return gridData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView cell;
        if (convertView == null) {
            cell = new TextView(context);
            cell.setLayoutParams(new GridView.LayoutParams(100, 100)); // Adjust size
            cell.setGravity(Gravity.CENTER);
            cell.setTextSize(18);
        } else {
            cell = (TextView) convertView;
        }
        cell.setText(String.valueOf(gridData.get(position)));
        return cell;
    }
}
