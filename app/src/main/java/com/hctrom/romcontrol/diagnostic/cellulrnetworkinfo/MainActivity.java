package com.hctrom.romcontrol.diagnostic.cellulrnetworkinfo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.hctrom.romcontrol.R;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.network_info);

		TextView tvNetworkInfo = (TextView) findViewById(R.id.tvNetworkInfo);

		String type = Connectivity.isConnectedFast(MainActivity.this);
		
		System.out.println("Network :: " + type);
		
		tvNetworkInfo.setText(type);
		
	}
}
