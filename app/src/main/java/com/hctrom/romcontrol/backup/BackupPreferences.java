package com.hctrom.romcontrol.backup;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.hctrom.romcontrol.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Created by Ivan on 24/05/2016.
 */
public class BackupPreferences {
    File sddir = new File(Environment.getExternalStorageDirectory().getPath() + "/HCTControl");
    File bkpdir = new File(sddir + "/backup");
    private static FragmentActivity myContext;
    ContentResolver cr;
    Context c;

    public BackupPreferences(Context context) {
        c = context;
        cr = c.getContentResolver();

    }

    // Copiar/Restaurar ajustes HCT Control
    public void showBackupDialog() {
        File myDirectory = new File(Environment.getExternalStorageDirectory(), "/HCTControl/backup/data");
        if(!myDirectory.exists()) {
            myDirectory.mkdirs();
        }
        AlertDialog.Builder b = new AlertDialog.Builder(c);
        Adapter adapter = new ArrayAdapter<>(c, R.layout.simple_list_item_single_choice, c.getResources().getStringArray(R.array.backup_items));
        b.setTitle(R.string.backup_dialog_title)
                .setSingleChoiceItems((ListAdapter) adapter, PreferenceManager.getDefaultSharedPreferences(c).getInt("backup", 0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        //based on position we do something
                        if (position == 0) {
                            dialog.dismiss();
                            //close dialog and open a new one
                            AlertDialog.Builder confirm = new AlertDialog.Builder(c);
                            confirm.setMessage(R.string.confirm_backup)
                                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    soundStock();
                                                    File prefdir = new File("/data/data/com.hctrom.romcontrol/shared_prefs");
                                                    File prefdir2 = new File("/data/data/com.hctrom.romcontrol/files");
                                                    File filesdir = new File(Environment.getExternalStorageDirectory().getPath()+"/HCTControl/backup/data/shared_prefs");
                                                    File filesdir2 = new File(Environment.getExternalStorageDirectory().getPath()+"/HCTControl/backup/data/files");
                                                    RestorePreferences.directoryDelete(filesdir);
                                                    RestorePreferences.directoryDelete(filesdir2);
                                                    try {
                                                        RestorePreferences.directoryBackupRestore(prefdir, filesdir);
                                                        if (prefdir2.exists()) {
                                                            RestorePreferences.directoryBackupRestore(prefdir2, filesdir2);
                                                        }
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                    backupPreferences();
                                                    //if file for auto_restore is present we delete it
                                                    File file = new File(bkpdir + "/auto_restore.txt");
                                                    if (file.exists()) {
                                                        file.delete();
                                                    }
                                                    Toast.makeText(c.getApplicationContext(), "Backup creado en: " + filesdir.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                    )
                                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();

                                        }
                                    });
                            AlertDialog dialogConfirm = confirm.create();
                            dialogConfirm.show();
                            Button positive_button = dialogConfirm.getButton(DialogInterface.BUTTON_POSITIVE);
                            Button negative_button = dialogConfirm.getButton(DialogInterface.BUTTON_NEGATIVE);
                            if (PreferenceManager.getDefaultSharedPreferences(c).getInt("theme_prefs", 0) == 3) {
                                dialogConfirm.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg_samsung_light);
                                positive_button.setTextColor(c.getResources().getColor(R.color.color_iconos_samsung_light));
                                negative_button.setTextColor(c.getResources().getColor(R.color.color_iconos_samsung_light));
                            }else if (PreferenceManager.getDefaultSharedPreferences(c).getInt("theme_prefs", 0) == 4){
                                dialogConfirm.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg_hct);
                                positive_button.setTextColor(c.getResources().getColor(R.color.myAccentColorMaterialDark));
                                negative_button.setTextColor(c.getResources().getColor(R.color.myAccentColorMaterialDark));
                            }else if (PreferenceManager.getDefaultSharedPreferences(c).getInt("theme_prefs", 0) == 0){
                                dialogConfirm.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg_hct);
                                positive_button.setTextColor(c.getResources().getColor(R.color.myAccentColorHCT));
                                negative_button.setTextColor(c.getResources().getColor(R.color.myAccentColorHCT));
                            }else {
                                dialogConfirm.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg_dark_light);
                                positive_button.setTextColor(c.getResources().getColor(R.color.myAccentColor));
                                negative_button.setTextColor(c.getResources().getColor(R.color.myAccentColor));
                            }
                        } else if (position == 1 ){
                            dialog.dismiss();
                            RestorePreferences rp = new RestorePreferences(c);
                            //this is the call for constructor
                            rp.showConfirmDialog();
                        }
                    }
                });
        AlertDialog dialogConfirm = b.create();
        dialogConfirm.show();
        Button positive_button = dialogConfirm.getButton(DialogInterface.BUTTON_POSITIVE);
        Button negative_button = dialogConfirm.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (PreferenceManager.getDefaultSharedPreferences(c).getInt("theme_prefs", 0) == 3) {
            dialogConfirm.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg_samsung_light);
            positive_button.setTextColor(c.getResources().getColor(R.color.color_iconos_samsung_light));
            negative_button.setTextColor(c.getResources().getColor(R.color.color_iconos_samsung_light));
        }else if (PreferenceManager.getDefaultSharedPreferences(c).getInt("theme_prefs", 0) == 4){
            dialogConfirm.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg_hct);
            positive_button.setTextColor(c.getResources().getColor(R.color.myAccentColorMaterialDark));
            negative_button.setTextColor(c.getResources().getColor(R.color.myAccentColorMaterialDark));
        }else if (PreferenceManager.getDefaultSharedPreferences(c).getInt("theme_prefs", 0) == 0){
            dialogConfirm.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg_hct);
            positive_button.setTextColor(c.getResources().getColor(R.color.myAccentColorHCT));
            negative_button.setTextColor(c.getResources().getColor(R.color.myAccentColorHCT));
        }else {
            dialogConfirm.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg_dark_light);
            positive_button.setTextColor(c.getResources().getColor(R.color.myAccentColor));
            negative_button.setTextColor(c.getResources().getColor(R.color.myAccentColor));
        }
    }

    private void soundStock(){
        final SharedPreferences prefs = c.getSharedPreferences("DatosSonidos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("emojis", "1");
        editor.commit();
        editor.putString("sonido_inicio", "1");
        editor.commit();
        editor.putString("sonido_nivel_sistema", "1");
        editor.commit();
        SharedPreferences prefs1 = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor1 = prefs1.edit();
        editor1.putBoolean("switch_bateria_baja", true);
        editor1.commit();
        editor1.putBoolean("switch_teclas_volumen", true);
        editor1.commit();
        editor1.putBoolean("switch_sonido_carga", true);
        editor1.commit();
        editor1.putBoolean("switch_sonido_captura", true);
        editor1.commit();
    }

    private void backupPreferences() {
        //we go into the shared_prefs folder and we read the files name's
        File prefdir = new File("/data/data/com.hctrom.romcontrol/shared_prefs");
        File filesdir = new File("/data/data/com.hctrom.romcontrol/FilePrefs/files");
        if (bkpdir.listFiles() != null) {
            for (File deleteBkp : bkpdir.listFiles()) {
                deleteBkp.delete();

            }
        }
        for (File f : prefdir.listFiles()) {
            if (f.isFile()) {
                String name = f.getName();
                //if file is defult pref we don't backup as it doesn't go in the bd
                if (!name.equals("com.hctrom.romcontrol_preferences.xml")) {
                    //we remove the xml suffix from filename and we assign it in the loop to the shared prefs
                    //this way we can use the getAll method of shared prefs for all the existing files in the folder
                    name = name.replace(".xml", "");
                    SharedPreferences pref = c.getSharedPreferences(name, Context.MODE_PRIVATE);
                    //now we write to the backup file
                    Map<String, ?> allEntries = pref.getAll();
                    for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                        try(
                                PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(bkpdir + "/prefs_HCTControl.txt", true)))) {
                            out.println(entry.getKey() + ": " + entry.getValue().toString());

                        }catch (IOException e) {

                        }
                    }
                }
            }
        }

        if (filesdir.exists()) {
            for (File files : filesdir.listFiles()) {
                if (files.isFile()) {
                    String name = files.getName();
                    if (!name.contains("com.hctrom")) {
                        File filebackup = new File(bkpdir + "/" + name);
                        try {
                            filebackup.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
