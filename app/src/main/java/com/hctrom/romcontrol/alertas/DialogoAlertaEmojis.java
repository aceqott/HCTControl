package com.hctrom.romcontrol.alertas;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.hctrom.romcontrol.R;
import com.hctrom.romcontrol.ThemeSelectorUtility;
import com.hctrom.romcontrol.prefs.Shell;
import com.hctrom.romcontrol.sh.Scripts;

/**
 * Created by Ivan on 13/12/2015.
 */
public class DialogoAlertaEmojis extends DialogFragment implements View.OnClickListener {
    private RadioButton stocksamsung;
    private RadioButton google;
    private RadioButton ios;
    private Button cancelar;
    private Button ok;
    Scripts sh = new Scripts();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed() {
                getDialog().dismiss();
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ThemeSelectorUtility theme = new ThemeSelectorUtility(getActivity());
        theme.onActivityCreateSetTheme(getActivity());
        final View v = inflater.inflate(R.layout.emojis, container, false);
        final SharedPreferences prefs = getActivity().getSharedPreferences("DatosSonidos", Context.MODE_PRIVATE);
        stocksamsung = (RadioButton) v.findViewById(R.id.emojis_stock);
        google = (RadioButton) v.findViewById(R.id.emojis_google);
        ios = (RadioButton) v.findViewById(R.id.emojis_ios);
        cancelar = (Button) v.findViewById(R.id.cancelar);
        ok = (Button) v.findViewById(R.id.ok);

        getDialog().setCanceledOnTouchOutside(false);
        String str = "" + prefs.getString("emojis", "1");
        if (str.contentEquals("1")) {
            stocksamsung.setChecked(true);
        } else {
            stocksamsung.setChecked(false);
        }
        if (str.contentEquals("2")) {
            google.setChecked(true);
        } else {
            google.setChecked(false);
        }
        if (str.contentEquals("3")) {
            ios.setChecked(true);
        } else {
            ios.setChecked(false);
        }

        stocksamsung.setOnClickListener(this);
        google.setOnClickListener(this);
        ios.setOnClickListener(this);
        cancelar.setOnClickListener(this);
        ok.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        final SharedPreferences prefs = getActivity().getSharedPreferences("DatosSonidos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (v == ok){
            if(stocksamsung.isChecked()){
                Shell.getRebootAction("" + sh.getEmojisStock());
                editor.putString("emojis", "1");
                editor.commit();
                Toast.makeText(getActivity(), "Emojis Stock seleccionados", Toast.LENGTH_LONG).show();
            }
            if(google.isChecked()){
                Shell.getRebootAction("" + sh.getEmojisGoogle());
                editor.putString("emojis", "2");
                editor.commit();
                Toast.makeText(getActivity(), "Emojis Google seleccionados", Toast.LENGTH_LONG).show();
            }
            if(ios.isChecked()){
                Shell.getRebootAction("" + sh.getEmojisiOS());
                editor.putString("emojis", "3");
                editor.commit();
                Toast.makeText(getActivity(), "Emojis iOS seleccionados", Toast.LENGTH_LONG).show();
            }
            getDialog().dismiss();
            final DialogoAlertaReiniciar dialogo = new DialogoAlertaReiniciar();
            dialogo.show(getActivity().getSupportFragmentManager(), "tagAlerta");
        }
        if (v == cancelar){
            getDialog().dismiss();
        }
    }
}
