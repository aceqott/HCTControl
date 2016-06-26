package com.hctrom.romcontrol;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ImagenPerfil extends AppCompatActivity implements View.OnClickListener {
    private Button btnAction, btnDelete, btnAplicar;
    private CheckBox checkBoxImage;
    private Toolbar toolbar;
    private CircleImageView imgvw;
    private TextView text;
    private EditText editText;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ThemeSelectorUtility theme = new ThemeSelectorUtility(this);
        theme.onActivityCreateSetTheme(this);
        setContentView(R.layout.imagen_perfil);
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("theme_prefs", 0) == 3) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.myPrimaryDarkColorSamsungLight));
        }else if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("theme_prefs", 0) == 0){
            getWindow().setStatusBarColor(getResources().getColor(R.color.myPrimaryDarkColorHCT));
        }else{
            getWindow().setStatusBarColor(getResources().getColor(R.color.myPrimaryDarkColor));
        }
        SharedPreferences prefs = getSharedPreferences("DatosLogin", Context.MODE_PRIVATE);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Inicio de sesión");
        }
        btnAction = (Button)findViewById(R.id.btnPic);
        btnDelete = (Button)findViewById(R.id.btnDelete);
        btnAplicar = (Button)findViewById(R.id.btnAplicar);
        checkBoxImage = (CheckBox) findViewById(R.id.checkBox);
        text = (TextView) findViewById(R.id.textoAplicar);
        editText = (EditText) findViewById(R.id.editText);
        imgvw = (CircleImageView)findViewById(R.id.imgView);

        editText.setText(prefs.getString("check_text_profile", ""));

        if (prefs.getBoolean("check_image_profile", true)){
            checkBoxImage.setChecked(true);
        }else{
            checkBoxImage.setChecked(false);
        }
        final File file = new File(getApplicationContext().getFilesDir().getPath() + "/imagen_perfil_hctcontrol.jpg");
        if (file.exists()) {
            btnDelete.setVisibility(View.VISIBLE);
            checkBoxImage.setVisibility(View.VISIBLE);
            //btnAplicar.setVisibility(View.VISIBLE);
            text.setVisibility(View.VISIBLE);
            Bitmap bitmap = null;

            try{
                FileInputStream fileInputStream = new FileInputStream(getApplicationContext().getFilesDir().getPath()+ "/imagen_perfil_hctcontrol.jpg");
                bitmap = BitmapFactory.decodeStream(fileInputStream);
            }catch (IOException io){
                io.printStackTrace();
            }

            imgvw.setImageBitmap(bitmap);
        }else{
            btnDelete.setVisibility(View.INVISIBLE);
            checkBoxImage.setVisibility(View.INVISIBLE);
            //btnAplicar.setVisibility(View.INVISIBLE);
            text.setVisibility(View.INVISIBLE);
        }
        btnAction.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        checkBoxImage.setOnClickListener(this);
        btnAplicar.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        SharedPreferences prefs = getSharedPreferences("DatosLogin", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor;
        editor = prefs.edit();
        switch (v.getId()){
            case R.id.btnAplicar:
                String edittext = "" + editText.getText();
                Toast.makeText(this, "Nombre:  " + editText.getText(), Toast.LENGTH_LONG).show();
                editor.putString("check_text_profile", edittext);
                editor.commit();

                // Restart
                Intent reboot = getBaseContext().getPackageManager().getLaunchIntentForPackage( getBaseContext().getPackageName() );
                reboot.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(reboot);
                break;
            case R.id.btnPic:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                int code = 2;
                startActivityForResult(intent, code);
                btnAplicar.setVisibility(View.VISIBLE);
                text.setVisibility(View.VISIBLE);
                break;
            case R.id.checkBox:
                btnAplicar.setVisibility(View.VISIBLE);
                text.setVisibility(View.VISIBLE);
                if (checkBoxImage.isChecked()) {
                    editor.putBoolean("check_image_profile", true);
                    editor.commit();
                } else {
                    editor.putBoolean("check_image_profile", false);
                    editor.commit();
                }
                break;
            case R.id.btnDelete:
                File file = new File(getApplicationContext().getFilesDir().getPath() + "/imagen_perfil_hctcontrol.jpg");
                file.delete();
                imgvw.setImageResource(R.drawable.imagen_perfil);
                editor.putBoolean("check_image_profile", false);
                editor.commit();
                text.setVisibility(View.VISIBLE);
                btnAplicar.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.INVISIBLE);
                checkBoxImage.setChecked(false);
                checkBoxImage.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED){
            // No hacer nada. Esto evita un FC al pulsar atrás cuando se selecciona galería
        }else {

            Uri selectedImage = data.getData();
            InputStream is;
            try {
                is = getContentResolver().openInputStream(selectedImage);
                BufferedInputStream bis = new BufferedInputStream(is);
                Bitmap bitmap = BitmapFactory.decodeStream(bis);
                CircleImageView iv = (CircleImageView)findViewById(R.id.imgView);
                iv.setImageBitmap(bitmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                try {
                    FileOutputStream outputStream = getApplicationContext().openFileOutput("imagen_perfil_hctcontrol.jpg", Context.MODE_PRIVATE);
                    outputStream.write(byteArray);
                    outputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            } catch (FileNotFoundException e) {}
            btnDelete.setVisibility(View.VISIBLE);
            checkBoxImage.setVisibility(View.VISIBLE);
        }
    }
}