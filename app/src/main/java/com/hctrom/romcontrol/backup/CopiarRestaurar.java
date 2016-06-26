package com.hctrom.romcontrol.backup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.hctrom.romcontrol.R;
import com.hctrom.romcontrol.prefs.Shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Ivan on 30/05/2016.
 */
public class CopiarRestaurar {

    // Copiar/Restaurar ajustes HCT Control
    private void showBackupRestoreDialog() {
        final Activity act = new Activity();
        AlertDialog.Builder b = new AlertDialog.Builder(act);
        Adapter adapter = new ArrayAdapter<>(act, R.layout.simple_list_item_single_choice, act.getResources().getStringArray(R.array.backup_items));
        b.setTitle(act.getString(R.string.backup_dialog_title))
                .setSingleChoiceItems((ListAdapter) adapter, PreferenceManager.getDefaultSharedPreferences(act).getInt("backup", 0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        final AlertDialog.Builder confirm = new AlertDialog.Builder(act);
                        //based on position we do something
                        if (position == 0) {
                            dialog.dismiss();
                            //close dialog and open a new one
                            confirm.setMessage(R.string.confirm_backup)
                                    .setTitle("Backup")
                                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            //back.backupUserPrefs(getApplicationContext());
                                            File prefdir = new File("/data/data/com.hctrom.romcontrol/shared_prefs");
                                            File prefdir2 = new File("/data/data/com.hctrom.romcontrol/files");
                                            File filesdir = new File(Environment.getExternalStorageDirectory().getPath()+"/HCTControl/backup/data/shared_prefs");
                                            File filesdir2 = new File(Environment.getExternalStorageDirectory().getPath()+"/HCTControl/backup/data/files");
                                            directoryDelete(filesdir);
                                            directoryDelete(filesdir2);
                                            try {
                                                directoryBackupRestore(prefdir, filesdir);
                                                if (prefdir2.exists()) {
                                                    directoryBackupRestore(prefdir2, filesdir2);
                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                            Toast.makeText(act.getApplicationContext(), "Backup creado en: " + filesdir.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();

                                        }
                                    });
                            Dialog d = confirm.create();
                            d.show();
                            TypedValue typedValue = new TypedValue();
                            Resources.Theme theme = act.getTheme();
                            theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
                            d.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
                        } else if (position == 1 ){
                            dialog.dismiss();
                            confirm.setMessage(R.string.confirm_restore)
                                    .setTitle("Restaurar")
                                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            File prefdir = new File("/data/data/com.hctrom.romcontrol/shared_prefs");
                                            File prefdir2 = new File("/data/data/com.hctrom.romcontrol/files");
                                            File filesdir = new File(Environment.getExternalStorageDirectory().getPath()+"/HCTControl/backup/data/shared_prefs");
                                            File filesdir2 = new File(Environment.getExternalStorageDirectory().getPath()+"/HCTControl/backup/data/files");
                                            directoryDelete(prefdir);
                                            directoryDelete(prefdir2);
                                            try {
                                                directoryBackupRestore(filesdir, prefdir);
                                                if (filesdir2.exists()) {
                                                    directoryBackupRestore(filesdir2, prefdir2);
                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            Toast.makeText(act.getApplicationContext(), "Datos restaurados en: " + prefdir.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                                            //checkRestaurar();
                                            confirm.setMessage("Se recomienda reiniciar el teléfono para terminar de aplicar los cambios.\n\nDesea reiniciar ahora?")
                                                    .setTitle("REINICIAR SISTEMA")
                                                    .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.dismiss();
                                                            Shell.getRebootAction("reboot");
                                                        }
                                                    })
                                                    .setNegativeButton("AHORA NO", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.dismiss();
                                                            // Restart
                                                            Intent reboot = act.getBaseContext().getPackageManager().getLaunchIntentForPackage( act.getBaseContext().getPackageName() );
                                                            reboot.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            act.startActivity(reboot);
                                                        }
                                                    });
                                            Dialog d = confirm.create();
                                            d.show();
                                            TypedValue typedValue = new TypedValue();
                                            Resources.Theme theme = act.getTheme();
                                            theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
                                            d.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
                                        }
                                    })
                                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();

                                        }
                                    });
                            confirm.create();
                            confirm.show();
                        }
                    }
                });
        AlertDialog d = b.create();
        d.show();
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = act.getTheme();
        theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
        d.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
    }

    public static void directoryBackupRestore(File sourceLocation, File targetLocation)
            throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list();
            for (int i = 0; i < sourceLocation.listFiles().length; i++) {

                directoryBackupRestore(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {

            InputStream in = new FileInputStream(sourceLocation);

            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }

    }

    public static void directoryDelete( File dir )
    {

        if ( dir.isDirectory() )
        {
            String [] children = dir.list();
            for ( int i = 0 ; i < children.length ; i ++ )
            {
                File child =    new File( dir , children[i] );
                if(child.isDirectory()){
                    directoryDelete( child );
                    child.delete();
                }else{
                    child.delete();

                }
            }
            dir.delete();
        }
    }

}
