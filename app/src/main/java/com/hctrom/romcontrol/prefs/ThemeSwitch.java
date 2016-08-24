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
        Drawable drawable[] = new Drawable[]{act.getResources().getDrawable(R.drawable.ic_batteryreborn),
                act.getResources().getDrawable(R.drawable.ic_wifianalyzer),
                act.getResources().getDrawable(R.drawable.ic_speedtest),
                act.getResources().getDrawable(R.drawable.ic_settingsalt),
                act.getResources().getDrawable(R.drawable.ic_homeboton),
                act.getResources().getDrawable(R.drawable.ic_potatogradient),
                act.getResources().getDrawable(R.drawable.ic_dynamicstatus),
                act.getResources().getDrawable(R.drawable.hct_bloqueo),
                act.getResources().getDrawable(R.drawable.hct_bloqueo_centro),
                act.getResources().getDrawable(R.drawable.hct_home),
                act.getResources().getDrawable(R.drawable.ic_volume_up_white_48dp),
                act.getResources().getDrawable(R.drawable.ic_recientes),
                act.getResources().getDrawable(R.drawable.ic_powermenu),
                act.getResources().getDrawable(R.drawable.ic_rebootrecovery),
                act.getResources().getDrawable(R.drawable.ic_screenrecord),
                act.getResources().getDrawable(R.drawable.ic_screenshot),
                act.getResources().getDrawable(R.drawable.ic_header),
                act.getResources().getDrawable(R.drawable.ic_toggle),
                act.getResources().getDrawable(R.drawable.ic_panelnoti),
                act.getResources().getDrawable(R.drawable.ic_info),
                act.getResources().getDrawable(R.drawable.ic_carrier1),
                act.getResources().getDrawable(R.drawable.ic_weather),
                act.getResources().getDrawable(R.drawable.ic_clockdate),
                act.getResources().getDrawable(R.drawable.ic_simtoolkit),
                act.getResources().getDrawable(R.drawable.ic_ahorro1),
                act.getResources().getDrawable(R.drawable.ic_sms_failed_white_48dp),
                act.getResources().getDrawable(R.drawable.ic_usb_white_48dp),
                act.getResources().getDrawable(R.drawable.ic_warning_white_48dp),
                act.getResources().getDrawable(R.drawable.ic_call_history),
                act.getResources().getDrawable(R.drawable.ic_contacts),
                act.getResources().getDrawable(R.drawable.ic_paypal),
                act.getResources().getDrawable(R.drawable.ic_videoder),
                act.getResources().getDrawable(R.drawable.ic_htc_personalize),
                act.getResources().getDrawable(R.drawable.ic_alarm_white_48dp),
                act.getResources().getDrawable(R.drawable.ic_bluetooth_white_48dp),
                act.getResources().getDrawable(R.drawable.ic_more_white_48dp),
                act.getResources().getDrawable(R.drawable.ic_wifi_white_48dp),
                act.getResources().getDrawable(R.drawable.ic_signal_white_48dp),
                act.getResources().getDrawable(R.drawable.ic_4g),
                act.getResources().getDrawable(R.drawable.ic_battery_80_white_48dp),
                act.getResources().getDrawable(R.drawable.ic_battery_white_48dp_percentage),
                act.getResources().getDrawable(R.drawable.ic_percentage),
                act.getResources().getDrawable(R.drawable.ic_qs_alarm_on),
                act.getResources().getDrawable(R.drawable.ic_3minitbattery),
                act.getResources().getDrawable(R.drawable.ic_launcherpsm),
                act.getResources().getDrawable(R.drawable.ic_ota),
                act.getResources().getDrawable(R.drawable.ic_hexconverter),
                act.getResources().getDrawable(R.drawable.ic_dpichanger),
                act.getResources().getDrawable(R.drawable.ic_emoji),
                act.getResources().getDrawable(R.drawable.ic_toolbox),
                act.getResources().getDrawable(R.drawable.ic_adaway),
                act.getResources().getDrawable(R.drawable.ic_diagnostico),
                act.getResources().getDrawable(R.drawable.ic_fastergps),
                act.getResources().getDrawable(R.drawable.ic_catlog),
                act.getResources().getDrawable(R.drawable.ic_buildpropeditor),
                act.getResources().getDrawable(R.drawable.ic_6thgear),
                act.getResources().getDrawable(R.drawable.ic_wiziconizer),
                act.getResources().getDrawable(R.drawable.ic_omniswitch),
                act.getResources().getDrawable(R.drawable.ic_clock2),
                act.getResources().getDrawable(R.drawable.ic_downloadbooster),
                act.getResources().getDrawable(R.drawable.ic_airplane),
                act.getResources().getDrawable(R.drawable.ic_privatemode)
        };

        int l = drawable.length;
        for (int i = 0; i < l; i++) {
            if (PreferenceManager.getDefaultSharedPreferences(act).getInt("theme_prefs", 0) == 1) {
                drawable[i].setColorFilter(new PorterDuffColorFilter(act.getResources().getColor(R.color.color_iconos_hct), PorterDuff.Mode.MULTIPLY));
            } else if (PreferenceManager.getDefaultSharedPreferences(act).getInt("theme_prefs", 0) == 2) {
                drawable[i].setColorFilter(new PorterDuffColorFilter(act.getResources().getColor(R.color.color_iconos_dark), PorterDuff.Mode.MULTIPLY));
            } else if (PreferenceManager.getDefaultSharedPreferences(act).getInt("theme_prefs", 0) == 3) {
                drawable[i].setColorFilter(new PorterDuffColorFilter(act.getResources().getColor(R.color.color_iconos_light), PorterDuff.Mode.MULTIPLY));
            } else {
                drawable[i].setColorFilter(new PorterDuffColorFilter(act.getResources().getColor(R.color.color_iconos_samsung_light), PorterDuff.Mode.MULTIPLY));
            }
        }
    }
}
