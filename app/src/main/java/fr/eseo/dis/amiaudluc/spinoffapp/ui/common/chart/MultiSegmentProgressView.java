package fr.eseo.dis.amiaudluc.spinoffapp.ui.common.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import androidx.annotation.Nullable;

public class MultiSegmentProgressView extends View {

    public static MultiSegmentProgressViewBuilder builder() {
        return new MultiSegmentProgressViewBuilder();
    }

    public static class Segment {
        public String id;
        public int color;
        public float percentage; // 0.0 to 1.0

        public Segment(
                String id,
                int color,
                float percentage
        ) {
            this.id = id;
            this.color = color;
            this.percentage = percentage;
        }
    }

    private Paint backgroundPaint;
    private Paint segmentPaint;
    private final RectF rectF = new RectF();
    private List<Segment> segments = new ArrayList<>();

    private float strokeWidthBackground = 16f;
    private float strokeWidthSegment = 20f;
    private int backgroundColor = Color.parseColor("#33FFFFFF"); // Default semi-transparent white

    public MultiSegmentProgressView(Context context) {
        super(context);
        init();
    }

    public MultiSegmentProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(strokeWidthBackground);
        backgroundPaint.setColor(backgroundColor);

        segmentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        segmentPaint.setStyle(Paint.Style.STROKE);
        segmentPaint.setStrokeWidth(strokeWidthSegment);
        segmentPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setSegments(List<Segment> segments) {
        this.segments = segments != null ? segments : new ArrayList<>();
        invalidate();
    }

    public void setStrokeWidths(float background, float segment) {
        this.strokeWidthBackground = background;
        this.strokeWidthSegment = segment;
        backgroundPaint.setStrokeWidth(strokeWidthBackground);
        segmentPaint.setStrokeWidth(strokeWidthSegment);
        invalidate();
    }

    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
        backgroundPaint.setColor(backgroundColor);
        invalidate();
    }

    public Optional<Segment> getSegment(String id) {
        return segments.stream()
                .filter(s -> s.id.equals(id))
                .findFirst();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();
        float radius = (Math.min(width, height) - strokeWidthSegment) / 2f;
        float centerX = width / 2f;
        float centerY = height / 2f;

        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        // Draw background circle
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint);

        // SVG rotation: -90 degrees is the top
        float currentStartAngle = -90f;

        // Draw each segment sequentially
        for (Segment segment : segments) {
            segmentPaint.setColor(segment.color);
            float sweepAngle = segment.percentage * 360f;
            canvas.drawArc(rectF, currentStartAngle, sweepAngle, false, segmentPaint);
            
            // Advance the start angle for the next segment
            currentStartAngle += sweepAngle;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        int size = Math.min(width, height);
        setMeasuredDimension(size, size);
    }
}
