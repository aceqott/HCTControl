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
public class DialogoAlertaNivelSonido extends DialogFragment implements View.OnClickListener {
    private RadioButton stock;
    private RadioButton medio;
    private RadioButton alto;
    private RadioButton extremo;
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
        final View v = inflater.inflate(R.layout.sonido_nivel_sistema, container, false);
        SharedPreferences prefs = getActivity().getSharedPreferences("DatosSonidos", Context.MODE_PRIVATE);
        stock = (RadioButton) v.findViewById(R.id.stock);
        medio = (RadioButton) v.findViewById(R.id.medio);
        alto = (RadioButton) v.findViewById(R.id.alto);
        extremo = (RadioButton) v.findViewById(R.id.extremo);
        cancelar = (Button) v.findViewById(R.id.cancelar);
        ok = (Button) v.findViewById(R.id.ok);

        getDialog().setCanceledOnTouchOutside(false);
        String str = "" + prefs.getString("sonido_nivel_sistema", "1");
        if (str.contentEquals("1")) {
            stock.setChecked(true);
        } else {
            stock.setChecked(false);
        }
        if (str.contentEquals("2")) {
            medio.setChecked(true);
        } else {
            medio.setChecked(false);
        }
        if (str.contentEquals("3")) {
            alto.setChecked(true);
        } else {
            alto.setChecked(false);
        }
        if (str.contentEquals("4")) {
            extremo.setChecked(true);
        } else {
            extremo.setChecked(false);
        }

        stock.setOnClickListener(this);
        medio.setOnClickListener(this);
        alto.setOnClickListener(this);
        extremo.setOnClickListener(this);
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
                Shell.getRebootAction("" + sh.getNivelSonidoStock());
                editor.putString("sonido_nivel_sistema", "1");
                editor.commit();
                Toast.makeText(getActivity(), "Sonido Stock seleccionado", Toast.LENGTH_LONG).show();
            }
            if(medio.isChecked()){
                Shell.getRebootAction("" + sh.getNivelSonidoMedio());
                editor.putString("sonido_nivel_sistema", "2");
                editor.commit();
                Toast.makeText(getActivity(), "Sonido Medio seleccionado", Toast.LENGTH_LONG).show();
            }
            if(alto.isChecked()){
                Shell.getRebootAction("" + sh.getNivelSonidoAlto());
                editor.putString("sonido_nivel_sistema", "3");
                editor.commit();
                Toast.makeText(getActivity(), "Sonido Alto seleccionado", Toast.LENGTH_LONG).show();
            }
            if(extremo.isChecked()){
                Shell.getRebootAction("" + sh.getNivelSonidoExtremo());
                editor.putString("sonido_nivel_sistema", "4");
                editor.commit();
                Toast.makeText(getActivity(), "Sonido Extremo seleccionado", Toast.LENGTH_LONG).show();
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
