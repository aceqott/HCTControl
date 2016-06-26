package com.hctrom.romcontrol.prefs;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;

import com.hctrom.romcontrol.R;

/**
 * Created by Ivan on 02/06/2016.
 */
public class ThemeSwitch {

    public static void getIconsColor(Context act){
        Drawable drawable[] = new Drawable[]{act.getResources().getDrawable(R.drawable.ic_bar),
                act.getResources().getDrawable(R.drawable.ic_3minit2),
                act.getResources().getDrawable(R.drawable.ic_home2),
                act.getResources().getDrawable(R.drawable.ic_alarm2),
                act.getResources().getDrawable(R.drawable.ic_clock2),
                act.getResources().getDrawable(R.drawable.ic_gradient),
                act.getResources().getDrawable(R.drawable.ic_speed),
                act.getResources().getDrawable(R.drawable.hct_bloqueo),
                act.getResources().getDrawable(R.drawable.hct_home),
                act.getResources().getDrawable(R.drawable.ic_alarm_white_24dp),
                act.getResources().getDrawable(R.drawable.ic_bluetooth_white_24dp),
                act.getResources().getDrawable(R.drawable.ic_orden),
                act.getResources().getDrawable(R.drawable.ic_fondot),
                act.getResources().getDrawable(R.drawable.ic_cpu),
                act.getResources().getDrawable(R.drawable.ic_cleaner),
                act.getResources().getDrawable(R.drawable.ic_setting),
                act.getResources().getDrawable(R.drawable.ic_notif),
                act.getResources().getDrawable(R.drawable.ic_volume_high_white_24dp),
                act.getResources().getDrawable(R.drawable.ic_lcd),
                act.getResources().getDrawable(R.drawable.ic_emoji2),
                act.getResources().getDrawable(R.drawable.ic_samsung2),
                act.getResources().getDrawable(R.drawable.ic_settings2),
                act.getResources().getDrawable(R.drawable.ic_multiwindow),
                act.getResources().getDrawable(R.drawable.ic_smart),
                act.getResources().getDrawable(R.drawable.ic_toolbox),
                act.getResources().getDrawable(R.drawable.ic_download2),
                act.getResources().getDrawable(R.drawable.ic_panel),
                act.getResources().getDrawable(R.drawable.ic_ahorro),
                act.getResources().getDrawable(R.drawable.ic_upsm),
                act.getResources().getDrawable(R.drawable.ic_adaway2),
                act.getResources().getDrawable(R.drawable.ic_catlog),
                act.getResources().getDrawable(R.drawable.ic_cpuspy2),
                act.getResources().getDrawable(R.drawable.ic_ota),
                act.getResources().getDrawable(R.drawable.ic_aviso),
                act.getResources().getDrawable(R.drawable.ic_faster_gps)
        };

        int l = drawable.length;
        for (int i = 0; i < l; i++) {
            if (PreferenceManager.getDefaultSharedPreferences(act).getInt("theme_prefs", 0) == 1) {
                drawable[i].setColorFilter(new PorterDuffColorFilter(act.getResources().getColor(R.color.color_iconos_dark), PorterDuff.Mode.MULTIPLY));
            } else if (PreferenceManager.getDefaultSharedPreferences(act).getInt("theme_prefs", 0) == 2) {
                drawable[i].setColorFilter(new PorterDuffColorFilter(act.getResources().getColor(R.color.color_iconos_light), PorterDuff.Mode.MULTIPLY));
            } else if (PreferenceManager.getDefaultSharedPreferences(act).getInt("theme_prefs", 0) == 3) {
                drawable[i].setColorFilter(new PorterDuffColorFilter(act.getResources().getColor(R.color.color_iconos_samsung_light), PorterDuff.Mode.MULTIPLY));
            } else {
                drawable[i].setColorFilter(new PorterDuffColorFilter(act.getResources().getColor(R.color.color_iconos_hct), PorterDuff.Mode.MULTIPLY));
            }
        }
    }
}
