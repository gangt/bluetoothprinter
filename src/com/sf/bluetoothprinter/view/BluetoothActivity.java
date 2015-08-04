package com.sf.bluetoothprinter.view;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.ListView;

import com.sf.bluetoothprinter.action.BluetoothAction;
import com.sf.bluetoothprinter.view.R;

public class BluetoothActivity extends Activity {

	private Context context = null;
	private BluetoothAction bluetoothAction;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = this;
		setTitle("À¶ÑÀ´òÓ¡");
		setContentView(R.layout.bluetooth_layout);
		this.initListener();
	}

	private void initListener() {
		ListView unbondDevices = (ListView) this
				.findViewById(R.id.unbondDevices);
		ListView bondDevices = (ListView) this.findViewById(R.id.bondDevices);
		Button switchBT = (Button) this.findViewById(R.id.openBluetooth_tb);
		Button searchDevices = (Button) this.findViewById(R.id.searchDevices);

		bluetoothAction = new BluetoothAction(this.context, unbondDevices,
				bondDevices, switchBT, searchDevices, BluetoothActivity.this);

		Button returnButton = (Button) this
				.findViewById(R.id.return_Bluetooth_btn);
		bluetoothAction.setSearchDevices(searchDevices);
		bluetoothAction.initView();

		switchBT.setOnClickListener(bluetoothAction);
		searchDevices.setOnClickListener(bluetoothAction);
		returnButton.setOnClickListener(bluetoothAction);
	}

	/***
	 * ÍË³öÊ±×¢Ïú¼àÌý
	 */
	@Override
	public void onBackPressed() {
		if (bluetoothAction != null) {
			bluetoothAction.getBluetoothInstance().unregisterReceiver();
		}
		super.onBackPressed();
	}

}
