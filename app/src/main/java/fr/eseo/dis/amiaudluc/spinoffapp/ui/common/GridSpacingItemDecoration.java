package fr.eseo.dis.amiaudluc.spinoffapp.ui.common;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private final int spanCount;
    private final int spacing;

    public GridSpacingItemDecoration(int spanCount, int spacing) {
        this.spanCount = spanCount;
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position >= 0) {
            int column = position % spanCount;

            // spacing - column * ((1f / spanCount) * spacing)
            outRect.left = column * spacing / spanCount;
            // (column + 1) * ((1f / spanCount) * spacing)
            outRect.right = spacing - (column + 1) * spacing / spanCount;

            if (position >= spanCount) {
                outRect.top = spacing;
            }
        } else {
            outRect.left = 0;
            outRect.right = 0;
            outRect.top = 0;
            outRect.bottom = 0;
        }
    }
}
