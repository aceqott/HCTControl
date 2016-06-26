package com.hctrom.romcontrol.alertas;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.widget.Button;

import com.hctrom.romcontrol.R;
import com.hctrom.romcontrol.ThemeSelectorUtility;
import com.hctrom.romcontrol.prefs.Shell;

/**
 * Created by Ivan on 13/12/2015.
 */
public class DialogoAlertaReiniciarSystemUI extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ThemeSelectorUtility theme = new ThemeSelectorUtility(getActivity());
        theme.onActivityCreateSetTheme(getActivity());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Es necesario reiniciar SystemUI para aplicar los cambios.\n\nDesea reiniciar SystemUI ahora?")
                .setTitle("REINICIAR SYSTEM UI")
                .setOnKeyListener(new Dialog.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                        // TODO Auto-generated method stub
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            // No hacer nada al pulsar el Botón Atrás
                        }
                        return true;
                    }
                })
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        Shell.getRebootAction("pkill com.android.systemui");
                    }
                })
                .setNegativeButton("AHORA NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button positive_button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button negative_button = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("theme_prefs", 0) == 3) {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg_samsung_light);
            positive_button.setTextColor(getResources().getColor(R.color.color_iconos_samsung_light));
            negative_button.setTextColor(getResources().getColor(R.color.color_iconos_samsung_light));
        }else if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("theme_prefs", 0) == 0){
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg_hct);
            positive_button.setTextColor(getResources().getColor(R.color.myAccentColorHCT));
            negative_button.setTextColor(getResources().getColor(R.color.myAccentColorHCT));
        }else {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg_dark_light);
            positive_button.setTextColor(getResources().getColor(R.color.myAccentColor));
            negative_button.setTextColor(getResources().getColor(R.color.myAccentColor));
        }

        return dialog;
    }
}
