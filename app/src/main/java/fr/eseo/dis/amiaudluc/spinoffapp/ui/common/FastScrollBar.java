package fr.eseo.dis.amiaudluc.spinoffapp.ui.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import fr.eseo.dis.amiaudluc.R;

/**
 * Minimal vertical fast-scroll bar drawn over a {@link RecyclerView}.
 * <p>
 * It replaces the {@code fastScrollEnabled} support of RecyclerView, which only arms its drag from
 * a transient visible state and whose touch target is the thumb width. This bar is always drawn,
 * always draggable, and its touch target is voluntarily wider than its visual width so it can be
 * grabbed with a finger.
 * <p>
 * It only paints and handles the drag: the geometry lives in {@link FastScrollGeometry}, so the
 * bar stays thin and easy to extend (sections, labels, orientation...).
 */
public class FastScrollBar extends View {

    /** Extra width around the drawn bar that still starts a drag, in dp. */
    private static final float TOUCH_SLOP_DP = 16f;
    /** Inset between the view edges and the bar, in dp. */
    private static final float EDGE_INSET_DP = 6f;
    /** Minimum drawn thumb height, so it stays usable when the list is long, in dp. */
    private static final float MIN_THUMB_HEIGHT_DP = 48f;
    /** Alpha applied to the track while idle, so it stays discreet behind the thumb. */
    private static final int IDLE_TRACK_ALPHA = 160;
    /** Alpha applied to the thumb while idle. */
    private static final int IDLE_THUMB_ALPHA = 230;

    private final Paint thumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    // Backing colours, so the dimmed alpha is applied without losing the original colour.
    private final int thumbColor;
    private final int trackColor;

    private final float touchSlop;
    private final float edgeInset;
    private final float minThumbHeight;
    private final float barWidth;

    @Nullable
    private RecyclerView recyclerView;
    private boolean dragging;

    public FastScrollBar(Context context) {
        this(context, null);
    }

    public FastScrollBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FastScrollBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        float density = getResources().getDisplayMetrics().density;
        this.touchSlop = TOUCH_SLOP_DP * density;
        this.edgeInset = EDGE_INSET_DP * density;
        this.minThumbHeight = MIN_THUMB_HEIGHT_DP * density;
        this.barWidth = getResources().getDimension(R.dimen.fast_scroll_bar_width);

        TypedArray styleable = context.obtainStyledAttributes(
                attrs, R.styleable.FastScrollBar, defStyleAttr, 0);
        try {
            this.thumbColor = resolveColor(styleable,
                    R.styleable.FastScrollBar_fastScrollThumbColor,
                    R.attr.fastScrollBarThumbColor,
                    R.color.colorAccent);
            this.trackColor = resolveColor(styleable,
                    R.styleable.FastScrollBar_fastScrollTrackColor,
                    R.attr.fastScrollBarTrackColor,
                    R.color.color_primary_semi_opaq);
        } finally {
            styleable.recycle();
        }

        setColors(thumbColor, trackColor);
        setWillNotDraw(false);
    }

    /**
     * Colours of the thumb and of the track. They come from the view attributes, then from the
     * theme, then from the hardcoded fallbacks, in that order.
     */
    public void setColors(int thumbColor, int trackColor) {
        thumbPaint.setColor(thumbColor);
        trackPaint.setColor(trackColor);
        invalidate();
    }

    /** Reads a colour from the view attributes, falling back to the theme then to a resource. */
    private int resolveColor(TypedArray styleable, int attrIndex, int themeAttr, int fallbackRes) {
        int color = styleable.getColor(attrIndex, Color.TRANSPARENT);
        if (Color.alpha(color) != 0) {
            return color;
        }
        TypedValue themeValue = new TypedValue();
        if (getContext().getTheme().resolveAttribute(themeAttr, themeValue, true)
                && themeValue.resourceId != 0) {
            return ContextCompat.getColor(getContext(), themeValue.resourceId);
        }
        return ContextCompat.getColor(getContext(), fallbackRes);
    }

    /**
     * Wires the bar to the list it drives. The geometry is read from the RecyclerView each time it
     * draws, so the only thing to follow is the redraw: a scroll listener invalidates it.
     */
    public void attachTo(@NonNull RecyclerView list) {
        this.recyclerView = list;
        list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                invalidate();
            }
        });
        requestLayout();
        invalidate();
    }

    /** Width of the touch target: the drawn bar plus the touch slop on each side. */
    public float getTouchTargetWidth() {
        return barWidth + 2 * touchSlop;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = (int) Math.ceil(getTouchTargetWidth() + 2 * edgeInset);
        setMeasuredDimension(
                resolveSize(width, widthMeasureSpec),
                resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (!isScrollable()) {
            return;
        }

        float barRight = getWidth() - edgeInset;
        float barLeft = barRight - barWidth;
        float centerX = (barLeft + barRight) / 2f;
        float radius = barWidth / 2f;
        float top = FastScrollGeometry.thumbTop(recyclerView, getHeight(), edgeInset, minThumbHeight);
        float bottom = top + FastScrollGeometry.thumbHeight(
                recyclerView, getHeight() - 2 * edgeInset, minThumbHeight);

        // The track keeps its alpha when idle, the thumb slightly less; the colours themselves
        // come from the XML attributes or the theme.
        trackPaint.setAlpha(dragging ? 255 : IDLE_TRACK_ALPHA);
        thumbPaint.setAlpha(dragging ? 255 : IDLE_THUMB_ALPHA);
        float trackHalf = radius / 2f;
        canvas.drawRoundRect(centerX - trackHalf, edgeInset, centerX + trackHalf,
                getHeight() - edgeInset, trackHalf, trackHalf, trackPaint);
        canvas.drawRoundRect(barLeft, top, barRight, bottom, radius, radius, thumbPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isScrollable()) {
            return false;
        }

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (!isInsideTouchTarget(event.getX())) {
                    return false;
                }
                dragging = true;
                getParent().requestDisallowInterceptTouchEvent(true);
                scrollToTouch(event.getY());
                return true;
            case MotionEvent.ACTION_MOVE:
                if (!dragging) {
                    return false;
                }
                scrollToTouch(event.getY());
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (!dragging) {
                    return false;
                }
                dragging = false;
                getParent().requestDisallowInterceptTouchEvent(false);
                invalidate();
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    /** Scrolls the list so the thumb centre follows the finger. */
    private void scrollToTouch(float y) {
        if (recyclerView == null) {
            return;
        }
        int index = FastScrollGeometry.indexForTouch(
                recyclerView, y, getHeight(), edgeInset, minThumbHeight);
        FastScrollGeometry.scrollToTouch(recyclerView, index);
        invalidate();
    }

    /** Whether the finger landed in the touch target, which is wider than the drawn bar. */
    private boolean isInsideTouchTarget(float x) {
        float barRight = getWidth() - edgeInset;
        float barLeft = barRight - barWidth;
        return x >= barLeft - touchSlop && x <= barRight + touchSlop;
    }

    /** The bar only makes sense when there is something to scroll. */
    private boolean isScrollable() {
        return recyclerView != null && FastScrollGeometry.isScrollable(recyclerView);
    }
}
