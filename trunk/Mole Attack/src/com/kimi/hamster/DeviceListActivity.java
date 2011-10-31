package com.kimi.hamster;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class DeviceListActivity extends Activity{
	
	private TextView tvpaired,tvnew;
	private ListView lvpaired,lvnew;
	private Button btnscan;
	private BluetoothAdapter mbtadapter;
	private ArrayList<ListItemView.Data> paireddevices = new ArrayList<ListItemView.Data>();
	private ArrayList<ListItemView.Data> newdevices = new ArrayList<ListItemView.Data>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.device_list);
		
		tvpaired = (TextView)findViewById(R.id.title_paired_devices);
		tvnew = (TextView)findViewById(R.id.title_new_devices);
		
		lvpaired = (ListView)findViewById(R.id.paired_devices);
		lvnew = (ListView)findViewById(R.id.new_devices);
		
		btnscan = (Button)findViewById(R.id.btn_scan);
		btnscan.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				doDiscovery();
                v.setVisibility(View.GONE);
			}
		});
		
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
        
		mbtadapter = BluetoothAdapter.getDefaultAdapter();
		
		Set<BluetoothDevice> devices = mbtadapter.getBondedDevices();
		if(devices.size()>0)
		{
			ListAdapter listadapter = new ListAdapter(this);
			loadpaired(devices);
			listadapter.setDatas(paireddevices);
			lvpaired.setAdapter(listadapter);
			lvpaired.setOnItemClickListener(mDeviceClickListener);
			tvpaired.setVisibility(View.VISIBLE);
		}
		else
		{
			tvpaired.setVisibility(View.GONE);
		}
	}
	
	private void loadpaired(Set<BluetoothDevice> devices)
	{
		paireddevices.clear();
		for(BluetoothDevice device : devices)
		{
			ListItemView.Data data = new ListItemView.Data();
			//if(device.getBluetoothClass().getMajorDeviceClass()==BluetoothClass.Device.Major.COMPUTER)
				//data.icon=getResources().getDrawable(id);
			data.nameText = device.getName();
			data.addressText = device.getAddress();
			paireddevices.add(data);
		}
	}
	
	@Override
    protected void onDestroy() {
        super.onDestroy();

        if (mbtadapter != null) {
            mbtadapter.cancelDiscovery();
        }

        this.unregisterReceiver(mReceiver);
    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
       
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

        tvnew.setVisibility(View.VISIBLE);

        if (mbtadapter.isDiscovering()) {
            mbtadapter.cancelDiscovery();
        }

        mbtadapter.startDiscovery();
    }
	
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            ListAdapter listadapter = new ListAdapter(getApplicationContext());
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                	ListItemView.Data data = new ListItemView.Data();
                	//data.icon = 
                	data.nameText = device.getName();
                	data.addressText = device.getAddress();
                	//if(device.getBluetoothClass().getMajorDeviceClass()==BluetoothClass.Device.Major.COMPUTER)
    				//data.icon=getResources().getDrawable(id);
                    newdevices.add(data);
                    listadapter.setDatas(newdevices);
                    lvnew.setAdapter(listadapter);
                    lvnew.setOnItemClickListener(mDeviceClickListener);
                }
            // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.devicelist_title);
            }
        }
    };
    
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            mbtadapter.cancelDiscovery();
            
            ListView view = (ListView)av;
            ListItemView.Data d = (ListItemView.Data)view.getItemAtPosition(arg2);
            String address = d.addressText;

            // Create the result Intent and include the MAC address
            Intent intent = new Intent();
            intent.putExtra("device_address", address);

            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };
    
}
