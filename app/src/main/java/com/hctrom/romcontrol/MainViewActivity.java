package com.hctrom.romcontrol;

/*      Created by Palleiro and Aceqott for HCTeam, 2016
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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hctrom.romcontrol.backup.BackupPreferences;
import com.hctrom.romcontrol.blurry.Blurry;
import com.hctrom.romcontrol.changelog.ChangeLog;
import com.hctrom.romcontrol.prefs.Shell;
import com.hctrom.romcontrol.prefs.ThemeSwitch;
import com.software.shell.fab.ActionButton;
import com.stericson.RootTools.RootTools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainViewActivity extends AppCompatActivity
        implements NavigationDrawerCallbacks, View.OnClickListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;
    private Animation fab_open,fab_close;
    int[] ids;
    int[] ids_text;
    private File sddir;
    private File bkpdir;
    ActionButton[] rebootFabs;
    TextView[] rebootFabs_text;
    ActionButton reboot, hotboot, recovery, bl, ui, lch, incall, menu;
    TextView text_reboot, text_hotboot, text_recovery, text_bl, text_ui, text_lch, text_incall;
    View overlay;
    AssetManager am;
    HandleScripts hs;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        /*Calling theme selector class to set theme upon start activity*/
        ThemeSelectorUtility theme = new ThemeSelectorUtility(this);
        theme.onActivityCreateSetTheme(this);
        ThemeSwitch.getIconsColor(getBaseContext());

        if (PreferenceManager.getDefaultSharedPreferences(this).getInt("aviso_backup", 0) == 0) {
            getAvisoBackupDialog();
        }

        //Getting root privileges upon first boot or if was not yet given su
        CheckSu suPrompt = new CheckSu();
        suPrompt.execute();

    }

    @Override
    public void onResume(){
        super.onResume();
        ThemeSwitch.getIconsColor(getBaseContext());
    }

    //Creates a list of NavItem objects to retrieve elements for the Navigation Drawer list of choices
    public List<NavItem> getMenu() {
        List<NavItem> items = new ArrayList<>();
        /*String array of item names is located in strings.xml under name nav_drawer_items
        * If you wish to add more items you need to:
        * 1. Add item to nav_drawer_items array
        * 2. Add a valid material design icon/image to dir drawable
        * 3. Add that image ID to the integer array below (int[] mIcons
        * 4. The POSITION of your new item in the string array MUST CORRESPOND to the position of your image in the integer array mIcons
        * 5. Create new PreferenceFragment or your own fragment or a method that you would like to invoke when a user clicks on your new item
        * 6. Continue down this file to a method onNavigationDrawerItemSelected(int position) - next method
        * 7. Add an action based on position. Remember that positions in array are beginning at 0. So if your item is number 6 in array, it will have a position of 5... etc
        * 8. You need to add same items to the int array in NavigationDrawerFragment, which has the same method*/
        String[] mTitles = getResources().getStringArray(R.array.nav_drawer_items);
        int[] mIcons = {R.drawable.ic_statusbar,
                R.drawable.ic_general,
                R.drawable.ic_notificaciones,
                R.drawable.ic_bloqueo,
                R.drawable.ic_sound_notifications,
                R.drawable.ic_phone_mods,
                R.drawable.ic_twlauncher,
                R.drawable.ic_general_framework,
                R.drawable.ic_theme_menu,
                R.drawable.ic_backup_settings,
                R.drawable.ic_menu,
                R.drawable.ic_acerca_de};
        for (int i = 0; i < mTitles.length && i < mIcons.length; i++) {
            NavItem current = new NavItem();
            current.setText(mTitles[i]);
            current.setDrawable(mIcons[i]);
            items.add(current);
        }

        return items;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        /* update the build_prop_main content by replacing fragments
        * See more detailed instructions on the thread or in annotations to the previous method*/

        setTitle(getMenu().get(position).getText());
        switch (position) {
            case 0:
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.container, new UIPrefsFragment()).commitAllowingStateLoss();
                break;
            case 1:
                getFragmentManager().beginTransaction().replace(R.id.container, GeneralFragment.newInstance("general_prefs")).commit();
                break;
            case 2:
                getFragmentManager().beginTransaction().replace(R.id.container, PhonePrefsFragment.newInstance("phone_prefs")).commit();
                break;
            case 3:
                getFragmentManager().beginTransaction().replace(R.id.container, BloqueoDePantallaFragment.newInstance("bloqueo_de_pantalla_prefs")).commit();
                break;
            case 4:
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.container, new SonidosNotificacionesFragment()).commitAllowingStateLoss();
                break;
            case 5:
                getFragmentManager().beginTransaction().replace(R.id.container, TelefonoLlamadasFragment.newInstance("telefono_llamadas_prefs")).commit();
                break;
            case 6:
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.container, new TWLauncherFragment()).commitAllowingStateLoss();
                break;
            case 7:
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.container, new FrameworksGeneralFragment()).commitAllowingStateLoss();
                break;
            case 8:
                showThemeChooserDialog();
                break;
            case 9:
                BackupPreferences rp = new BackupPreferences(MainViewActivity.this);
                rp.showBackupDialog();
                //startActivity(new Intent(this, MainActivity.class));
                break;
            case 10:
                ChangeLog cl = new ChangeLog(this);
                //if (cl.isFirstRun()) {
                cl.getLogDialog().show();
                //}
                break;
            case 11:
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.container, new AcercaDeFragment()).commitAllowingStateLoss();
                break;

        }
    }

    private static long back_pressed;

    @Override
    public void onBackPressed() {
        if (overlay.getVisibility() == View.VISIBLE){
            Blurry.delete((ViewGroup) findViewById(R.id.content));
            for (int i = 0; i < rebootFabs.length; i++) {
                overlay.setVisibility(View.GONE);
                rebootFabs[i].hide();
                rebootFabs_text[i].setVisibility(View.GONE);
            }
        }else if (mNavigationDrawerFragment.isDrawerOpen()){
            mNavigationDrawerFragment.closeDrawer();
        }else if (back_pressed + 2000 > System.currentTimeMillis()){
            super.onBackPressed();
        }
        else{
            Toast.makeText(getBaseContext(), "Pulsa de nuevo para Salir", Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        SharedPreferences prefs = getSharedPreferences("ConfigMenuFlotante", Context.MODE_PRIVATE);
        if(prefs.getBoolean("floating_button_activador", true)){
            (menu.findItem(R.id.option1)).setChecked(true);
        }else {
            (menu.findItem(R.id.option1)).setChecked(false);
        }

        if(prefs.getBoolean("floating_button_vista", true)){
            (menu.findItem(R.id.option2)).setChecked(true);
            (menu.findItem(R.id.option1)).setCheckable(true);
        }else {
            (menu.findItem(R.id.option2)).setChecked(false);
            (menu.findItem(R.id.option1)).setCheckable(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences.Editor editor;
        SharedPreferences prefs = getSharedPreferences("ConfigMenuFlotante", Context.MODE_PRIVATE);
        switch(item.getItemId()){
            case R.id.option1:
                if(item.isChecked()){
                    item.setChecked(false);
                    menu.setEnabled(false);
                    Toast.makeText(MainViewActivity.this, "Menú flotante desactivado", Toast.LENGTH_LONG).show();
                    editor = prefs.edit();
                    editor.putBoolean("floating_button_activador",false);
                    editor.commit();
                }else{
                    item.setChecked(true);
                    menu.setEnabled(true);
                    Toast.makeText(MainViewActivity.this, "Menú flotante activado", Toast.LENGTH_LONG).show();
                    editor = prefs.edit();
                    editor.putBoolean("floating_button_activador",true);
                    editor.commit();
                }
                break;
            case R.id.option2:
                if(item.isChecked()){
                    item.setChecked(false);
                    fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
                    menu.startAnimation(fab_close);
                    menu.hide();
                    Toast.makeText(MainViewActivity.this, "Menú flotante oculto", Toast.LENGTH_LONG).show();
                    editor = prefs.edit();
                    editor.putBoolean("floating_button_vista",false);
                    editor.commit();
                }else{
                    item.setChecked(true);
                    fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
                    menu.startAnimation(fab_open);
                    menu.show();
                    Toast.makeText(MainViewActivity.this, "Menú flotante visible", Toast.LENGTH_LONG).show();
                    editor = prefs.edit();
                    editor.putBoolean("floating_button_vista",true);
                    editor.commit();
                }
                break;
            case R.id.action_reboot:
                    showHideRebootMenu();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*Handling onClick event for the Reboot Menu (round Action Buttons array)
    * For now we handle them under su, later on, since app is intended to be a system app,
    * we will add PowerManager for items: Reboot, Reboot recovery and Reboot Download*/
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            /*Handles the onClick event for the semi transparent white overlay
            * Once clicked, we consider it a click outside the Reboot Menu and it invokes methos showHideRebootMenu()*/
            case R.id.overlay:
                showHideRebootMenu();
                break;
            case R.id.action_reboot:
                Shell.getRebootAction("reboot");
                break;
            case R.id.action_reboot_hotboot:
                Shell.getRebootAction("busybox killall system_server");
                break;
            case R.id.action_reboot_recovery:
                Shell.getRebootAction("reboot recovery");
                break;
            case R.id.action_reboot_bl:
                Shell.getRebootAction("reboot download");
                break;
            case R.id.action_reboot_systemUI:
                Shell.getRebootAction("pkill com.android.systemui");
                break;
            case R.id.action_reboot_launcher:
                Shell.getRebootAction("pkill com.sec.android.app.launcher");
                break;
            case R.id.action_reboot_incallui:
                Shell.getRebootAction("pkill com.android.incallui");
                break;
        }
    }

    //Initializes the reboot menu as arrray of views, finds by id and sets animations and onClickListener to each in a loop
    private void initRebootMenu() {
        ids_text = new int[]{R.id.text_action_reboot, R.id.text_action_reboot_hotboot, R.id.text_action_reboot_recovery, R.id.text_action_reboot_bl, R.id.text_action_reboot_systemUI, R.id.text_action_reboot_launcher, R.id.text_action_reboot_incallui};
        ids = new int[]{R.id.action_reboot, R.id.action_reboot_hotboot, R.id.action_reboot_recovery, R.id.action_reboot_bl, R.id.action_reboot_systemUI, R.id.action_reboot_launcher, R.id.action_reboot_incallui};
        rebootFabs_text = new TextView[]{text_reboot, text_hotboot, text_recovery, text_bl, text_ui, text_lch, text_incall};
        rebootFabs = new ActionButton[]{reboot, hotboot, recovery, bl, ui, lch, incall};
        overlay = findViewById(R.id.overlay);
        int l = ids.length;
        for (int i = 0; i < l; i++) {
            rebootFabs[i] = (ActionButton) findViewById(ids[i]);
            rebootFabs[i].hide();
            rebootFabs[i].setHideAnimation(ActionButton.Animations.ROLL_TO_RIGHT);
            rebootFabs[i].setShowAnimation(ActionButton.Animations.ROLL_FROM_RIGHT);
            rebootFabs_text[i] = (TextView) findViewById(ids_text[i]);
            rebootFabs_text[i].setVisibility(View.GONE);
        }
    }

    //Show/Hide reboot menu with animation depending on the view's visibility
    public void showHideRebootMenu() {

        for (int i = 0; i < rebootFabs.length; i++) {
            if (rebootFabs[i].isShown()) {
                overlay.setVisibility(View.GONE);
                rebootFabs[i].hide();
                rebootFabs_text[i].setVisibility(View.GONE);
            } else {
                overlay.setVisibility(View.VISIBLE);
                rebootFabs[i].show();
                rebootFabs_text[i].setVisibility(View.VISIBLE);
                GradientDrawable gd = new GradientDrawable();
                gd.setColor(getResources().getColor(R.color.button_material_dark)); // Changes this drawbale to use a single color instead of a gradient
                gd.setCornerRadius(20);
                gd.setStroke(1, 0xFF000000);
                rebootFabs_text[i].setBackgroundDrawable(gd);
            }
        }

        if (!overlay.isShown()){
            Blurry.delete((ViewGroup) findViewById(R.id.content));
        }else{
            Blurry.with(MainViewActivity.this)
                    .radius(25)
                    .sampling(2)
                    .async()
                    .animate(200)
                    .onto((ViewGroup) findViewById(R.id.content));
        }
    }

    //Activates a chosen theme based on single choice list dialog, which opens upon selecting item at position 4 in nav drawer list
    private void getAvisoBackupDialog() {
        View checkBoxView = View.inflate(this, R.layout.aviso_backup_text, null);
        final CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkBoxAvisoBackup);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (checkBox.isChecked()){
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt("aviso_backup", 1).commit();
                }else{
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt("aviso_backup", 0).commit();
                }
            }
        });
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setView(checkBoxView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
        ;
        AlertDialog d = b.create();
        d.show();
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = this.getTheme();
        theme.resolveAttribute(R.attr.colorAccent, typedValue, true);

        Button cancel = d.getButton(AlertDialog.BUTTON_NEGATIVE);
        cancel.setTextColor(typedValue.data);
        Button ok = d.getButton(AlertDialog.BUTTON_POSITIVE);
        ok.setTextColor(typedValue.data);
        d.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
    }

    //Activates a chosen theme based on single choice list dialog, which opens upon selecting item at position 4 in nav drawer list
    private void showThemeChooserDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        Adapter adapter = new ArrayAdapter<>(this, R.layout.simple_list_item_single_choice, getResources().getStringArray(R.array.theme_items));
        b.setIcon(R.drawable.ic_htc_personalize)
         .setTitle(getString(R.string.theme_chooser_dialog_title))
                .setSingleChoiceItems((ListAdapter) adapter, PreferenceManager.getDefaultSharedPreferences(this).getInt("theme_prefs", 0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Invokes method initTheme(int) - next method based on chosen theme
                        initTheme(which);
                    }
                })
        ;
        AlertDialog d = b.create();
        d.show();
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = this.getTheme();
        theme.resolveAttribute(R.attr.colorAccent, typedValue, true);

        Button cancel = d.getButton(AlertDialog.BUTTON_NEGATIVE);
        cancel.setTextColor(typedValue.data);
        Button ok = d.getButton(AlertDialog.BUTTON_POSITIVE);
        ok.setTextColor(typedValue.data);
        d.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
        ListView lv = d.getListView();
        int paddingTop = Math.round(this.getResources().getDimension(R.dimen.dialog_listView_top_padding));
        int paddingBottom = Math.round(this.getResources().getDimension(R.dimen.dialog_listView_bottom_padding));
        lv.setPadding(0, paddingTop, 0, paddingBottom);
    }

    /*Writes the chosen position integer (in theme chooser dialog) into common shared preferences.
    * Based on that integer (currently 0 or 1), a helper class ThemeSelectorUtility (which is called at the very beginning of onCreate)
    * then reads that integer when it's instantiated and sets the theme for the activity.
    * The activity is them rebooted, overriding pending transitions, to make the theme switch seemless.*/
    private void initTheme(int i) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt("theme_prefs", i).commit();
        finish();
        this.overridePendingTransition(0, R.animator.fadeout);
        startActivity(new Intent(this, MainViewActivity.class));
        this.overridePendingTransition(R.animator.fadein, 0);

    }

    //Asynchronous class to ask for su rights at the beginning of the activity. If the root rights have been denied or the device is not rooted, the app will not run.
    public class CheckSu extends AsyncTask<Void, Void, Boolean> {
        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainViewActivity.this);
            mProgressDialog.setMessage(getString(R.string.gaining_root));
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            //Accessing the ability of the device to get root and the ability of app to achieve su privileges.
            sddir = new File(Environment.getExternalStorageDirectory().getPath() + "/HCTControl");
            bkpdir = new File(sddir + "/backup");
            if (RootTools.isAccessGiven()) {
                if (!sddir.exists()) {
                    Log.d("sddir", "sddir doesn't exists");
                    sddir.mkdir();
                    bkpdir.mkdir();
                }
                am = getAssets();
                //Calling the helper class HandleScripts to copy scripts to the files folder and chmod 755.
                //Scripts can be then accessed and executed using script#scriptname key for PreferenceScreen in PreferenceFragments
                hs = new HandleScripts(MainViewActivity.this);
                hs.copyAssetFolder();
                return true;

            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mProgressDialog.dismiss();
            //If the device is not rooted or su has been denied the app will not run.
            //A dialog will be shown announcing that with a single button, upon clicking which the activity will finish.
            if (!RootTools.isAccessGiven()) {
                //If no su access detected, throw and alert dialog with single button that will finish the activity
                AlertDialog.Builder mNoSuBuilder = new AlertDialog.Builder(MainViewActivity.this);
                mNoSuBuilder.setTitle(R.string.missing_su_title);
                mNoSuBuilder.setMessage(R.string.missing_su);
                mNoSuBuilder.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                mNoSuBuilder.create();
                Dialog mNoSu = mNoSuBuilder.create();
                mNoSu.show();


            } else {
                //Provided the su privileges have been established, we run the activity as usual, beginning with setting content view
                setContentView(R.layout.activity_main_view);
                getWindow().setStatusBarColor(getResources().getColor(R.color.statusbar));
                mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
                setSupportActionBar(mToolbar);
                //ThemeSwitch.getIconsColor(getBaseContext());
                menu = (ActionButton) findViewById(R.id.action_menu);

                SharedPreferences prefs = getSharedPreferences("ConfigMenuFlotante", Context.MODE_PRIVATE);
                if(prefs.getBoolean("floating_button_vista", true)){
                    menu.show();
                    if(prefs.getBoolean("floating_button_activador", true)){
                        menu.setEnabled(true);
                    }else{
                        menu.setEnabled(false);
                    }
                }else{
                    menu.hide();
                }

                menu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mNavigationDrawerFragment.openDrawer();

                    }
                });

                mNavigationDrawerFragment = (NavigationDrawerFragment)
                        getFragmentManager().findFragmentById(R.id.fragment_drawer);

                // Set up the drawer. Look in NavigationDrawerFragment for more details
                mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar, MainViewActivity.this);
                initRebootMenu();
//                try {
//                    remountSystem("rw");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }


        }
    }
//
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        try {
//            remountSystem("rw");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        try {
//            remountSystem("ro");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        try {
//            remountSystem("ro");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    //To use this method when the user interacts with the app, you need to remove the outcommenting from all the previous methods and a from the onPostExecute of async task CheckSu
    private void remountSystem(String mount) throws Exception {
        String system = "/system";
        String mounted = RootTools.getMountedAs(system);
        boolean isMountedRW = mounted.equals("rw") ? true : false;
        if (isMountedRW && mount.equals("ro")) {
            RootTools.remount(system, "ro");
        } else if(!isMountedRW && mount.equals("rw")){
            RootTools.remount(system, "rw");
        }
    }
}
