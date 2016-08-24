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
import com.hctrom.romcontrol.prefs.ThemeSwitch;
import com.hctrom.romcontrol.videotutorial.VideoTutorial;


public class AcercaDeFragment extends PreferenceFragment {
    HandlePreferenceFragments hpf;
    private PreferenceScreen preferenceScreen;
    private PreferenceScreen logo;
    private static FragmentActivity myContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeSwitch.getIconsColor(getActivity());
        //addPreferencesFromResource(R.xml.acerca_de_prefs);
        hpf = new HandlePreferenceFragments(getActivity(), this, "acerca_de_prefs");
        logo = (PreferenceScreen) findPreference("logo_hct");
        preferenceScreen = (PreferenceScreen) findPreference("videotutorial_hct");

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

    @Override
    public void onResume() {
        super.onResume();
        hpf.onResumeFragment();

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

    @Override
    public void onPause() {
        super.onPause();
        hpf.onPauseFragment();
    }

}
