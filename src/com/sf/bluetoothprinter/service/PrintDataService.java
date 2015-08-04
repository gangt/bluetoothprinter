package com.sf.bluetoothprinter.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter.LengthFilter;
import android.widget.Toast;

public class PrintDataService {
	private Context context = null;
	private String deviceAddress = null;
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter
			.getDefaultAdapter();
	private BluetoothDevice device = null;
	private static BluetoothSocket bluetoothSocket = null;
	private static OutputStream outputStream = null;
	private static final UUID uuid = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private boolean isConnection = false;
	final String[] items = { "��λ��ӡ��", "��׼ASCII����", "ѹ��ASCII����", "���岻�Ŵ�",
			"��߼ӱ�", "ȡ���Ӵ�ģʽ", "ѡ��Ӵ�ģʽ", "ȡ�����ô�ӡ", "ѡ���ô�ӡ", "ȡ���ڰ׷���", "ѡ��ڰ׷���",
			"ȡ��˳ʱ����ת90��", "ѡ��˳ʱ����ת90��" };
	final byte[][] byteCommands = { { 0x1b, 0x40 },// ��λ��ӡ��
			{ 0x1b, 0x4d, 0x00 },// ��׼ASCII����
			{ 0x1b, 0x4d, 0x01 },// ѹ��ASCII����
			{ 0x1d, 0x21, 0x00 },// ���岻�Ŵ�
			{ 0x1d, 0x21, 0x11 },// ��߼ӱ�
			{ 0x1b, 0x45, 0x00 },// ȡ���Ӵ�ģʽ
			{ 0x1b, 0x45, 0x01 },// ѡ��Ӵ�ģʽ
			{ 0x1b, 0x7b, 0x00 },// ȡ�����ô�ӡ
			{ 0x1b, 0x7b, 0x01 },// ѡ���ô�ӡ
			{ 0x1d, 0x42, 0x00 },// ȡ���ڰ׷���
			{ 0x1d, 0x42, 0x01 },// ѡ��ڰ׷���
			{ 0x1b, 0x56, 0x00 },// ȡ��˳ʱ����ת90��
			{ 0x1b, 0x56, 0x01 },// ѡ��˳ʱ����ת90��
	};

	public PrintDataService(Context context, String deviceAddress) {
		super();
		this.context = context;
		this.deviceAddress = deviceAddress;
		this.device = this.bluetoothAdapter.getRemoteDevice(this.deviceAddress);
	}

	/**
	 * ��ȡ�豸����
	 * 
	 * @return String
	 */
	public String getDeviceName() {
		return this.device.getName();
	}

	/**
	 * ���������豸
	 */
	public boolean connect() {
		if (!this.isConnection) {
			try {
				bluetoothSocket = this.device
						.createRfcommSocketToServiceRecord(uuid);
				bluetoothSocket.connect();
				outputStream = bluetoothSocket.getOutputStream();
				this.isConnection = true;
				if (this.bluetoothAdapter.isDiscovering()) {
					// System.out.println("�ر���������");
					this.bluetoothAdapter.isDiscovering();
				}
			} catch (Exception e) {
				Toast.makeText(this.context, "����ʧ�ܣ�", 1).show();
				return false;
			}
			Toast.makeText(this.context, this.device.getName() + "���ӳɹ���",
					Toast.LENGTH_SHORT).show();
			return true;
		} else {
			return true;
		}
	}

	/**
	 * �Ͽ������豸����
	 */
	public static void disconnect() {
		// System.out.println("�Ͽ������豸����");
		try {
			bluetoothSocket.close();
			if (outputStream != null) {
				outputStream.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * ѡ��ָ��
	 */
	public void selectCommand() {
		new AlertDialog.Builder(context).setTitle("��ѡ��ָ��")
				.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (isConnection) {
							try {
								outputStream.write(byteCommands[which]);

							} catch (IOException e) {
								Toast.makeText(context, "����ָ��ʧ�ܣ�",
										Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(context, "�豸δ���ӣ����������ӣ�",
									Toast.LENGTH_SHORT).show();
						}
					}
				}).create().show();
	}

	/**
	 * ��������
	 */
	public void send(String sendData) {
		if (this.isConnection) {
			System.out.println("��ʼ��ӡ����");
			try {
				byte[] data = sendData.getBytes("gbk");
				outputStream.write(data, 0, data.length);
				outputStream.flush();
			} catch (IOException e) {
				Toast.makeText(this.context, "����ʧ�ܣ�", Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			Toast.makeText(this.context, "�豸δ���ӣ����������ӣ�", Toast.LENGTH_SHORT)
					.show();

		}
	}

}