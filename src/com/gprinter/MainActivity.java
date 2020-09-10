package com.gprinter;

import java.util.Vector;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.gprinterio.GpCom.ERROR_CODE;
import com.gprinterio.GpDevice;
import com.gprinterio.PrinterRecieveListener;
import com.printer.EscCommand;
import com.printer.EscCommand.STATUS;
import com.printer.TscCommand;
import com.printer.TscCommand.BARCODETYPE;
import com.printer.TscCommand.BITMAP_MODE;
import com.printer.TscCommand.DENSITY;
import com.printer.TscCommand.DIRECTION;
import com.printer.TscCommand.FONTMUL;
import com.printer.TscCommand.FONTTYPE;
import com.printer.TscCommand.READABEL;
import com.printer.TscCommand.ROTATION;
import com.printer.TscCommand.SPEED;

/**
 * MainActivity.java
 * @author    Mr.������
 * @data      2014-11-7
 * @Company   Gprinter
 */
public class MainActivity extends Activity implements PrinterRecieveListener {
	private final static String DEBUG_TAG = "SamleApp";
	private GpDevice mDevice;
	private RadioGroup rgPort = null, rgMode = null;
	private RadioButton rbUSB = null, rbBluetooth = null, rbEhternet = null;
	private RadioButton rbEsc = null, rbTsc = null;
	boolean m_bStatusThreadStop;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mDevice = new GpDevice();
		mDevice.registerCallback(MainActivity.this);

