package com.hctrom.romcontrol.build.prop.editor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hctrom.romcontrol.R;
import com.hctrom.romcontrol.ThemeSelectorUtility;
import com.hctrom.romcontrol.prefs.Shell;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

public class EditPropActivity extends Activity {

	private EditText editName;
	private EditText editKey;
	protected boolean changesPending;
	private AlertDialog unsavedChangesDialog;
	private String tempFile;
	private String propReplaceFile;
	private static FragmentActivity myContext;

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
		AppCompatCallback callback = new AppCompatCallback() {
			@Override
			public void onSupportActionModeStarted(ActionMode actionMode) {
			}

			@Override
			public void onSupportActionModeFinished(ActionMode actionMode) {
			}
		};
		AppCompatDelegate delegate = AppCompatDelegate.create(this, callback);

		delegate.onCreate(savedInstanceState);
		delegate.setContentView(R.layout.buil_prop_edit);

		Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
		delegate.setSupportActionBar(toolbar);
		if (delegate.getSupportActionBar() != null) {
			delegate.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			delegate.getSupportActionBar().setDisplayShowHomeEnabled(true);
			delegate.getSupportActionBar().setTitle("Buil.prop Edit");
		}

		toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		tempFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HCTControl/buildprop.tmp";
		propReplaceFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HCTControl/propreplace.txt";
		setUpControls();
	}

	private void setUpControls() {
		editName = (EditText)findViewById(R.id.prop_name);
    	editKey = (EditText)findViewById(R.id.prop_key);
    	
    	String name = getIntent().getExtras().getString("name");
    	String key = getIntent().getExtras().getString("key");
    	
    	if (name != null) {
			editName.setText(name);
	    	editKey.setText(key);
    	}
    	
    	editName.addTextChangedListener(new TextWatcher() {
    		@Override
    		public void onTextChanged(CharSequence s, int start, int before, int count) {
				changesPending = true;
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO: Nothing
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO: Nothing
			}
		});
    	
    	editKey.addTextChangedListener(new TextWatcher() {
    		@Override
    		public void onTextChanged(CharSequence s, int start, int before, int count) {
				changesPending = true;
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO: Nothing
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO: Nothing
			}
		});
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.build_prop_edit, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    		case R.id.save_menu:
    			final Properties prop = new Properties();
				
				try {
					FileInputStream in = new FileInputStream(new File(tempFile));
					prop.load(in);
					in.close();
				} catch (IOException e) {
					Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
				}
				
				prop.setProperty(editName.getText().toString(), editKey.getText().toString());
				
		    	try {
		    		FileOutputStream out = new FileOutputStream(new File(tempFile));
		    		prop.store(out, null);
		    		out.close();
		    		
		    		replaceInFile(new File(tempFile));
		    		transferFileToSystem();
				} catch (IOException e) {
					Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
				}

				android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);

				builder.setMessage("Es necesario reiniciar el teléfono para que los cambios sean aplicados.\n\nDesea reiniciar ahora?")
						.setTitle("REINICIAR SISTEMA")
						.setOnKeyListener(new Dialog.OnKeyListener() {
							@Override
							public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
								// TODO Auto-generated method stub
								if (keyCode == KeyEvent.KEYCODE_BACK) {
									// No hacer nada al pulsar el Botón Atrás
								}
								return true;
							}
						})
						.setPositiveButton("SI", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
								finish();
								Shell.getRebootAction("reboot");
							}
						})
						.setNegativeButton("TODAVÍA NO", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
								finish();
								startActivity(new Intent(getApplicationContext(), BuildPropEditor.class));
							}
						});
				android.support.v7.app.AlertDialog dialog = builder.create();
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();

				Button positive_button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
				Button negative_button = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
				if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("theme_prefs", 0) == 3) {
					dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg_samsung_light);
					positive_button.setTextColor(getResources().getColor(R.color.color_iconos_samsung_light));
					negative_button.setTextColor(getResources().getColor(R.color.color_iconos_samsung_light));
				}else if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("theme_prefs", 0) == 0){
					dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg_hct);
					positive_button.setTextColor(getResources().getColor(R.color.myAccentColorHCT));
					negative_button.setTextColor(getResources().getColor(R.color.myAccentColorHCT));
				}else {
					dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg_dark_light);
					positive_button.setTextColor(getResources().getColor(R.color.myAccentColor));
					negative_button.setTextColor(getResources().getColor(R.color.myAccentColor));
				}
    			break;
    		case R.id.discart_menu:
    			cancel();
    			break;
    	}

    	return true;
    }
	
	protected void cancel() {
		if (changesPending) {
			unsavedChangesDialog = new AlertDialog.Builder(this)
			.setTitle(R.string.unsaved_changes_title)
			.setMessage(R.string.unsaved_changes_message)
			.setPositiveButton(R.string.save, new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					final Properties prop = new Properties();
					
					try {
						FileInputStream in = new FileInputStream(new File(tempFile));
						prop.load(in);
						in.close();
					} catch (IOException e) {
						Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
					}
					
					prop.setProperty(editName.getText().toString(), editKey.getText().toString());
					
			    	try {
			    		FileOutputStream out = new FileOutputStream(new File(tempFile));
			    		prop.store(out, null);
			    		out.close();
			    		
			    		replaceInFile(new File(tempFile));
			    		transferFileToSystem();
					} catch (IOException e) {
						Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
					}
				}
			})
			.setNeutralButton(R.string.discart, new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			})
			.setNegativeButton(R.string.cancel, new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					unsavedChangesDialog.cancel();
				}
			}).create();
			unsavedChangesDialog.show();
		} else {
			finish();
		}
	}
	
	private void transferFileToSystem() {
    	Process process = null;
        DataOutputStream os = null;
        
        try {
            process = Runtime.getRuntime().exec("su");
	        os = new DataOutputStream(process.getOutputStream());
	        os.writeBytes("mount -o remount,rw -t yaffs2 /dev/block/mtdblock4 /system\n");
	        os.writeBytes("mv -f /system/build.prop /system/build.prop.bak\n");
	        os.writeBytes("busybox cp -f " + propReplaceFile + " /system/build.prop\n");
	        os.writeBytes("chmod 644 /system/build.prop\n");
	        //os.writeBytes("mount -o remount,ro -t yaffs2 /dev/block/mtdblock4 /system\n");
	        //os.writeBytes("rm " + propReplaceFile);
	        //os.writeBytes("rm " + tempFile);
	        os.writeBytes("exit\n");
	        os.flush();
	        process.waitFor();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            	Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        
    	Toast.makeText(getApplicationContext(), "Cambio guardado y backup creado en " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/HCTControl/build.prop.bak", Toast.LENGTH_SHORT).show();
    }
	
	private void replaceInFile(File file) throws IOException {
		File tmpFile = new File(propReplaceFile);
		FileWriter fw = new FileWriter(tmpFile);
		Reader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		
		while (br.ready()) {
			fw.write(br.readLine().replaceAll("\\\\", "") + "\n");
		}
		
		fw.close();
		br.close();
		fr.close();
	}
}
