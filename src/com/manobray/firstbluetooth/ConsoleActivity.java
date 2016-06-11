package com.manobray.firstbluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.manobray.firstbluetooth.ReadingActivity.updateUIClass;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ConsoleActivity extends Activity {

	private BluetoothSocket blueSocket;
	private InputStream mInputStream;
	private OutputStream mOutputStream;
	
	private String respostaString, protocolString;
	
	private EditText mensagemEditText, respostaEditText;
	private TextView protocoloTextView, statusTextView;
	private int startRead;
	
	private Thread threadUI, threadFetch;
	
	private Button sendButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_console);
		
		blueSocket = BluetoothConstants.passingSocket;
		mensagemEditText = (EditText)findViewById(R.id.mensagemEditText);
		respostaEditText = (EditText)findViewById(R.id.respostaEditText);
		protocoloTextView = (TextView)findViewById(R.id.protocolTextView);
		statusTextView = (TextView)findViewById(R.id.statusTextView);
		sendButton = (Button)findViewById(R.id.sendButton);
		respostaString = "Resposta:";
		protocolString = "Protocolo Selecionado:";
		startRead =0;
		try {
			setupConnection();
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), "Couldn't fetch Streams", Toast.LENGTH_SHORT).show();
		}
		threadUI = new Thread(){
			@Override
			public void run(){
				while(true)
				{
					if(startRead == 0)
					{
						try {
							Thread.sleep(500);
							new UpdateUIClass().execute("", "", "");
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
				}
			}
		};
		
		threadFetch = new Thread(){
			@Override
			public void run(){
				while(true)
				{
					if(startRead == 1)
					{
						ComandoOBD comandoEnviado = new ComandoOBD(mensagemEditText.getText().toString());
						try {
							startRead = 0;
							respostaString = comandoEnviado.run(mInputStream, mOutputStream);
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				
			}
		};
		
		sendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startRead = 1;				
			}
		});
		
		threadFetch.start();
		threadUI.start();
	}
	
	
	private void setupConnection() throws IOException {
		mInputStream = blueSocket.getInputStream();
		mOutputStream = blueSocket.getOutputStream();		
	}
	
	
	private class UpdateUIClass extends AsyncTask<String,String,String>{

		@Override
		protected String doInBackground(String... params) {
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			String stringEnviada = mensagemEditText.getText().toString();
			if(stringEnviada.contains("AT SP")){
				protocoloTextView.setText(protocolString);
			}
			else respostaEditText.setText(respostaString);
		}
		
		
	}
	
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.console, menu);
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
