package com.hctrom.romcontrol;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.hctrom.romcontrol.licenseadapter.LicenseDialogoAlerta;
import com.hctrom.romcontrol.videotutorial.VideoTutorial;


public class AcercaDeFragment extends PreferenceFragment {
    //HandlePreferenceFragments hpf;
    private static FragmentActivity myContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.acerca_de_prefs);
        //hpf = new HandlePreferenceFragments(getActivity(), this, "acerca_de_prefs");

        PreferenceScreen preferenceScreen = (PreferenceScreen) findPreference("videotutorial_hct");
        preferenceScreen.setIcon(R.drawable.ic_videotutorial);
        PreferenceScreen preferenceScreen1 = (PreferenceScreen) findPreference("paypal_me");
        preferenceScreen1.setIcon(R.drawable.ic_paypal);
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("theme_prefs", 0) == 1) {
            preferenceScreen.getIcon().setTint(getResources().getColor(R.color.color_iconos_dark));
            preferenceScreen1.getIcon().setTint(getResources().getColor(R.color.color_iconos_dark));
        }else if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("theme_prefs", 0) == 2){
            preferenceScreen.getIcon().setTint(getResources().getColor(R.color.color_iconos_light));
            preferenceScreen1.getIcon().setTint(getResources().getColor(R.color.color_iconos_light));
        }else if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("theme_prefs", 0) == 3){
            preferenceScreen.getIcon().setTint(getResources().getColor(R.color.color_iconos_samsung_light));
            preferenceScreen1.getIcon().setTint(getResources().getColor(R.color.color_iconos_samsung_light));
        }else{
            preferenceScreen.getIcon().setTint(getResources().getColor(R.color.color_iconos_hct));
            preferenceScreen1.getIcon().setTint(getResources().getColor(R.color.color_iconos_hct));
        }
        PreferenceScreen logo = (PreferenceScreen) findPreference("logo_hct");
        logo.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                myContext = (FragmentActivity) getActivity();
                final LicenseDialogoAlerta dialogo = new LicenseDialogoAlerta();
                dialogo.show(myContext.getSupportFragmentManager(), "tagAlerta");
                return false;
            }
        });

        preferenceScreen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (exiteConexionInternet()) {
                    Toast.makeText(getActivity(), "Abriendo ...", Toast.LENGTH_LONG).show();
                }
                startActivity(new Intent(getActivity(), VideoTutorial.class));

                return false;
            }
        });

    }

    /**
     * Comprueba si hay conexión a internet.
     * @return boolean
     */
    private boolean exiteConexionInternet() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
/*
    @Override
    public void onResume() {
        super.onResume();
        hpf.onResumeFragment();
    }

    @Override
    public void onPause() {
        super.onPause();
        hpf.onPauseFragment();
    }
*/

}
