package fr.eseo.dis.amiaudluc.spinoffapp.ui.common.chart;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import androidx.core.widget.TextViewCompat;
import fr.eseo.dis.amiaudluc.R;

public class MultiSegmentProgressViewBuilder {
    private int primaryColor;
    private int secondaryColor;
    private boolean useDefaultColors = false;
    private Interpolator interpolator;
    private List<Map.Entry<String, Integer>> data;
    private MultiSegmentProgressView chartView;

    protected MultiSegmentProgressViewBuilder() {
    }

    public MultiSegmentProgressViewBuilder withColors() {
        this.useDefaultColors = true;
        return this;
    }

    public MultiSegmentProgressViewBuilder withColors(int primaryColor, int secondaryColor) {
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.useDefaultColors = false;
        return this;
    }

    public MultiSegmentProgressViewBuilder withInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
        return this;
    }

    public MultiSegmentProgressViewBuilder withStrokeWidths(float background, float segment) {
        this.chartView.setStrokeWidths(background, segment);
        return this;
    }

    public MultiSegmentProgressViewBuilder withData(List<Map.Entry<String, Integer>> data) {
        this.data = data;
        return this;
    }

    public MultiSegmentProgressViewBuilder into(MultiSegmentProgressView view) {
        this.chartView = view;
        if (data == null || view == null) return this;

        Context context = view.getContext();
        int pColor = useDefaultColors ? context.getColor(R.color.colorPrimary) : primaryColor;
        int sColor = useDefaultColors ? context.getColor(R.color.colorSecondary) : secondaryColor;

        int total = data.stream().mapToInt(Map.Entry::getValue).sum();
        if (total == 0) {
            view.setSegments(null);
            return this;
        }

        ChartColorGenerator colorGenerator = new ChartColorGenerator(
                pColor,
                sColor,
                data.size(),
                interpolator
        );

        List<MultiSegmentProgressView.Segment> segments = data.stream()
                .map(entry -> new MultiSegmentProgressView.Segment(
                        entry.getKey(),
                        colorGenerator.next(),
                        (float) entry.getValue() / total
                ))
                .collect(Collectors.toList());

        view.setSegments(segments);
        return this;
    }

    public void withLegendInto(ViewGroup container) {
        if (data == null || container == null || chartView == null) return;

        Context context = container.getContext();
        int total = data.stream().mapToInt(Map.Entry::getValue).sum();

        for (Map.Entry<String, Integer> entry : data) {
            TextView textView = new TextView(context);
            TextViewCompat.setTextAppearance(textView, R.style.BentoLabel);

            chartView.getSegment(entry.getKey()).ifPresent(segment -> {
                GradientDrawable dot = new GradientDrawable();
                dot.setShape(GradientDrawable.OVAL);
                int dotSize = (int) (8 * context.getResources().getDisplayMetrics().density);
                dot.setSize(dotSize, dotSize);
                dot.setColor(segment.color);
                textView.setCompoundDrawablesRelativeWithIntrinsicBounds(dot, null, null, null);
                textView.setCompoundDrawablePadding((int) (8 * context.getResources().getDisplayMetrics().density));
            });

            int percent = total > 0 ? (entry.getValue() * 100 / total) : 0;
            textView.setText(String.format(Locale.getDefault(), "%s: %d (%d%%)", entry.getKey(), entry.getValue(), percent));
            container.addView(textView);
        }
    }
}
