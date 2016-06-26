package com.hctrom.romcontrol.build.prop.editor;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.hctrom.romcontrol.R;
import com.hctrom.romcontrol.ThemeSelectorUtility;
import com.hctrom.romcontrol.prefs.Shell;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class BuildPropEditor extends ListActivity {
    private ListView listView;
    private String tempFile;
    private boolean refreshList;
	private Toolbar toolbar;
    private static FragmentActivity myContext;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
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
        delegate.setContentView(R.layout.build_prop_main);

        toolbar= (Toolbar) findViewById(R.id.toolbar);
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

        listView = getListView();
        listView.setTextFilterEnabled(true);

        createTempFile();
        populateList();
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
    	// TODO: This isn't working.
    	if (refreshList) {
    		// Something was added, better refresh
    		populateList();
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.build_prop_main, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
            case R.id.action_reboot:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

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
                                Shell.getRebootAction("reboot");
                            }
                        })
                        .setNegativeButton("TODAVÍA NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();

                            }
                        });
                AlertDialog dialog = builder.create();
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
    		case R.id.add_menu:
    			refreshList = true;
    			showEdit(null, null);
    			break;
    		case R.id.backup_menu:
    			backup();
    			break;
    		case R.id.restore_menu:
    			restore();
    			break;
    	}
    	return true;
    }
    
    /*@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.build_prop_context, menu);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.context_edit:
				// do one
				return true;
			case R.id.context_delete:
				// do remove
				return true;
			default:
				return false;
		}
    }*/
    
    public void populateList() {
    	final Properties prop = new Properties();
        File file = new File(tempFile);
    	try {
    		prop.load(new FileInputStream(file));
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
		}
    	
    	final String[] pTitle = prop.keySet().toArray(new String[0]);
    	final List<String> pDesc = new ArrayList<String>();
    	for (int i = 0; i < pTitle.length; i++) {
    		pDesc.add(prop.getProperty(pTitle[i]));
    	}
    	
    	ArrayList<Map<String, String>> list = buildData(pTitle, pDesc);
		String[] from = { "title", "description" };
		int[] to = { R.id.prop_title, R.id.prop_desc };

		SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.build_prop_list_item, from, to);
		setListAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				showEdit(pTitle[position], prop.getProperty(pTitle[position]));
			}
        });
    }
    
    private ArrayList<Map<String, String>> buildData(String[] t, List<String> d) {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		for (int i = 0; i < t.length; ++i) {
			list.add(putData(t[i], d.get(i)));
		}

		return list;
	}
    
    private HashMap<String, String> putData(String title, String description) {
		HashMap<String, String> item = new HashMap<String, String>();

		item.put("title", title);
		item.put("description", description);

		return item;
	}
    
    public void showEdit(String name, String key) {
    	Intent intent = new Intent(BuildPropEditor.this, EditPropActivity.class);
		
    	intent.putExtra("name", name);
    	intent.putExtra("key", key);
    	
    	startActivity(intent);
        finish();
    }
    
    private void backup() {
    	Process process = null;
        DataOutputStream os = null;
        
        try {
            process = Runtime.getRuntime().exec("su");
	        os = new DataOutputStream(process.getOutputStream());
	        os.writeBytes("mount -o remount,rw -t yaffs2 /dev/block/mtdblock4 /system\n");
	        os.writeBytes("cp -f /system/build.prop " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/HCTControl/build.prop.bak\n");
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
        
    	Toast.makeText(getApplicationContext(), "Se ha creado un Backup del build.prop en " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/HCTControl/build.prop.bak", Toast.LENGTH_SHORT).show();
    }
    
    private void restore() {
    	Process process = null;
        DataOutputStream os = null;
        
        try {
            process = Runtime.getRuntime().exec("su");
	        os = new DataOutputStream(process.getOutputStream());
	        os.writeBytes("mount -o remount,rw -t yaffs2 /dev/block/mtdblock4 /system\n");
	        os.writeBytes("mv -f /system/build.prop /system/build.prop.bak\n");
	        os.writeBytes("busybox cp -f " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/HCTControl/build.prop.bak /system/build.prop\n");
	        os.writeBytes("chmod 644 /system/build.prop\n");
	        //os.writeBytes("mount -o remount,ro -t yaffs2 /dev/block/mtdblock4 /system\n");
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
        
    	Toast.makeText(getApplicationContext(), "Se ha Restaurado el build.prop desde " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/build.prop.bak", Toast.LENGTH_SHORT).show();
    }
    
    private void createTempFile() {
    	Process process = null;
        DataOutputStream os = null;
        
        try {
            process = Runtime.getRuntime().exec("su");
	        os = new DataOutputStream(process.getOutputStream());
	        os.writeBytes("mount -o remount,rw -t yaffs2 /dev/block/mtdblock4 /system\n");
	        os.writeBytes("cp -f /system/build.prop " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/HCTControl/buildprop.tmp\n");
	        os.writeBytes("chmod 777 " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/HCTControl/buildprop.tmp\n");
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
        
        tempFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HCTControl/buildprop.tmp";
    }
    
    public boolean runRootCommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        
        try {
            process = Runtime.getRuntime().exec("su");
	        os = new DataOutputStream(process.getOutputStream());
	        os.writeBytes(command+"\n");
	        os.writeBytes("exit\n");
	        os.flush();
	        process.waitFor();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
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
        return true;
    }
}
