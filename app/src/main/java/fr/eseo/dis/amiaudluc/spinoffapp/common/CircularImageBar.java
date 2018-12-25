package fr.eseo.dis.amiaudluc.spinoffapp.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by lucasamiaud on 01/03/2018.
 */

public class CircularImageBar {

    public static Bitmap BuildNote(double size){
        Bitmap b = Bitmap.createBitmap(300, 300,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        Paint paint = new Paint();

        paint.setColor(Color.parseColor("#FFFFFF"));
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(150, 150, 140, paint);
        paint.setColor(Color.parseColor("#ffe240"));
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.FILL);
        final RectF oval = new RectF();
        paint.setStyle(Paint.Style.STROKE);
        oval.set(10,10,290,290);
        canvas.drawArc(oval, 270, (float) (((size)*360)/10), false, paint);
        paint.setStrokeWidth(5);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.parseColor("#FFFFFF"));
        paint.setTextSize(140);
        canvas.drawText(""+size, 150, 150+(paint.getTextSize()/3), paint);

        return b;
    }

    public static Bitmap BuildSeasons(int size){
        Bitmap b = Bitmap.createBitmap(300, 300,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        Paint paint = new Paint();

        paint.setColor(Color.parseColor("#c4c4c4"));
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.parseColor("#FFFFFF"));
        paint.setTextSize(130);
        canvas.drawText(""+size, 150, 150+(paint.getTextSize()/3), paint);

        return b;
    }

    public static Bitmap BuildNumber(int size,int color,Context ctx){
        Bitmap b = Bitmap.createBitmap(300, 300,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        Paint paint = new Paint();

        paint.setColor(ctx.getColor(color));
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(ctx.getColor(color));
        paint.setTextSize(130);
        canvas.drawText(""+size, 150, 150+(paint.getTextSize()/3), paint);

        return b;
    }

    public static Bitmap BuildString(String size,int color,Context ctx){
        Bitmap b = Bitmap.createBitmap(500, 500,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        Paint paint = new Paint();

        paint.setColor(ctx.getColor(color));
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(ctx.getColor(color));
        paint.setTextSize(130);
        canvas.drawText(""+size, 150, 150+(paint.getTextSize()/3), paint);

        return b;
    }

}
