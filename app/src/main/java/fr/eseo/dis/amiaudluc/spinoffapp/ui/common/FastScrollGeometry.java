package fr.eseo.dis.amiaudluc.spinoffapp.ui.common;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Geometry of a scroll bar: where the thumb sits for a given list, and which item a touch targets.
 * <p>
 * Pure and side-effect free, so it can be unit tested and reused by any bar implementation.
 * <p>
 * The position is expressed as a <b>fraction of the visible range in items</b>, not in pixels:
 * {@link RecyclerView#computeVerticalScrollRange()} is only an estimate for the items the
 * layout manager has not measured yet, so a pixel ratio drifts and makes the list jump when the
 * content is long. An item ratio stays stable whatever the list length.
 */
public final class FastScrollGeometry {

    private FastScrollGeometry() {
    }

    /**
     * Fraction of the item range already scrolled, between 0 and 1. When the list cannot provide
     * its layout manager, it falls back to the pixel ratio of the RecyclerView.
     */
    public static float progress(RecyclerView list) {
        LinearLayoutManager layoutManager = layoutManagerOf(list);
        if (layoutManager == null) {
            return pixelProgress(list);
        }

        int count = layoutManager.getItemCount();
        int first = layoutManager.findFirstVisibleItemPosition();
        int last = layoutManager.findLastVisibleItemPosition();
        if (count == 0 || first == RecyclerView.NO_POSITION) {
            return 0f;
        }
        if (last == RecyclerView.NO_POSITION || last < first) {
            return 0f;
        }

        int visible = last - first + 1;
        int scrollable = count - visible;
        if (scrollable <= 0) {
            return 0f;
        }
        return clamp(first / (float) scrollable);
    }

    /**
     * Height of the thumb: it shrinks as the content grows, but never below {@code minHeight} so
     * it stays grabbable on a long list.
     */
    public static float thumbHeight(RecyclerView list, float available, float minHeight) {
        LinearLayoutManager layoutManager = layoutManagerOf(list);
        if (layoutManager == null) {
            return Math.min(minHeight, available);
        }
        int count = layoutManager.getItemCount();
        int first = layoutManager.findFirstVisibleItemPosition();
        int last = layoutManager.findLastVisibleItemPosition();
        if (count <= 0 || first == RecyclerView.NO_POSITION || last == RecyclerView.NO_POSITION) {
            return Math.min(minHeight, available);
        }
        int visible = last - first + 1;
        return Math.min(available, Math.max(minHeight, available * (visible / (float) count)));
    }

    /** Top edge of the thumb inside a track of the given height. */
    public static float thumbTop(RecyclerView list, float trackHeight, float trackInset,
                                 float minHeight) {
        float available = trackHeight - 2 * trackInset;
        float thumbHeight = thumbHeight(list, available, minHeight);
        return trackInset + (available - thumbHeight) * progress(list);
    }

    /** Whether the list has enough content to be scrolled. */
    public static boolean isScrollable(RecyclerView list) {
        return list.computeVerticalScrollRange() > list.computeVerticalScrollExtent();
    }

    /**
     * Item index the thumb should point at when the touch is at {@code y} on a track of the given
     * height. This is the value to hand to {@link #scrollToTouch}, not a pixel offset.
     */
    public static int indexForTouch(RecyclerView list, float y, float trackHeight,
                                    float trackInset, float minHeight) {
        LinearLayoutManager layoutManager = layoutManagerOf(list);
        if (layoutManager == null) {
            return 0;
        }
        int count = layoutManager.getItemCount();
        if (count == 0) {
            return 0;
        }

        float thumbHeight = thumbHeight(list, trackHeight - 2 * trackInset, minHeight);
        float travel = trackHeight - 2 * trackInset - thumbHeight;
        if (travel <= 0) {
            return layoutManager.findFirstVisibleItemPosition();
        }

        float ratio = clamp((y - trackInset - thumbHeight / 2f) / travel);
        int first = layoutManager.findFirstVisibleItemPosition();
        int last = layoutManager.findLastVisibleItemPosition();
        int visible = (first == RecyclerView.NO_POSITION || last == RecyclerView.NO_POSITION)
                ? 1 : last - first + 1;
        int scrollable = count - visible;
        if (scrollable <= 0) {
            return 0;
        }
        return Math.round(ratio * scrollable);
    }

    /**
     * Moves the list so the item at {@code targetIndex} sits under the thumb. A single
     * {@code scrollToPosition} is used instead of a pixel {@code scrollBy}: it does not depend on
     * the estimated content height, so the list does not jump on long content.
     */
    public static void scrollToTouch(RecyclerView list, int targetIndex) {
        LinearLayoutManager layoutManager = layoutManagerOf(list);
        if (layoutManager == null) {
            return;
        }
        int count = layoutManager.getItemCount();
        if (count == 0) {
            return;
        }
        int index = Math.max(0, Math.min(count - 1, targetIndex));
        if (Math.abs(index - layoutManager.findFirstVisibleItemPosition()) <= 1) {
            return; // already there: avoid re-laying out for nothing
        }
        list.scrollToPosition(index);
    }

    private static LinearLayoutManager layoutManagerOf(RecyclerView list) {
        RecyclerView.LayoutManager layoutManager = list.getLayoutManager();
        return layoutManager instanceof LinearLayoutManager manager ? manager : null;
    }

    /** Fallback position used when the layout manager is not a LinearLayoutManager. */
    private static float pixelProgress(RecyclerView list) {
        int range = list.computeVerticalScrollRange();
        int scrollable = range - list.computeVerticalScrollExtent();
        if (scrollable <= 0) {
            return 0f;
        }
        return clamp(list.computeVerticalScrollOffset() / (float) scrollable);
    }

    private static float clamp(float value) {
        return Math.max(0f, Math.min(1f, value));
    }
}
