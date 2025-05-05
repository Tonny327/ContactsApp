package com.example.contactsapp;

import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.content.Context;

public class TextDrawableUtil {
    public static Drawable createAvatar(Context context, String name, int sizePx) {
        String letter = name != null && !name.isEmpty()
                ? name.substring(0, 1).toUpperCase()
                : "?";

        int bgColor = AvatarColorUtil.getColor(context, name);
        int textColor = Color.WHITE;

        Bitmap bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        // Background
        paint.setColor(bgColor);
        paint.setAntiAlias(true);
        canvas.drawCircle(sizePx / 2f, sizePx / 2f, sizePx / 2f, paint);

        // Text
        paint.setColor(textColor);
        paint.setTextSize(sizePx * 0.5f);
        paint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float x = sizePx / 2f;
        float y = sizePx / 2f - (fontMetrics.ascent + fontMetrics.descent) / 2f;
        canvas.drawText(letter, x, y, paint);

        return new BitmapDrawable(context.getResources(), bitmap);
    }
}

