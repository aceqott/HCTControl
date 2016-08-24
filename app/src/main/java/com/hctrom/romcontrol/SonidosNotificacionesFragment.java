package com.hctrom.romcontrol;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.hctrom.romcontrol.alertas.DialogoAlertaNivelSonido;
import com.hctrom.romcontrol.alertas.DialogoAlertaReiniciar;
import com.hctrom.romcontrol.alertas.DialogoAlertaReiniciarSystemUI;
import com.hctrom.romcontrol.alertas.DialogoAlertaSonidoInicio;
import com.hctrom.romcontrol.prefs.Shell;
import com.hctrom.romcontrol.prefs.ThemeSwitch;
import com.hctrom.romcontrol.sh.Scripts;


public class SonidosNotificacionesFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    HandlePreferenceFragments hpf;
    SwitchPreference switchfreference_volume_warning_toggle;
    SwitchPreference switchfreference_bateria_baja;
    SwitchPreference switchfreference_teclas_volumen;
    SwitchPreference switchfreference_sonido_carga;
    SwitchPreference switchfreference_sonido_captura;
    PreferenceScreen preferencescreen_nivel_sonido;
    PreferenceScreen preferencescreen_sonido_inicio;
    private static FragmentActivity myContext;
    Scripts sh = new Scripts();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeSwitch.getIconsColor(getActivity());
        //addPreferencesFromResource(R.xml.sonidos_notificaciones_prefs);
        hpf = new HandlePreferenceFragments(getActivity(), this, "sonidos_notificaciones_prefs");
        switchfreference_volume_warning_toggle = (SwitchPreference) findPreference("volume_warning_toggle");
        switchfreference_bateria_baja = (SwitchPreference) findPreference("switch_bateria_baja");
        switchfreference_teclas_volumen = (SwitchPreference) findPreference("switch_teclas_volumen");
        switchfreference_sonido_carga = (SwitchPreference) findPreference("switch_sonido_carga");
        switchfreference_sonido_captura = (SwitchPreference) findPreference("switch_sonido_captura");
        preferencescreen_nivel_sonido = (PreferenceScreen) findPreference("nivel_sonido");
        preferencescreen_sonido_inicio = (PreferenceScreen) findPreference("sonido_inicio");


        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (prefs.getBoolean("switch_bateria_baja", true)) {
            switchfreference_bateria_baja.setChecked(true);
        } else {
            switchfreference_bateria_baja.setChecked(false);
        }

        if (prefs.getBoolean("switch_teclas_volumen", true)) {
            switchfreference_teclas_volumen.setChecked(true);
        } else {
            switchfreference_teclas_volumen.setChecked(false);
        }

        if (prefs.getBoolean("switch_sonido_carga", true)) {
            switchfreference_sonido_carga.setChecked(true);
        } else {
            switchfreference_sonido_carga.setChecked(false);
        }

        if (prefs.getBoolean("switch_sonido_captura", true)) {
            switchfreference_sonido_captura.setChecked(true);
        } else {
            switchfreference_sonido_captura.setChecked(true);
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        hpf.onResumeFragment();
        switchfreference_volume_warning_toggle.setOnPreferenceClickListener(this);
        switchfreference_bateria_baja.setOnPreferenceClickListener(this);
        switchfreference_teclas_volumen.setOnPreferenceClickListener(this);
        switchfreference_sonido_carga.setOnPreferenceClickListener(this);
        switchfreference_sonido_captura.setOnPreferenceClickListener(this);
        preferencescreen_nivel_sonido.setOnPreferenceClickListener(this);
        preferencescreen_sonido_inicio.setOnPreferenceClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        hpf.onPauseFragment();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if(preference == switchfreference_volume_warning_toggle) {
            myContext = (FragmentActivity) getActivity();
            final DialogoAlertaReiniciar dialogo = new DialogoAlertaReiniciar();
            dialogo.show(myContext.getSupportFragmentManager(), "tagAlerta");
        }

        if(preference == switchfreference_bateria_baja) {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            final SharedPreferences.Editor editor = prefs.edit();
            PreferenceManager preferenceManager = getPreferenceManager();
            if (preferenceManager.getSharedPreferences().getBoolean("switch_bateria_baja", true)) {
                // Your switch is on
                Toast.makeText(getActivity(), "Sonido ON", Toast.LENGTH_LONG).show();
                Shell.getRebootAction("" + sh.getBateriaBaja("ON"));
                //switchfreference_bateria_baja.setSummary("Activado");
                editor.putBoolean("switch_bateria_baja", true);
                editor.commit();
            } else {
                // Your switch is off
                Toast.makeText(getActivity(), "Sonido OFF", Toast.LENGTH_LONG).show();
                Shell.getRebootAction("" + sh.getBateriaBaja("OFF"));
                //switchfreference_bateria_baja.setSummary("Desactivado");
                editor.putBoolean("switch_bateria_baja", false);
                editor.commit();
            }

            myContext = (FragmentActivity) getActivity();
            final DialogoAlertaReiniciarSystemUI dialogo = new DialogoAlertaReiniciarSystemUI();
            dialogo.show(myContext.getSupportFragmentManager(), "tagAlerta");
        }

        if(preference == switchfreference_teclas_volumen) {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            final SharedPreferences.Editor editor = prefs.edit();
            PreferenceManager preferenceManager = getPreferenceManager();
            if (preferenceManager.getSharedPreferences().getBoolean("switch_teclas_volumen", true)) {
                // Your switch is on
                Toast.makeText(getActivity(), "Sonido ON", Toast.LENGTH_LONG).show();
                Shell.getRebootAction("" + sh.getSonidoTeclaVolumen("ON"));
                //switchfreference_teclas_volumen.setSummary("Activado");
                editor.putBoolean("switch_teclas_volumen", true);
                editor.commit();
            } else {
                // Your switch is off
                Toast.makeText(getActivity(), "Sonido OFF", Toast.LENGTH_LONG).show();
                Shell.getRebootAction("" + sh.getSonidoTeclaVolumen("OFF"));
                //switchfreference_teclas_volumen.setSummary("Desactivado");
                editor.putBoolean("switch_teclas_volumen", true);
                editor.commit();
            }

            myContext = (FragmentActivity) getActivity();
            final DialogoAlertaReiniciarSystemUI dialogo = new DialogoAlertaReiniciarSystemUI();
            dialogo.show(myContext.getSupportFragmentManager(), "tagAlerta");
        }

        if(preference == switchfreference_sonido_carga) {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            final SharedPreferences.Editor editor = prefs.edit();
            PreferenceManager preferenceManager = getPreferenceManager();
            if (preferenceManager.getSharedPreferences().getBoolean("switch_sonido_carga", true)) {
                // Your switch is on
                Toast.makeText(getActivity(), "Sonido ON", Toast.LENGTH_LONG).show();
                Shell.getRebootAction("" + sh.getSonidoCarga("ON"));
                //switchfreference_sonido_carga.setSummary("Activado");
                editor.putBoolean("switch_sonido_carga", true);
                editor.commit();
            } else {
                // Your switch is off
                Toast.makeText(getActivity(), "Sonido OFF", Toast.LENGTH_LONG).show();
                Shell.getRebootAction("" + sh.getSonidoCarga("OFF"));
                //switchfreference_sonido_carga.setSummary("Desactivado");
                editor.putBoolean("switch_sonido_carga", false);
                editor.commit();
            }
        }

        if(preference == switchfreference_sonido_captura) {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            final SharedPreferences.Editor editor = prefs.edit();
            PreferenceManager preferenceManager = getPreferenceManager();
            if (preferenceManager.getSharedPreferences().getBoolean("switch_sonido_captura", true)) {
                // Your switch is on
                Toast.makeText(getActivity(), "Sonido ON", Toast.LENGTH_LONG).show();
                Shell.getRebootAction("" + sh.getSonidoCapturaPantalla("ON"));
                //switchfreference_sonido_captura.setSummary("Activado");
                editor.putBoolean("switch_sonido_captura", true);
                editor.commit();
            } else {
                // Your switch is off
                Toast.makeText(getActivity(), "Sonido OFF", Toast.LENGTH_LONG).show();
                Shell.getRebootAction("" + sh.getSonidoCapturaPantalla("OFF"));
                //switchfreference_sonido_captura.setSummary("Desactivado");
                editor.putBoolean("switch_sonido_captura", false);
                editor.commit();
            }

            myContext = (FragmentActivity) getActivity();
            final DialogoAlertaReiniciarSystemUI dialogo = new DialogoAlertaReiniciarSystemUI();
            dialogo.show(myContext.getSupportFragmentManager(), "tagAlerta");
        }

        if (preference == preferencescreen_nivel_sonido){
            myContext = (FragmentActivity) getActivity();
            final DialogoAlertaNivelSonido dialogo = new DialogoAlertaNivelSonido();
            dialogo.show(myContext.getSupportFragmentManager(), "tagAlerta");
        }

        if (preference == preferencescreen_sonido_inicio){
            myContext = (FragmentActivity) getActivity();
            final DialogoAlertaSonidoInicio dialogo = new DialogoAlertaSonidoInicio();
            dialogo.show(myContext.getSupportFragmentManager(), "tagAlerta");
        }

        return false;
    }
}
