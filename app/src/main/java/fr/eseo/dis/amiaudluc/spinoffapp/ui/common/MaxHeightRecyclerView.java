package fr.eseo.dis.amiaudluc.spinoffapp.ui.common;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A RecyclerView that wraps its content but never grows taller than a fraction of the screen.
 * Beyond that height it scrolls internally, so very long lists (e.g. seasons with thousands of
 * episodes) do not force the parent NestedScrollView to measure every single item.
 */
public class MaxHeightRecyclerView extends RecyclerView {

    private int maxHeight;

    public MaxHeightRecyclerView(Context context) {
        this(context, null);
    }

    public MaxHeightRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaxHeightRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.maxHeight = (int) (getResources().getDisplayMetrics().heightPixels * 0.6f);
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int mode = MeasureSpec.getMode(heightSpec);
        if (mode != MeasureSpec.EXACTLY && MeasureSpec.getSize(heightSpec) > maxHeight) {
            heightSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthSpec, heightSpec);
    }
}
