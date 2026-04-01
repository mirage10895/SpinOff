package fr.eseo.dis.amiaudluc.spinoffapp.ui.common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.text.DecimalFormat;

/**
 * Created by lucasamiaud on 01/03/2018.
 */

public class CircularImageBar {

    public static Bitmap buildNote(double size) {
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        Bitmap b = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        Paint paint = new Paint();

        paint.setColor(Color.parseColor("#FFFFFF"));
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(150, 150, 140, paint);
        paint.setColor(Color.parseColor("#4F9DA6"));
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.FILL);
        final RectF oval = new RectF();
        paint.setStyle(Paint.Style.STROKE);
        oval.set(10, 10, 290, 290);
        canvas.drawArc(oval, 270, (float) (((size) * 360) / 10), false, paint);
        paint.setStrokeWidth(5);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.parseColor("#FFFFFF"));
        paint.setTextSize(140);
        canvas.drawText(decimalFormat.format(size), 150, 150 + (paint.getTextSize() / 3), paint);

        return b;
    }

    public static Bitmap buildNumber(int size, int color) {
        Bitmap b = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        Paint paint = new Paint();

        paint.setColor(color);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(130);
        canvas.drawText(String.valueOf(size), 150, 150 + (paint.getTextSize() / 3), paint);

        return b;
    }

}
