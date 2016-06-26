package com.hctrom.romcontrol.backup;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.hctrom.romcontrol.R;
import com.hctrom.romcontrol.ThemeSelectorUtility;
import com.hctrom.romcontrol.alertas.DialogoAlertaReiniciar;
import com.hctrom.romcontrol.prefs.Shell;
import com.hctrom.romcontrol.sh.Scripts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Ivan on 24/05/2016.
 */
public class RestorePreferences {
    private static FragmentActivity myContext;
    ContentResolver cr;
    Context c;

    public RestorePreferences(Context context) {
        c = context;
        cr = c.getContentResolver();

    }

    public void showConfirmDialog() {
        Activity act = (Activity) c;
        ThemeSelectorUtility theme = new ThemeSelectorUtility(c);
        theme.onActivityCreateSetTheme(act);
        //first of all we will display a popup that if confirm will start the real restore
        AlertDialog dialogConfirm = new AlertDialog.Builder(c)
                .setMessage(R.string.confirm_restore)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
                        //checkRestaurar();
                        Toast.makeText(c.getApplicationContext(), "Datos restaurados en: " + prefdir.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                        //here we call the method that is going to restore
                        restoreSettings();
                        soundStock();
                        dialog.dismiss();
                        myContext = (FragmentActivity) c;
                        final DialogoAlertaReiniciar dialogo = new DialogoAlertaReiniciar();
                        dialogo.show(myContext.getSupportFragmentManager(), "tagAlerta");
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                })
                .create();
        dialogConfirm.show();

