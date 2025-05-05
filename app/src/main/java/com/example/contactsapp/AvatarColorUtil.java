package com.example.contactsapp;

import android.content.Context;
import androidx.core.content.ContextCompat;

public class AvatarColorUtil {

    private static final int[] COLOR_IDS = {
            R.color.avatar_color_0,
            R.color.avatar_color_1,
            R.color.avatar_color_2,
            R.color.avatar_color_3,
            R.color.avatar_color_4,
            R.color.avatar_color_5,
            R.color.avatar_color_6,
            R.color.avatar_color_7,
            R.color.avatar_color_8,
            R.color.avatar_color_9,
    };

    public static int getColor(Context context, String seed) {
        if (seed == null || seed.isEmpty()) return ContextCompat.getColor(context, COLOR_IDS[0]);
        int index = Math.abs(seed.toLowerCase().charAt(0) % COLOR_IDS.length);
        return ContextCompat.getColor(context, COLOR_IDS[index]);
    }
}
