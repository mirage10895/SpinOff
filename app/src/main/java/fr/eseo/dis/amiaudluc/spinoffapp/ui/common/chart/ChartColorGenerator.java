package fr.eseo.dis.amiaudluc.spinoffapp.ui.common.chart;

import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import java.util.Iterator;

import androidx.core.graphics.ColorUtils;

public class ChartColorGenerator implements Iterator<Integer> {

    private final int primaryColor;
    private final int secondaryColor;
    private final int totalSegments;
    private final Interpolator interpolator;

    private int currentStep = 0;

    /**
     * @param primaryColor   The starting color
     * @param secondaryColor The ending color
     * @param totalSegments  Total number of chart fragments
     * @param interpolator   Controls the "clustering". Pass null for a standard even gradient.
     */
    public ChartColorGenerator(int primaryColor, int secondaryColor, int totalSegments, Interpolator interpolator) {
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.totalSegments = totalSegments;
        this.interpolator = interpolator != null ? interpolator : new LinearInterpolator();
    }

    @Override
    public boolean hasNext() {
        return currentStep < totalSegments;
    }

    @Override
    public Integer next() {
        if (!hasNext()) {
            // Failsafe in case you call next() too many times
            return secondaryColor;
        }

        // 1. Calculate linear progress (0.0 to 1.0)
        float linearRatio = totalSegments > 1 ? (float) currentStep / (totalSegments - 1) : 0f;

        // 2. Apply the interpolator to warp the ratio (this creates the clustering effect)
        float blendedRatio = interpolator.getInterpolation(linearRatio);

        // 3. Blend the colors based on the warped ratio
        int generatedColor = ColorUtils.blendARGB(primaryColor, secondaryColor, blendedRatio);

        // 4. Advance the state for the next call
        currentStep++;

        return generatedColor;
    }
}
