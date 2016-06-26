package com.hctrom.romcontrol.alertas;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.widget.Button;

import com.hctrom.romcontrol.R;
import com.hctrom.romcontrol.ThemeSelectorUtility;
import com.hctrom.romcontrol.build.prop.editor.BuildPropEditor;

/**
 * Created by Ivan on 13/12/2015.
 */
public class DialogoAlertaBuildEdit extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ThemeSelectorUtility theme = new ThemeSelectorUtility(getActivity());
        theme.onActivityCreateSetTheme(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("Si no sabe Editar o Configurar el Build.prop, por favor, NO modifique nada de este apartado o de lo contrario puede sufrir un BootLoop...")
                .setTitle("¡¡¡ATENCIÓN!!!")
                .setCancelable(false)
                .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        startActivity(new Intent(getActivity(), BuildPropEditor.class));
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setOnKeyListener(new Dialog.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                        // TODO Auto-generated method stub
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            // No hacer nada al pulsar el Botón Atrás
                        }
                        return true;
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
