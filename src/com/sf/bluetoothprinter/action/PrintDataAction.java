package com.sf.bluetoothprinter.action;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.sf.bluetoothprinter.service.PrintDataService;
import com.sf.bluetoothprinter.view.R;

public class PrintDataAction implements OnClickListener {
	private Context context = null;
	private TextView deviceName = null;
	private TextView connectState = null;
	private EditText printData = null;
	private String deviceAddress = null;
	private PrintDataService printDataService = null;
	private String fileName = "ic_launcher-web.png";

	public PrintDataAction(Context context, String deviceAddress,
			TextView deviceName, TextView connectState) {
		super();
		this.context = context;
		this.deviceAddress = deviceAddress;
		this.deviceName = deviceName;
		this.connectState = connectState;
		this.printDataService = new PrintDataService(this.context,
				this.deviceAddress);
		this.initView();

	}

	private void initView() {
		// ���õ�ǰ�豸����
		this.deviceName.setText(this.printDataService.getDeviceName());
		// һ�����������������豸
		boolean flag = this.printDataService.connect();
		if (flag == false) {
			// ����ʧ��
			this.connectState.setText("����ʧ�ܣ�");
		} else {
			// ���ӳɹ�
			this.connectState.setText("���ӳɹ���");

		}
	}

	public void setPrintData(EditText printData) {
		this.printData = printData;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.send) {
			String sendData = this.printData.getText().toString();
			this.printDataService.send(sendData + "\n");

		} else if (v.getId() == R.id.command) {
			this.printDataService.selectCommand();

		}
	}

}