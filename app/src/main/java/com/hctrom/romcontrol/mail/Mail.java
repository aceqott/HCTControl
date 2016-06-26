package com.hctrom.romcontrol.mail;

/**
 * Created by Ivan on 30/03/2016.
 */

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.hctrom.romcontrol.R;
import com.hctrom.romcontrol.ThemeSelectorUtility;


public class Mail extends AppCompatActivity {
    private Toolbar toolbar;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeSelectorUtility theme = new ThemeSelectorUtility(this);
        theme.onActivityCreateSetTheme(this);
        setContentView(R.layout.mail);
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("theme_prefs", 0) == 3) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.myPrimaryDarkColorSamsungLight));
        }else if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("theme_prefs", 0) == 0){
            getWindow().setStatusBarColor(getResources().getColor(R.color.myPrimaryDarkColorHCT));
        }else{
            getWindow().setStatusBarColor(getResources().getColor(R.color.myPrimaryDarkColor));
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("E-m@il");
        }
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
							/* obtenemos los datos para el envío del correo */
                EditText etSubject = (EditText) findViewById(R.id.etSubject);
                EditText etBody = (EditText) findViewById(R.id.etBody);

					        /* es necesario un intent que levante la actividad deseada */
                Intent itSend = new Intent(android.content.Intent.ACTION_SEND);

                            /* vamos a enviar texto plano a menos que el checkbox está marcado */
                itSend.setType("plain/text");

                            /* colocamos los datos para el envío */
                itSend.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ "hctrom.soporte@gmail.com" });
                itSend.putExtra(android.content.Intent.EXTRA_SUBJECT, etSubject.getText().toString());
                itSend.putExtra(android.content.Intent.EXTRA_TEXT, etBody.getText());
                            /* iniciamos la actividad */
                startActivity(itSend);
            }
        });
    }
}