        Button positive_button = dialogConfirm.getButton(DialogInterface.BUTTON_POSITIVE);
        Button negative_button = dialogConfirm.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (PreferenceManager.getDefaultSharedPreferences(act).getInt("theme_prefs", 0) == 3) {
            dialogConfirm.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg_samsung_light);
            positive_button.setTextColor(act.getResources().getColor(R.color.color_iconos_samsung_light));
            negative_button.setTextColor(act.getResources().getColor(R.color.color_iconos_samsung_light));
        }else if (PreferenceManager.getDefaultSharedPreferences(act).getInt("theme_prefs", 0) == 0){
            dialogConfirm.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg_hct);
            positive_button.setTextColor(act.getResources().getColor(R.color.myAccentColorHCT));
            negative_button.setTextColor(act.getResources().getColor(R.color.myAccentColorHCT));
        }else {
            dialogConfirm.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg_dark_light);
            positive_button.setTextColor(act.getResources().getColor(R.color.myAccentColor));
            negative_button.setTextColor(act.getResources().getColor(R.color.myAccentColor));
        }
    }

    private void soundStock(){
        final SharedPreferences prefs = c.getSharedPreferences("DatosSonidos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("emojis", "1");
        editor.commit();
        editor.putString("sonido_inicio", "1");
        editor.commit();
        editor.putString("nivel_sonido_sistema", "1");
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

    public void restoreSettings() {
        //first of all we delete any existing file in our special file preference folder
        //by check if list of files inside the folder is not null and if so delete each one
        File sddir = new File(Environment.getExternalStorageDirectory().getPath()+"/HCTControl");
        File bkpdir = new File(sddir +"/backup");
        File deletecurr = new File(c.getFilesDir()+"/FilePrefs");
        if (deletecurr.listFiles() != null) {
            for (File deleteBkp : deletecurr.listFiles()) {
                deleteBkp.delete();

            }
        }
        //now we read the backup preference file
        File file = new File(bkpdir +"/prefs_HCTControl.txt");
        if (file.exists())
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                //for each line of the file we spilt it into before the ":" the key part and after the value
                for (String line = br.readLine(); line != null; line = br.readLine()) {
                    String key = line.substring(0, line.indexOf(":"));
                    String value = line.substring(line.indexOf(" ") + 1, line.length());
                    //if value is a bolean we replace the preference value with a value suitable for database
                    //then we are ready to write into db
                    if (value.equals("false")) {
                        Settings.System.putString(cr, String.valueOf(key), "0");
                    } else if (value.equals("true")) {
                        Settings.System.putString(cr, String.valueOf(key), "1");
                    } else
                        Settings.System.putString(cr, String.valueOf(key), String.valueOf(value));
                }
            } catch (IOException e) {
            }
        //now we read the special prefs file
        for (File files : bkpdir.listFiles()) {
            if (files.isFile()) {
                Log.d("restore", "found file"+ files);
                String name = files.getName();
                if (!name.endsWith(".txt")) {
                    Log.d("restore", "file is not txt");
                    File filerestore = new File(deletecurr +"/" + name);
                    Log.d("restore", name);
                    try {
                        filerestore.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        /*
        if (c instanceof MainViewActivity) {
            ((Activity) c).finish();
            Intent start = new Intent(c, MainViewActivity.class);
            start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            c.startActivity(start);
        }
        */
    }

    public void checkRestaurar(){
        Scripts sh = new Scripts();
        final SharedPreferences prefs1 = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor1 = prefs1.edit();

        if (prefs1.getString("switch_bateria_baja", "ON") == "ON"){
            //Toast.makeText(this, "Sonido Batería baja ON", Toast.LENGTH_LONG).show();
            Shell.getRebootAction("" + sh.getBateriaBaja("ON"));
        }else{
            //Toast.makeText(this, "Sonido Batería baja OFF", Toast.LENGTH_LONG).show();
            Shell.getRebootAction("" + sh.getBateriaBaja("OFF"));
            editor1.putString("switch_bateria_baja", "OFF");
            editor1.commit();
        }

        if (prefs1.getString("switch_teclas_volumen", "ON") == "ON"){
            //Toast.makeText(this, "Sonido teclas volumen ON", Toast.LENGTH_LONG).show();
            Shell.getRebootAction("" + sh.getSonidoTeclaVolumen("ON"));
        }else{
            //Toast.makeText(this, "Sonido teclas volumen OFF", Toast.LENGTH_LONG).show();
            Shell.getRebootAction("" + sh.getSonidoTeclaVolumen("OFF"));
            editor1.putString("switch_teclas_volumen", "OFF");
            editor1.commit();
        }

        if (prefs1.getString("switch_sonido_carga", "ON") == "ON"){
            //Toast.makeText(this, "Sonido Cargador ON", Toast.LENGTH_LONG).show();
            Shell.getRebootAction("" + sh.getSonidoCarga("ON"));
        }else{
            //Toast.makeText(this, "Sonido Cargador OFF", Toast.LENGTH_LONG).show();
            Shell.getRebootAction("" + sh.getSonidoCarga("OFF"));
            editor1.putString("switch_sonido_carga", "OFF");
            editor1.commit();
        }

        if (prefs1.getString("switch_sonido_captura", "ON") == "ON"){
            //Toast.makeText(this, "Sonido Captura pantalla ON", Toast.LENGTH_LONG).show();
            Shell.getRebootAction("" + sh.getSonidoCapturaPantalla("ON"));
        }else{
            //Toast.makeText(this, "Sonido Captura pantalla OFF", Toast.LENGTH_LONG).show();
            Shell.getRebootAction("" + sh.getSonidoCapturaPantalla("OFF"));
            editor1.putString("switch_sonido_captura", "OFF");
            editor1.commit();
        }

        final SharedPreferences prefs = c.getSharedPreferences("ui_prefs", Context.MODE_PRIVATE);
        String str_nivel_sonido = "" + prefs.getString("nivel_sonido_sistema", "1");
        if (str_nivel_sonido.contentEquals("2")) {
            //Toast.makeText(this, "Nivel Sonido Medio", Toast.LENGTH_LONG).show();
            Shell.getRebootAction("" + sh.getNivelSonidoMedio());
        }
        if (str_nivel_sonido.contentEquals("3")) {
            //Toast.makeText(this, "Nivel Sonido Alto", Toast.LENGTH_LONG).show();
            Shell.getRebootAction("" + sh.getNivelSonidoAlto());
        }
        if (str_nivel_sonido.contentEquals("4")) {
            //Toast.makeText(this, "Nivel Sonido Extremo", Toast.LENGTH_LONG).show();
            Shell.getRebootAction("" + sh.getNivelSonidoExtremo());
        }

        String str_sonido_inicio = "" + prefs.getString("sonido_inicio", "1");
        if (str_sonido_inicio.contentEquals("2")) {
            //Toast.makeText(this, "Sonido Inicio HCT", Toast.LENGTH_LONG).show();
            Shell.getRebootAction("" + sh.getSonidoInicioHCT());
        }
        if (str_sonido_inicio.contentEquals("3")) {
            //Toast.makeText(this, "Sin Sonido de Inicio", Toast.LENGTH_LONG).show();
            Shell.getRebootAction("" + sh.getSonidoInicioOff());
        }

        String str_emojis = "" + prefs.getString("emojis", "1");
        if (str_emojis.contentEquals("2")) {
            //Toast.makeText(this, "Emojis Google", Toast.LENGTH_LONG).show();
            Shell.getRebootAction("" + sh.getEmojisGoogle());
        }
        if (str_emojis.contentEquals("3")) {
            //Toast.makeText(this, "Emojis iOS", Toast.LENGTH_LONG).show();
            Shell.getRebootAction("" + sh.getEmojisiOS());
        }
    }

}
