package com.hctrom.romcontrol;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.hctrom.romcontrol.alertas.DialogoAlertaDominioDelete;
import com.hctrom.romcontrol.hosts.HfmHelpers;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Ivan on 09/06/2016.
 */
public class HostWhiteListView extends AppCompatActivity {
    private ListView listView ;
    private Toolbar toolbar;
    private EditText editText;
    private Button buttonAdd, buttonDelete, buttonAplicar;
    ArrayList<String> m_listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeSelectorUtility theme = new ThemeSelectorUtility(this);
        theme.onActivityCreateSetTheme(this);
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("theme_prefs", 0) == 3) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.myPrimaryDarkColorSamsungLight));
        }else if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("theme_prefs", 0) == 0){
            getWindow().setStatusBarColor(getResources().getColor(R.color.myPrimaryDarkColorHCT));
        }else{
            getWindow().setStatusBarColor(getResources().getColor(R.color.myPrimaryDarkColor));
        }
        setContentView(R.layout.host_white_listview);
        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Lista Blanca Hosts");
        }

        editText = (EditText) findViewById(R.id.editTextDominio);
        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonDelete = (Button) findViewById(R.id.buttonDelete);
        buttonAplicar = (Button) findViewById(R.id.buttonAplicar);
        listView = (ListView) findViewById(R.id.list);

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, m_listItems);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        final SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int v = data.getInt("Dominio_value", 50);
        if (v == 50){
            buttonAplicar.setVisibility(View.INVISIBLE);
            buttonDelete.setVisibility(View.INVISIBLE);
            editText.setHint("ihatepops.com    <- EJEMPLO");
        }else {
            buttonAplicar.setVisibility(View.VISIBLE);
            buttonDelete.setVisibility(View.VISIBLE);
            editText.setHint("");
            LoadPreferences();
        }

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // ListView Clicked item index
                int itemPosition     = position;

                // ListView Clicked item value
                String  itemValue    = (String) listView.getItemAtPosition(position);

                // Show Alert
                Toast.makeText(getApplicationContext(), "Position :" + itemPosition + "  ListItem : " + itemValue , Toast.LENGTH_LONG).show();
            }

        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogoAlertaDominioDelete dialogo = new DialogoAlertaDominioDelete();
                dialogo.show(getSupportFragmentManager(), "tagAlerta");
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String input = editText.getText().toString();
                if(null != input && input.length() > 0) {
                    SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = data.edit();
                    m_listItems.add(input);
                    adapter.notifyDataSetChanged();
                    int i = data.getInt("Dominio_value", 0);

                    SavePreferences("Dominio_item_" + i, input);

                    i = i + 1;
                    editor.putInt("Dominio_value", i);
                    editor.commit();
                    editText.setText("");
                    editText.setHint("");
                    buttonAplicar.setVisibility(View.VISIBLE);
                    buttonDelete.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(getApplicationContext(), "Por favor, introduzca un texto!!", Toast.LENGTH_LONG).show();
                }
            }
        });

        buttonAplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    HfmHelpers.RunAsRoot("" + loadWhiteList());
                    Toast.makeText(getApplicationContext(), "Lista Blanca aplicada con éxito!!", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected String loadWhiteList(){
        SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int i = data.getInt("Dominio_value", 0);
        String cmd = "";
        int x = 0;
        while (x < i){
            String edittext = "" + data.getString("Dominio_item_" + x, "");
            String web = edittext;
            cmd = cmd + " && sed -i '/" + web + "/d' /etc/hosts";  //Remove line content
            x++;
        }
        cmd = "mount -o rw,remount /system" + cmd + " ; mount -o ro,remount /system";
        return cmd;
    }

    protected void SavePreferences(String key, String value) {
        // TODO Auto-generated method stub
        SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = data.edit();
        editor.putString(key, value);
        editor.commit();


    }

    protected void LoadPreferences(){
        SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(this);
        int i = data.getInt("Dominio_value", 0);

        int x = 0;
        while (x <= i) {
            String dataSet = data.getString("Dominio_item_" + x, "");

            adapter.add(dataSet);
            adapter.notifyDataSetChanged();
            x++;
        }
    }

    @Override
    public boolean onKeyUp( int keyCode, KeyEvent event )
    {
        if( keyCode == KeyEvent.KEYCODE_BACK ) {
            onBackPressed();
            return true;
        }else {
            return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Reinicia una Activity
    public static void reiniciarActivity(Activity actividad){
        Intent intent=new Intent();
        intent.setClass(actividad, actividad.getClass());
        //llamamos a la actividad
        actividad.startActivity(intent);
        //finalizamos la actividad actual
        actividad.finish();

    }
}
