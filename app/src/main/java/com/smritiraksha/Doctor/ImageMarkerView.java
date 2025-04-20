package com.smritiraksha.Doctor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.smritiraksha.R;

public class ImageMarkerView extends MarkerView {
    private ImageView imageView;

    public ImageMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        imageView = findViewById(R.id.markerImage);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        super.refreshContent(e, highlight);

        // Set image for each data point
        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.patientwalk);
        imageView.setImageBitmap(bitmap); // Set the image for each point
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
