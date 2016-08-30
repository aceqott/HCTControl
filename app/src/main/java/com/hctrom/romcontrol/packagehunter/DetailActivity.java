package com.hctrom.romcontrol.packagehunter;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.hctrom.romcontrol.R;
import com.hctrom.romcontrol.ThemeSelectorUtility;
import com.hctrom.romcontrol.packagehunter.hunter.PackageHunter;

import java.util.ArrayList;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {
  private Toolbar toolbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeSelectorUtility theme = new ThemeSelectorUtility(this);
        theme.onActivityCreateSetTheme(this);
        setContentView(R.layout.appinfo_activity_detail);
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
            getSupportActionBar().setTitle("App Info");
        }
        toolbar.setElevation(0);

        String packageName = getIntent().getStringExtra("data");
        PackageHunter packageHunter = new PackageHunter(this);

        String version = packageHunter.getVersionForPkg(packageName);
        String versionCode = packageHunter.getVersionCodeForPkg(packageName);
        String appName = packageHunter.getAppNameForPkg(packageName);
        long firstInstallTime = packageHunter.getFirstInstallTimeForPkg(packageName);
        long lastUpdateTime = packageHunter.getLastUpdatedTimeForPkg(packageName);
        Drawable icon = packageHunter.getIconForPkg(packageName);

        String[] permissions = packageHunter.getPermissionForPkg(packageName);
        String[] activities = packageHunter.getActivitiesForPkg(packageName);
        String[] services = packageHunter.getServicesForPkg(packageName);
        String[] providers = packageHunter.getProvidersForPkg(packageName);
        String[] receivers = packageHunter.getReceiverForPkg(packageName);

        TextView txt_version = (TextView) findViewById(R.id.txtvw_vname);
        TextView txt_versioncode = (TextView) findViewById(R.id.txtvw_vc);
        TextView txt_pkg = (TextView) findViewById(R.id.txtvw_pkgname);
        ImageView img_icon = (ImageView) findViewById(R.id.imgvw_icn);

        TextView txt_firsttime = (TextView) findViewById(R.id.txtvw_firsttime);
        TextView txt_lastupdated = (TextView) findViewById(R.id.txtvw_lastupdated);

        img_icon.setImageDrawable(icon);
        txt_version.setText("Version : " + version);
        txt_versioncode.setText("Version Code : " + versionCode);
        txt_pkg.setText(packageName);
        txt_firsttime.setText("First Install Time : " + getFormattedUpTime(firstInstallTime));
        txt_lastupdated.setText("Last Update Time : " + getFormattedUpTime(lastUpdateTime));

        if (getSupportActionBar() != null) getSupportActionBar().setTitle(appName);

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv_detaillist);
        ArrayList<ElementInfo> elementInfoArrayList = new ArrayList<>();
        elementInfoArrayList.add(new ElementInfo("Permissions", permissions));
        elementInfoArrayList.add(new ElementInfo("Services", services));
        elementInfoArrayList.add(new ElementInfo("Activities", activities));
        elementInfoArrayList.add(new ElementInfo("Providers", providers));
        elementInfoArrayList.add(new ElementInfo("Receivers", receivers));

        RVDetailsAdapter adapter = new RVDetailsAdapter(elementInfoArrayList);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
      }

      @Override
      public boolean onKeyUp( int keyCode, KeyEvent event ) {
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
                  //app icon in action bar clicked; goto parent activity.
                  this.finish();
                  return true;
              default:
                  return super.onOptionsItemSelected(item);
          }
      }

      private String getFormattedUpTime(long millis) {
          int sec = (int) (millis / 1000) % 60;
          int min = (int) ((millis / (1000 * 60)) % 60);
          int hr = (int) ((millis / (1000 * 60 * 60)) % 24);

          return String.format(Locale.US, "%02d:%02d:%02d", hr, min, sec);
      }
}
