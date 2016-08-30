/*
 * Copyright (C) 2016 Nishant Srivastava
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hctrom.romcontrol.packagehunter;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.hctrom.romcontrol.R;
import com.hctrom.romcontrol.ThemeSelectorUtility;
import com.hctrom.romcontrol.packagehunter.hunter.PackageHunter;
import com.hctrom.romcontrol.packagehunter.hunter.PkgInfo;

import java.util.ArrayList;

public class PackageHunterMain extends AppCompatActivity {

  private ArrayList<PkgInfo> pkgInfoArrayList;
  private RVMainAdapter adapter;

  private PackageHunter packageHunter;
  private Toolbar toolbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      ThemeSelectorUtility theme = new ThemeSelectorUtility(this);
      theme.onActivityCreateSetTheme(this);
      setContentView(R.layout.appinfo_activity_main);
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

      packageHunter = new PackageHunter(this);

      RecyclerView rv = (RecyclerView) findViewById(R.id.rv_pkglist);
      pkgInfoArrayList = packageHunter.getInstalledPackages();

      adapter = new RVMainAdapter(this, pkgInfoArrayList);
      rv.hasFixedSize();
      rv.setLayoutManager(new LinearLayoutManager(this));
      rv.setAdapter(adapter);

      // Set On Click
      rv.addOnItemTouchListener(
          new RVItemClickListener(this, new RVItemClickListener.OnItemClickListener() {
            @Override public void onItemClick(View view, int position) {
              Intent i = new Intent(PackageHunterMain.this, DetailActivity.class);
              i.putExtra("data", pkgInfoArrayList.get(position).getPackageName());
              startActivity(i);
            }
          }));
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.appinfo_menu_main, menu);
      MenuItem searchViewItem = menu.findItem(R.id.action_search);
      final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);
      searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
          @Override public boolean onQueryTextSubmit(String query) {
            searchViewAndroidActionBar.clearFocus();
            return true;
          }

          @Override public boolean onQueryTextChange(String query) {

            pkgInfoArrayList = packageHunter.searchInList(query, PackageHunter.PACKAGES);
            adapter.updateWithNewListData(pkgInfoArrayList);

            return false;
          }
      });
      return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
          case R.id.action_refresh:
              Toast.makeText(getBaseContext(), "Lista Apps\nActualizada", Toast.LENGTH_SHORT).show();
              pkgInfoArrayList = packageHunter.getInstalledPackages();
              adapter.updateWithNewListData(pkgInfoArrayList);
              break;
          case android.R.id.home:
              // app icon in action bar clicked; goto parent activity.
              this.finish();
              break;
      }
      return super.onOptionsItemSelected(item);
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
}
