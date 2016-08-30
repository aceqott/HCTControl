package com.hctrom.romcontrol.hexconverter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.hctrom.romcontrol.R;
import com.hctrom.romcontrol.ThemeSelectorUtility;

/*      Created by Roberto Mariani and Anna Berkovitch, 28/06/2016
        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

public class HexConverterMain extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String URI_KEY = "uri_key";
    public static final String FRAGMENT_CODE_KEY = "fragment_code";
    private SharedPreferences mSharedPreferences;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeSelectorUtility theme = new ThemeSelectorUtility(this);
        theme.onActivityCreateSetTheme(this);
        setContentView(R.layout.hexconverter_activity_main);
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("theme_prefs", 0) == 3) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.myPrimaryDarkColorSamsungLight));
        }else if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("theme_prefs", 0) == 4){
            getWindow().setStatusBarColor(getResources().getColor(R.color.myPrimaryDarkColorMaterialDark));
        }else if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("theme_prefs", 0) == 0){
            getWindow().setStatusBarColor(getResources().getColor(R.color.myPrimaryDarkColorHCT));
        }else{
            getWindow().setStatusBarColor(getResources().getColor(R.color.myPrimaryDarkColor));
        }
        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("HexConverter");
        }
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int selectedFragment = mSharedPreferences.getInt(FRAGMENT_CODE_KEY, 0);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(R.id.fragmentContainer, SelectorFragment.newInstance()).commit();
        }

        setUpSpinner(selectedFragment);
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

    private void setUpSpinner(int selectedFragment) {
        Spinner spinner = (Spinner) findViewById(R.id.selectorSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_values, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(selectedFragment);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == 46) {
            if (data != null && data.getData() != null) {
                mSharedPreferences.edit().putString(URI_KEY, data.getData().toString()).apply();
                getFragmentManager().beginTransaction().replace(R.id.fragmentContainer, SelectorFragment.newInstance()).commit();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (position == 1 && mSharedPreferences.getString(URI_KEY, null) == null) {
            Intent getContentIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            getContentIntent.setType("image/*");
            startActivityForResult(getContentIntent, 46);
        }
        mSharedPreferences.edit().putInt(FRAGMENT_CODE_KEY, position).apply();
        getFragmentManager().beginTransaction().replace(R.id.fragmentContainer, SelectorFragment.newInstance()).commit();

    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
