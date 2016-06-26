package com.hctrom.romcontrol.alertas;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
public class DialogoAlertaSonidoInicio extends DialogFragment implements View.OnClickListener {
    private RadioButton stock;
    private RadioButton hct;
    private RadioButton off;
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
        final View v = inflater.inflate(R.layout.sonido_inicio, container, false);
        final SharedPreferences prefs = getActivity().getSharedPreferences("DatosSonidos", Context.MODE_PRIVATE);
        stock = (RadioButton) v.findViewById(R.id.sonido_inicio_stock);
        hct = (RadioButton) v.findViewById(R.id.sonido_inicio_hct);
        off = (RadioButton) v.findViewById(R.id.sonido_inicio_off);
        ok = (Button) v.findViewById(R.id.ok);
        cancelar = (Button) v.findViewById(R.id.cancelar);

        getDialog().setCanceledOnTouchOutside(false);
        String str = "" + prefs.getString("sonido_inicio", "1");
        if (str.contentEquals("1")) {
            stock.setChecked(true);
        } else {
            stock.setChecked(false);
        }
        if (str.contentEquals("2")) {
            hct.setChecked(true);
        } else {
            hct.setChecked(false);
        }
        if (str.contentEquals("3")) {
            off.setChecked(true);
        } else {
            off.setChecked(false);
        }

        stock.setOnClickListener(this);
        hct.setOnClickListener(this);
        off.setOnClickListener(this);
        ok.setOnClickListener(this);
        cancelar.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        final SharedPreferences prefs = getActivity().getSharedPreferences("DatosSonidos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (v == ok){
            if(stock.isChecked()){
                Shell.getRebootAction("" + sh.getSonidoInicioStock());
                editor.putString("sonido_inicio", "1");
                editor.commit();
                Toast.makeText(getActivity(), "Sonido Stock seleccionado", Toast.LENGTH_LONG).show();
            }
            if(hct.isChecked()){
                Shell.getRebootAction("" + sh.getSonidoInicioHCT());
                editor.putString("sonido_inicio", "2");
                editor.commit();
                Toast.makeText(getActivity(), "Sonido HCT seleccionado", Toast.LENGTH_LONG).show();
            }
            if(off.isChecked()){
                Shell.getRebootAction("" + sh.getSonidoInicioOff());
                editor.putString("sonido_inicio", "3");
                editor.commit();
                Toast.makeText(getActivity(), "Sonido inicio desactivado", Toast.LENGTH_LONG).show();
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
