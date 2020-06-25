package br.com.manobray.obdtelemetry;


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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends Activity {

    private BluetoothAdapter mBluetoothAdapter;

    private final int REQUEST_ENABLE_BT = 1;
    private ArrayList<String> devicesToConnect;
    private Set<BluetoothDevice> pairedDevices;
    private Button listPairedButton;
    private Button turnOnButton;
    private ArrayAdapter<String> mArrayAdapter;
    private ListView pairedDevicesListView;
    private boolean bluetoothTaLigado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Declara��es
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        listPairedButton = (Button) findViewById(R.id.pairedButton);
        turnOnButton = (Button) findViewById(R.id.buttonON);
        pairedDevicesListView = (ListView) findViewById(R.id.pairedDevicesListView);
        devicesToConnect = new ArrayList<String>();
        bluetoothTaLigado = mBluetoothAdapter.isEnabled();

        if(bluetoothTaLigado) turnOnButton.setVisibility(View.INVISIBLE);
        else turnOnButton.setVisibility(View.VISIBLE);

        turnOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bluetoothTaLigado)
                {
                    turnOnButton.setText("Turn Off BT");
                    startConnection();
                    bluetoothTaLigado = true;
                }
                else{
                    turnOnButton.setText("Turn On BT");
                    mBluetoothAdapter.disable();
                    bluetoothTaLigado = false;
                }
            }
        });
        listPairedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBluetoothAdapter.isEnabled())
                    listPairedDevices();
                else{
                    Log.d("MyTag", "Bluetooth Não Ligado");
                    Toast.makeText(getApplicationContext(), "Ligue o Bluetooth", Toast.LENGTH_SHORT).show();
                }
            }
        });
        pairedDevicesListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int row,
                                    long arg3) {
                String deviceMacClicked = pairedDevicesListView.getItemAtPosition(row).toString();
                deviceMacClicked = deviceMacClicked.substring(deviceMacClicked.length() - 17);
                Toast.makeText(getApplicationContext(), deviceMacClicked, Toast.LENGTH_SHORT).show();

                if(deviceMacClicked.compareTo("11:22:33:DD:EE:FF") == 0){
                    Intent nextScreen = new Intent(getApplicationContext(), ConnectionScreen.class);
                    nextScreen.putExtra("DeviceMAC", deviceMacClicked);
                    startActivity(nextScreen);
                }
                else Toast.makeText(getApplicationContext(), "Não é um dispositivo OBD", Toast.LENGTH_SHORT).show();
            }

        });
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
    }


    private void startConnection()
    {
        if(!mBluetoothAdapter.isEnabled()){
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }
        else Toast.makeText(getApplicationContext(), "Já conectado", Toast.LENGTH_SHORT).show();
    }

    private void listPairedDevices()
    {
        int numberOfDevicesBefore, numberOfDevicesAfter;
        if(pairedDevices != null) numberOfDevicesBefore = pairedDevices.size();
        else numberOfDevicesBefore = 0;
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        numberOfDevicesAfter = pairedDevices.size();
        if (pairedDevices.size() > 0) {
            // Loop nos dispositivos pareados
            for (BluetoothDevice device : pairedDevices) {
                // Adiciona device no array
                String stringToAdd = device.getName() + "\n" + device.getAddress();
                if(!devicesToConnect.contains(stringToAdd))
                    devicesToConnect.add(device.getName() + "\n" + device.getAddress());
            }
            if(numberOfDevicesBefore != numberOfDevicesAfter) {
                // Se houve mudan�a nos dispostivos existentes no array, adiciona na lista
                mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, devicesToConnect);
                pairedDevicesListView.setAdapter(mArrayAdapter);
            }
        }
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        turnOnButton.setVisibility(View.VISIBLE);
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        turnOnButton.setVisibility(View.VISIBLE);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        turnOnButton.setVisibility(View.INVISIBLE);
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        turnOnButton.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        /* ... */

        // Unregister broadcast listeners
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
