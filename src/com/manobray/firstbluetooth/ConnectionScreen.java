package com.manobray.firstbluetooth;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;




public class ConnectionScreen extends Activity {

	private TextView macAdressTextView;
	private TextView estadoTV;
	private TextView errosTV;
	private String deviceMacAdress;
	private String exceptionStringToDisplay;
	private UUID appUUID;
	private BluetoothAdapter btAdapter;
	private BluetoothDevice obdReader;
	private BluetoothSocket blueSocket;
	private BluetoothSocket blueSocketFallback;
	private Button connectButton;
	private Button startButton;
	private Button consoleButton;
	private int bluetoothConnectionState;   
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connection_screen);
		
		Intent i = getIntent();
		deviceMacAdress = i.getStringExtra("DeviceMAC");
		macAdressTextView = (TextView) findViewById(R.id.macAdressTextView);
		estadoTV = (TextView) findViewById(R.id.stateTextView);
		errosTV = (TextView) findViewById(R.id.ErrosTextView);
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		obdReader = btAdapter.getRemoteDevice("11:22:33:DD:EE:FF");
		appUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
		connectButton = (Button)findViewById(R.id.buttonConnectDevice);
		startButton = (Button)findViewById(R.id.buttonStartReading);
		consoleButton = (Button)findViewById(R.id.buttonConsole);
		blueSocket = null;
		blueSocketFallback = null;
		bluetoothConnectionState = BluetoothConstants.SOCKET_NULL;
		exceptionStringToDisplay = "";
		errosTV.setText("Erros");
		
		errosTV.setVisibility(View.GONE);
		estadoTV.setVisibility(View.GONE);
		
		connectButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Thread threadToConnect = new Thread(){
					
				    public void run(){
				    	try {
				    		blueSocket = obdReader.createInsecureRfcommSocketToServiceRecord(appUUID);
				    		bluetoothConnectionState = BluetoothConstants.SOCKET_CREATED;
				    	} catch (IOException e) {
				    		exceptionStringToDisplay = exceptionStringToDisplay + e.toString() + "\n";
				    		Log.d("MyTag","Couldn't create Socket");
				    		bluetoothConnectionState = BluetoothConstants.SOCKET_NULL;
				      		blueSocket = null;
				    	}
				      	try {
				      		blueSocket.connect();
				      		bluetoothConnectionState = BluetoothConstants.SOCKET_CONNECTED;
				      	} catch (IOException e) {
				      		exceptionStringToDisplay = exceptionStringToDisplay + e.toString() + "\n";
				      		Log.d("MyTag,", e.toString());
				      		Log.d("MyTag","Couldn't Connect to Socket");
				      		Class<?> clazz = blueSocket.getRemoteDevice().getClass();
				    	    Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};
				    	    try {
				    	    	Method m = clazz.getMethod("createRfcommSocket", paramTypes);
				    	        Object[] params = new Object[]{Integer.valueOf(1)};
				    	        blueSocketFallback = (BluetoothSocket) m.invoke(blueSocket.getRemoteDevice(), params);
				    	        blueSocketFallback.connect();
				    	        blueSocket = blueSocketFallback;
					      		bluetoothConnectionState = BluetoothConstants.SOCKET_CONNECTED;
				    	    } catch (Exception e2) {
				    	    	exceptionStringToDisplay = exceptionStringToDisplay + e2.toString() + "\n";
				    	    	Log.d("MyTag,", e2.toString());
				    	    	bluetoothConnectionState = BluetoothConstants.SOCKET_FALLBACK_ERROR;
				    	        Log.d("My Tag", "Não conectou nem com o FallBack");
				    	    }
				      	}
				    }
				};
				threadToConnect.start();
//				ConnectionThread threadToConnect = new ConnectionThread(obdReader, appUUID);
//				threadToConnect.start();
			}
		});
		
		startButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(bluetoothConnectionState == BluetoothConstants.SOCKET_CONNECTED){
					estadoTV.setText(exceptionStringToDisplay + "\nSocket Aberto e Conectado!");
					Log.d("MyTag","Socket Aberto e Conectado!");
				}
				else if(bluetoothConnectionState == BluetoothConstants.SOCKET_CREATED) {
					estadoTV.setText(exceptionStringToDisplay + "\nCouldnt' connect to socket");
				}
				else if(bluetoothConnectionState == BluetoothConstants.SOCKET_FALLBACK_ERROR){
					estadoTV.setText(exceptionStringToDisplay + "\nSocket Fallback Failed.");
				}
				else if(bluetoothConnectionState == BluetoothConstants.SOCKET_NULL){
					estadoTV.setText(exceptionStringToDisplay + "\nCouldnt' create to socket");
				}
				
				if(bluetoothConnectionState == BluetoothConstants.SOCKET_CONNECTED){
					bluetoothConnectionState = BluetoothConstants.CONNECTION_STARTED;
					errosTV.setText("Socket Conectado\n");
					Toast.makeText(getApplicationContext(), "Start OBD Commands", Toast.LENGTH_SHORT).show();
					errosTV.setText(errosTV.getText() + "Passando Socket para Globals\n");
					BluetoothConstants.passingSocket = blueSocket;
					errosTV.setText(errosTV.getText() + "Socket enviado para Globals, Iniciando Intent\n");					
					Intent nextScreen2 = new Intent(getApplicationContext(), ReadingActivity.class);
					errosTV.setText(errosTV.getText() + "Intent Criada, Iniciando Activity\n");
					startActivity(nextScreen2);
				}
				else{
					Toast.makeText(getApplicationContext(), "Connect First", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		
		consoleButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(bluetoothConnectionState == BluetoothConstants.SOCKET_CONNECTED){
					estadoTV.setText(exceptionStringToDisplay + "\nSocket Aberto e Conectado!");
					Log.d("MyTag","Socket Aberto e Conectado!");
				}
				else if(bluetoothConnectionState == BluetoothConstants.SOCKET_CREATED) {
					estadoTV.setText(exceptionStringToDisplay + "\nCouldnt' connect to socket");
				}
				else if(bluetoothConnectionState == BluetoothConstants.SOCKET_FALLBACK_ERROR){
					estadoTV.setText(exceptionStringToDisplay + "\nSocket Fallback Failed.");
				}
				else if(bluetoothConnectionState == BluetoothConstants.SOCKET_NULL){
					estadoTV.setText(exceptionStringToDisplay + "\nCouldnt' create to socket");
				}
				
				if(bluetoothConnectionState == BluetoothConstants.SOCKET_CONNECTED){
					bluetoothConnectionState = BluetoothConstants.CONNECTION_STARTED;
					errosTV.setText("Socket Conectado\n");
					Toast.makeText(getApplicationContext(), "Start OBD Commands", Toast.LENGTH_SHORT).show();
					errosTV.setText(errosTV.getText() + "Passando Socket para Globals\n");
					BluetoothConstants.passingSocket = blueSocket;
					errosTV.setText(errosTV.getText() + "Socket enviado para Globals, Iniciando Intent\n");					
					Intent nextScreen3 = new Intent(getApplicationContext(), ConsoleActivity.class);
					errosTV.setText(errosTV.getText() + "Intent Criada, Iniciando Activity\n");
					startActivity(nextScreen3);
				}
				else{
					Toast.makeText(getApplicationContext(), "Connect First", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		
		macAdressTextView.setText("Device Mac Adress:" + "\n" + deviceMacAdress);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.connection_screen, menu);
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