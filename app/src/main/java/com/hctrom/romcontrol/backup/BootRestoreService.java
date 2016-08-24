package com.hctrom.romcontrol.backup;

import android.app.Activity;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;
import android.widget.Button;

import com.hctrom.romcontrol.R;
import com.hctrom.romcontrol.ThemeSelectorUtility;

public class BootRestoreService extends Service {

    public BootRestoreService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        showConfirmDialog();
    }

    private void showConfirmDialog() {
        Activity act = (Activity) getApplicationContext();
        ThemeSelectorUtility theme = new ThemeSelectorUtility(getApplicationContext());
        theme.onActivityCreateSetTheme(act);
        AlertDialog dialogConfirm = new AlertDialog.Builder(this)
                .setMessage(R.string.confirm_restore)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RestorePreferences rp = new RestorePreferences(BootRestoreService.this);
                                rp.restoreSettings();
                                BootRestoreService.this.stopSelf();
                            }
                        }

                )
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        BootRestoreService.this.stopSelf();


                    }
                })
                .create();
        dialogConfirm.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialogConfirm.show();

        Button positive_button = dialogConfirm.getButton(DialogInterface.BUTTON_POSITIVE);
        Button negative_button = dialogConfirm.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("theme_prefs", 0) == 3) {
            dialogConfirm.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg_samsung_light);
            positive_button.setTextColor(getResources().getColor(R.color.color_iconos_samsung_light));
            negative_button.setTextColor(getResources().getColor(R.color.color_iconos_samsung_light));
        }else if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("theme_prefs", 0) == 4){
            dialogConfirm.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg_hct);
            positive_button.setTextColor(getResources().getColor(R.color.myAccentColorMaterialDark));
            negative_button.setTextColor(getResources().getColor(R.color.myAccentColorMaterialDark));
        }else if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("theme_prefs", 0) == 0){
            dialogConfirm.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg_hct);
            positive_button.setTextColor(getResources().getColor(R.color.myAccentColorHCT));
            negative_button.setTextColor(getResources().getColor(R.color.myAccentColorHCT));
        }else {
            dialogConfirm.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg_dark_light);
            positive_button.setTextColor(getResources().getColor(R.color.myAccentColor));
            negative_button.setTextColor(getResources().getColor(R.color.myAccentColor));
        }
    }
}
