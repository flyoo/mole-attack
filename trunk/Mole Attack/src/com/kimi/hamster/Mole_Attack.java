package com.kimi.hamster;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Mole_Attack extends Activity {
	
	private Button btnStart,btnPauseResume,btnEnd,btnreset;
	
	private static final int ABOUT = 1;
	//private static final int CONNECT = 0;
	private int status = 0;
	
	//消息类型
	public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    
    
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    
    private BluetoothAdapter mBluetoothAdapter = null;

    private BluetoothService mbtService = null;

    private SensorManager sensormgr;
    private Sensor sensor;
    private float x,y,z,last_x=0,last_y=0,last_z=0;
    private long last_time,time;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        btnStart = (Button)findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(status==1)
					return;
				sendMessage(new byte[]{3});
				sensormgr = (SensorManager)getSystemService(SENSOR_SERVICE);
				sensor = sensormgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
				sensormgr.registerListener(slis, sensor,SensorManager.SENSOR_DELAY_GAME);
				status=1;
			}
		});
        
        btnPauseResume = (Button)findViewById(R.id.btn_pause_resume);
        btnPauseResume.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(status==0)
					return;
				if(v.getTag().toString().equals(getResources().getString(R.string.pause)))
				{
					sendMessage(new byte[]{4});
					btnPauseResume.setText(R.string.resume);
					btnPauseResume.setTag(getResources().getString(R.string.resume));
				}
				else if(v.getTag().toString().equals(getResources().getString(R.string.resume)))
				{
					sendMessage(new byte[]{5});
					btnPauseResume.setText(R.string.pause);
					btnPauseResume.setTag(getResources().getString(R.string.pause));
				}
			}
		});
        
        btnEnd = (Button)findViewById(R.id.btn_end);
        btnEnd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sensormgr.unregisterListener(slis);
				status = 0;
				sendMessage(new byte[]{6});
			}
		});
        
        btnreset = (Button)findViewById(R.id.btn_reset);
        btnreset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendMessage(new byte[]{2});
			}
		});
    }
    
    private SensorEventListener slis = new SensorEventListener() {
		
		public void onSensorChanged(SensorEvent event) {
			//float last_x=0;
			x = event.values[SensorManager.DATA_X];
			y = event.values[SensorManager.DATA_Y];
			z = event.values[SensorManager.DATA_Z];
			time = event.timestamp/1000000;

			if(last_x==0&&last_y==0&&last_z==0)
			{
				last_x=x;
				last_y=y;
				last_z=z;
				last_time = time;
				//return;
			}
			
			if(x>=5&&last_x<=-15)
			{
				sendMessage(new byte[]{1});
				Toast.makeText(getApplicationContext(), String.valueOf(x)+String.valueOf(last_x), Toast.LENGTH_SHORT).show();
				last_x=x;
				return;
			}
			
			if(Math.abs(x)>5)
			{
				last_x=x;
			}
			
			if(Math.abs(last_y-y)>30||Math.abs(last_z-z)>30)
			{
				/*Log.d("test2","time:"+time);
				Log.d("test2","time-last_time:"+(time - last_time));
				Log.d("test2","x:"+x);
				Log.d("test2","last_x:"+last_x);
				Log.d("test2","y:"+y);
				Log.d("test2","last_y:"+last_y);
				Log.d("test2","z"+z);
				Log.d("test2","last_z:"+last_z);*/
				byte[] msg = new byte[5];
				msg[0] = 0;
				msg[1] = (byte)(-z);
				msg[2] = (byte)y;
				//if(Math.abs(last_time-time)>128)
				//	msg[3] = 0;
				//else
				msg[3] = (byte)((time-last_time)>>8);
				msg[4] = (byte)((time-last_time)&0xff);
				last_z = z;
				last_y = y;
				last_time = time;
				sendMessage(msg);
				//sendMessage(new byte[]{0,0,0,(byte)(211>>8),(byte)(211&0xff)});
			}
			
		}
		
		public void onAccuracyChanged(Sensor sensor, int accuracy) {			
		}
	};
    
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
            
            if (resultCode == Activity.RESULT_OK) {
                
                String address = data.getExtras().getString("device_address");
                
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                
                mbtService.connect(device);
            }
            break;
        case REQUEST_ENABLE_BT:
            
            if (resultCode != Activity.RESULT_OK) {
            	Toast.makeText(this, R.string.disable, Toast.LENGTH_SHORT).show();
                finish();
            } 
        }
    }
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		sensormgr.unregisterListener(slis);
		if (mbtService != null) mbtService.stop();
	}


	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } else {
            if (mbtService == null) {
            	mbtService = new BluetoothService(this, mhandler);
            }
        }
	}

	/**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessage(byte[] message) {
        
        if (mbtService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        
        if (message.length > 0) {
            //byte[] send = message.getBytes();
            mbtService.write(message);
        }
    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 0, getResources().getString(R.string.connect)).setIcon(android.R.drawable.ic_menu_search);
		menu.add(0, 1, 1, getResources().getString(R.string.about)).setIcon(R.drawable.about);
		menu.add(0, 2, 2, getResources().getString(R.string.exit)).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		super.onOptionsItemSelected(item);
		switch(item.getItemId())
		{
		case 0:
			Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            return true;
		case 1:
			showDialog(ABOUT);
			break;
		case 2:
			this.finish();
			break;
		}
		return true;
	}


	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		super.onCreateDialog(id);
		switch(id)
		{
		case ABOUT:
			return showAbout();
		default:
			return null;
		}
		
	}
    
	/**
	 * show关于对话框
	 * @return
	 */
    private Dialog showAbout()
    {
    	return new AlertDialog.Builder(this).setPositiveButton(getApplicationContext().getString(android.R.string.ok),
				new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which){}
				}).setTitle(getApplicationContext().getString(R.string.about))
					.setMessage(getApplicationContext().getString(R.string.poweredby)).create();
    }
    
    private final Handler mhandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			
		}
    	
    };
    
}