package com.smritiraksha;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
public class WordSearchAdapter extends  BaseAdapter {
    private Context context;
    private String[] gridData;

    public WordSearchAdapter(Context context, String[] gridData) {
        this.context = context;
        this.gridData = gridData;
    }

    @Override
    public int getCount() {
        return gridData.length;
    }

    @Override
    public Object getItem(int position) {
        return gridData[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            textView = new TextView(context);
            textView.setTextSize(20);
            textView.setPadding(10, 10, 10, 10);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            textView = (TextView) convertView;
        }

        textView.setText(gridData[position]);

        // If word is selected, highlight it
        textView.setBackgroundColor(Color.TRANSPARENT);

        return textView;
    }
}
