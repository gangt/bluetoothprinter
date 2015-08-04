package com.sf.bluetoothprinter.service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.sf.bluetoothprinter.view.R;

public class BluetoothService {
	private Context context = null;
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter
			.getDefaultAdapter();
	private ArrayList<BluetoothDevice> unbondDevices = null; // ���ڴ��δ��������豸
	private ArrayList<BluetoothDevice> bondDevices = null;// ���ڴ������������豸
	private Button switchBT = null;
	private Button searchDevices = null;
	private ListView unbondDevicesListView = null;
	private ListView bondDevicesListView = null;

	/**
	 * ����Ѱ������豸��ListView,��������һ��������Ϣ��ӡ�б����
	 */
	private void addBondDevicesToListView() {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		int count = this.bondDevices.size();
		System.out.println("�Ѱ��豸������" + count);
		for (int i = 0; i < count; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("deviceName", this.bondDevices.get(i).getName());
			data.add(map);// ��item������ݼӵ�data��
		}
		String[] from = { "deviceName" };
		int[] to = { R.id.device_name };
		SimpleAdapter simpleAdapter = new SimpleAdapter(this.context, data,
				R.layout.bonddevice_item, from, to);
		// ��������װ�ص�listView��
		this.bondDevicesListView.setAdapter(simpleAdapter);

		this.bondDevicesListView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						BluetoothDevice device = bondDevices.get(arg2);
						Intent intent = new Intent();
						intent.setClassName(context,
								"com.sf.bluetoothprinter.view.PrintDataActivity");
						intent.putExtra("deviceAddress", device.getAddress());
						context.startActivity(intent);
					}
				});

	}

	/**
	 * ���δ�������豸��ListView
	 */
	private void addUnbondDevicesToListView() {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		int count = this.unbondDevices.size();
	//	System.out.println("δ���豸������" + count);
		for (int i = 0; i < count; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("deviceName", this.unbondDevices.get(i).getName());
			data.add(map);// ��item������ݼӵ�data��
		}
		String[] from = { "deviceName" };
		int[] to = { R.id.undevice_name };
		SimpleAdapter simpleAdapter = new SimpleAdapter(this.context, data,
				R.layout.unbonddevice_item, from, to);

		// ��������װ�ص�listView��
		this.unbondDevicesListView.setAdapter(simpleAdapter);

		// Ϊÿ��item�󶨼����������豸������
		this.unbondDevicesListView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						try {
							Method createBondMethod = BluetoothDevice.class
									.getMethod("createBond");
							createBondMethod.invoke(unbondDevices.get(arg2));
							// ���󶨺õ��豸��ӵ��Ѱ�list����
							bondDevices.add(unbondDevices.get(arg2));
							// ���󶨺õ��豸��δ��list�������Ƴ�
							unbondDevices.remove(arg2);
							addBondDevicesToListView();
							addUnbondDevicesToListView();
						} catch (Exception e) {
							Toast.makeText(context, "���ʧ�ܣ�", Toast.LENGTH_SHORT)
									.show();
						}

					}
				});
	}

	public BluetoothService(Context context, ListView unbondDevicesListView,
			ListView bondDevicesListView, Button switchBT, Button searchDevices) {
		this.context = context;
		this.unbondDevicesListView = unbondDevicesListView;
		this.bondDevicesListView = bondDevicesListView;
		// this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		this.unbondDevices = new ArrayList<BluetoothDevice>();
		this.bondDevices = new ArrayList<BluetoothDevice>();
		this.switchBT = switchBT;
		this.searchDevices = searchDevices;
		this.initIntentFilter();

	}

	public void initIntentFilter() {
		// ���ù㲥��Ϣ����
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
		intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		// ע��㲥�����������ղ������������
		context.registerReceiver(receiver, intentFilter);

	}

	/***
	 * ע���㲥
	 */
	public  void unregisterReceiver(){
		context.unregisterReceiver(receiver);
	}
	
	/**
	 * ������
	 */
	public void openBluetooth(Activity activity) {
		Intent enableBtIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_ENABLE);
		activity.startActivityForResult(enableBtIntent, 1);

	}

	/**
	 * �ر�����
	 */
	public void closeBluetooth() {
		this.bluetoothAdapter.disable();
	}

	/**
	 * �ж������Ƿ��
	 * 
	 * @return boolean
	 */
	public boolean isOpen() {
		return this.bluetoothAdapter.isEnabled();

	}

	/**
	 * ���������豸
	 */
	public void searchDevices() {
		this.bondDevices.clear();
		this.unbondDevices.clear();

		// Ѱ�������豸��android�Ὣ���ҵ����豸�Թ㲥��ʽ����ȥ
		this.bluetoothAdapter.startDiscovery();
	}

	/**
	 * ���δ�������豸��list����
	 * 
	 * @param device
	 */
	public void addUnbondDevices(BluetoothDevice device) {
		System.out.println("δ���豸���ƣ�" + device.getName());
		if (!this.unbondDevices.contains(device)) {
			this.unbondDevices.add(device);
		}
	}

	/**
	 * ����Ѱ������豸��list����
	 * 
	 * @param device
	 */
	public void addBandDevices(BluetoothDevice device) {
		System.out.println("�Ѱ��豸���ƣ�" + device.getName());
		if (!this.bondDevices.contains(device)) {
			this.bondDevices.add(device);
		}
	}
	

	/**
	 * �����㲥������
	 */
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		ProgressDialog progressDialog = null;

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
					addBandDevices(device);
				} else {
					addUnbondDevices(device);
				}
			} else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				progressDialog = ProgressDialog.show(context, "���Ե�...",
						"���������豸��...", true);

			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
			//	System.out.println("�豸�������");
				progressDialog.dismiss();

				addUnbondDevicesToListView();
				addBondDevicesToListView();
				// bluetoothAdapter.cancelDiscovery();
			}
			if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
				//	System.out.println("--------������-----------");
					switchBT.setText("�ر�����");
					searchDevices.setEnabled(true);
					bondDevicesListView.setEnabled(true);
					unbondDevicesListView.setEnabled(true);
				} else if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) {
				//	System.out.println("--------�ر�����-----------");
					switchBT.setText("������");
					searchDevices.setEnabled(false);
					bondDevicesListView.setEnabled(false);
					unbondDevicesListView.setEnabled(false);
				}
			}

		}

	};

}