		rgPort = (RadioGroup) findViewById(R.id.rgPort);
		rgMode = (RadioGroup) findViewById(R.id.rgPrintMode);
		rbUSB = (RadioButton) findViewById(R.id.rbUsb);
		rbBluetooth = (RadioButton) findViewById(R.id.rbBluetooth);
		rbEhternet = (RadioButton) findViewById(R.id.rbEthernet);
		rbEsc = (RadioButton) findViewById(R.id.rbEsc);
		rbTsc = (RadioButton) findViewById(R.id.rbTsc);
		ImageView iv = (ImageView) findViewById(R.id.ivPhoto);
		Bitmap b = BitmapFactory.decodeResource(getResources(),
				R.drawable.gprinter);
		iv.setImageBitmap(b);
	}

	/**
	 * ����˵�����򿪶˿�
	 * @param view
	 */
	public void openButtonClicked(View view) {
		if (rgPort.getCheckedRadioButtonId() == rbBluetooth.getId()) {
//			mDevice.openBluetoothPort(this, "98:D3:31:40:27:D5");
			mDevice.openBluetoothPort(this, "88:C2:55:C7:FB:4E");
		} else if (rgPort.getCheckedRadioButtonId() == rbUSB.getId()) {
			mDevice.openUSBPort(this);
		} else
			mDevice.openEthernetPort("192.168.123.100", 9100);
	}

	/**
	 * ����˵�����رն˿�
	 * @param view
	 */
	public void closeButtonClicked(View view) {
		mDevice.closePort();
	}

	/**
	 * ����˵������ӡͼƬ
	 * @param view
	 */
	public void sendImageButtonClicked(View view) {
		if (rgMode.getCheckedRadioButtonId() == rbTsc.getId()) {
			TscCommand tsc = new TscCommand(60, 60, 0);// ���ñ�ǩ�ߴ��ȡ��߶ȡ���϶
			tsc.addReference(0, 0); // ����ԭ������
			tsc.addSpeed(SPEED.SPEED1DIV5);// ���ô�ӡ�ٶ�
			tsc.addDensity(DENSITY.DNESITY15);// ���ô�ӡŨ��
			tsc.addDirection(DIRECTION.FORWARD);// ���ô�ӡ����
			tsc.addCls();// �����ӡ������
			Bitmap b = BitmapFactory.decodeResource(getResources(),
					R.drawable.gprinter);
			tsc.addBitmap(0, 0, BITMAP_MODE.OVERWRITE, b);
			tsc.addPrint(1, 1);

			Vector<Byte> Command = new Vector<Byte>(4096, 1024);
			Command = tsc.getCommand();
			Vector<Byte> data = new Vector<Byte>(Command.size());

			for (int k = 0; k < Command.size(); k++) {
				if (data.size() >= 1024) {
					mDevice.sendDataImmediately(data);
					data.clear();
				}
				data.add(Command.get(k));
			}
			mDevice.sendDataImmediately(data);
		} else {
			EscCommand esc = new EscCommand();
			Bitmap b = BitmapFactory.decodeResource(getResources(),
					R.drawable.gprinter);
			esc.addRastBitImage(b);
			Vector<Byte> Command = new Vector<Byte>(4096, 1024);
			Command = esc.getCommand();
			Vector<Byte> data = new Vector<Byte>(Command.size());
			for (int k = 0; k < Command.size(); k++) {
				if (data.size() >= 1024) {
					mDevice.sendDataImmediately(data);
					data.clear();
				}
				data.add(Command.get(k));
			}
			mDevice.sendDataImmediately(data);
		}
	}

	/**
	 * ����˵������ӡһά����
	 * @param view
	 */
	public void sendBarcodeButtonClicked(View view) {
		if (rgMode.getCheckedRadioButtonId() == rbTsc.getId()) {
			Log.d(DEBUG_TAG, "sendButtonPress");
			TscCommand tsc = new TscCommand(60, 60, 0);
			tsc.addReference(0, 0); // ����ԭ������
			tsc.addSpeed(SPEED.SPEED1DIV5);// ���ô�ӡ�ٶ�
			tsc.addDensity(DENSITY.DNESITY0);// ���ô�ӡŨ��
			tsc.addDirection(DIRECTION.FORWARD);// ���ô�ӡ����
			tsc.addCls();// �����ӡ������
			tsc.addText(20, 20, FONTTYPE.FONT_TAIWAN, ROTATION.ROTATION_0,
					FONTMUL.MUL_1, FONTMUL.MUL_1, "UPCA");
			tsc.add1DBarcode(20, 50, BARCODETYPE.UPCA, 40, READABEL.EANBEL,
					ROTATION.ROTATION_0, "0123456789012");
			tsc.addText(20, 120, FONTTYPE.FONT_TAIWAN, ROTATION.ROTATION_0,
					FONTMUL.MUL_1, FONTMUL.MUL_1, "UPCE");
			tsc.add1DBarcode(20, 150, BARCODETYPE.UPCE, 40, READABEL.EANBEL,
					ROTATION.ROTATION_0, "0123456789012");
			tsc.addText(20, 220, FONTTYPE.FONT_TAIWAN, ROTATION.ROTATION_0,
					FONTMUL.MUL_1, FONTMUL.MUL_1, "EAN13");
			tsc.add1DBarcode(20, 250, BARCODETYPE.EAN13, 40, READABEL.EANBEL,
					ROTATION.ROTATION_0, "012345678901212");
			tsc.addPrint(1, 1);
			Vector<Byte> Command = new Vector<Byte>(4096, 1024);
			Command = tsc.getCommand();
			mDevice.sendDataImmediately(Command);
		} else {
			EscCommand esc = new EscCommand();
//			esc.addText("UPCA :\n");
//			esc.addUPCA("123456789012");
//			esc.addText("UPCE :\n");
//			esc.addUPCE("0234567890124");
//			esc.addText("EAN13 :\n");
//			esc.addEAN13("35245545245214");
//			esc.addText("EAN8 :\n");
//			esc.addEAN8("1234567890");
//			esc.addText("CODE39 :\n");
//			esc.addCODE39("gprinter");
//			esc.addText("ITF :\n");
//			esc.addITF("123");
//			esc.addText("CODABAR :\n");
//			esc.addCODABAR("A123A");
//			esc.addText("CODE93 :\n");
//			esc.addCODE93("gprinter");
			esc.addSetBarcodeWidth((byte)2);
			esc.addText("CODE128 :\n");
//			esc.addCODE128("gprinter");
			esc.addCODE128("88C255C7FB4E");
//			esc.addEAN13("88C255C7FB4E");
			
			Vector<Byte> Command = new Vector<Byte>(4096, 1024);
			Command = esc.getCommand();
			mDevice.sendDataImmediately(Command);
		}
	}

	/**
	 * ����˵������ӡ����
	 * @param view
	 */
	public void sendTextButtonClicked(View view) {
		if (rgMode.getCheckedRadioButtonId() == rbTsc.getId()) {
			Log.d(DEBUG_TAG, "sendText");
			TscCommand tsc = new TscCommand(60, 30, 0);// ���ñ�ǩ�ߴ��ȡ��߶ȡ���϶
			tsc.addReference(0, 0); // ����ԭ������
			tsc.addSpeed(SPEED.SPEED1DIV5);// ���ô�ӡ�ٶ�
			tsc.addDensity(DENSITY.DNESITY0);// ���ô�ӡŨ��
			tsc.addDirection(DIRECTION.FORWARD);// ���ô�ӡ����
			tsc.addCls();// �����ӡ������
			tsc.addText(20, 20, FONTTYPE.FONT_TAIWAN, ROTATION.ROTATION_0,
					FONTMUL.MUL_1, FONTMUL.MUL_1, "Hello Gprinter");// ��������
			tsc.addPrint(1, 1);// �����ӡ��ǩ����
			tsc.addSound(2, 100); // ������
			Vector<Byte> Command = new Vector<Byte>(4096, 1024);
			Command = tsc.getCommand();// ��ȡ����༭�Ĵ�ӡ����
			mDevice.sendDataImmediately(Command); // ��������
		} else {
			EscCommand esc = new EscCommand();
			esc.addTurnEmphasizedModeOnOrOff(EscCommand.ENABLE.ON);// �Ӵ�ģʽ��Ч
			esc.addText("Hello Gprinter\n");// ��ӡ����
			Vector<Byte> Command = new Vector<Byte>(4096, 1024);
			Command = esc.getCommand();// ��ñ༭����������
			mDevice.sendDataImmediately(Command);// ��������
		}
	}

	/**
	 * ����˵������ѯ
	 * @param view
	 */
	public void queryButtonClicked(View view) {
		if (rgMode.getCheckedRadioButtonId() == rbTsc.getId()) {
			Log.d(DEBUG_TAG, "sendText");
			TscCommand tsc = new TscCommand();// ���ñ�ǩ�ߴ��ȡ��߶ȡ���϶
			tsc.queryPrinterType();
			Vector<Byte> Command = new Vector<Byte>(4096, 1024);
			Command = tsc.getCommand();// ��ȡ����༭�Ĵ�ӡ����
			mDevice.sendDataImmediately(Command); // ��������
		} else {
			EscCommand esc = new EscCommand();
			esc.queryRealtimeStatus(STATUS.PRINTER_ERROR);
			Vector<Byte> Command = new Vector<Byte>(4096, 1024);
			Command = esc.getCommand();// ��ñ༭����������
			mDevice.sendDataImmediately(Command);// ��������
		}
	}

	private void showReceive(final Vector<Byte> receiveBuffer) {
		try {
			this.runOnUiThread(new Runnable() {
				public void run() {
					Log.d(DEBUG_TAG, "receiveBuffer" + receiveBuffer.toString());
				}
			});
		} catch (Exception e) {
		}
	}

	@Override
	public ERROR_CODE ReceiveData(Vector<Byte> receiveBuffer) {
		// TODO Auto-generated method stub
		showReceive(receiveBuffer);
		return null;
	}

	public ERROR_CODE ReceiveData2(Vector<Byte> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
