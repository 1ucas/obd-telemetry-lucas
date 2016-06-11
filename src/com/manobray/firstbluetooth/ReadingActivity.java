package com.manobray.firstbluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ReadingActivity extends Activity {

	private BluetoothSocket blueSocket;
	private InputStream mInputStream;
	private OutputStream mOutputStream;
	private String stringRPM, stringSPEED, stringFuelType, stringTempoLigado, stringBateria;
	private String stringConsumo, stringMAF, stringFuelLevel, stringEngineFuelRate, stringDistTravelCC;
	private TextView rpmTextView, speedTextView, tempoLigadoTV, fuelConsumptionTV, myErrosTV, bateriaTV;
	private TextView mafTV, fuelLevelTV, fuelTypeTV, fuelRateTV, distanceTravelTV;
	private Thread threadUI, threadFetch;
	private Button updateUIButton, translateButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reading);
		
		blueSocket = BluetoothConstants.passingSocket;
		stringRPM = "";
		stringSPEED = "";
		rpmTextView = (TextView) findViewById(R.id.rpmTextView2);
		speedTextView = (TextView) findViewById(R.id.speedTextView);
		myErrosTV = (TextView) findViewById(R.id.myErroTextView);
		tempoLigadoTV = (TextView) findViewById(R.id.tempoLigadoTextView);
		fuelTypeTV = (TextView) findViewById(R.id.fuelTypeTextView);
		fuelConsumptionTV = (TextView) findViewById(R.id.fuelConsumptionRateTextView);
		bateriaTV = (TextView) findViewById(R.id.voltageTextView);
		mafTV = (TextView) findViewById(R.id.MassAirFLowRateTextView);
		fuelLevelTV = (TextView) findViewById(R.id.FuelLevelInputTextView);
		fuelRateTV = (TextView) findViewById(R.id.EngineFuelRateInputTextView);
		distanceTravelTV = (TextView) findViewById(R.id.DistanceSinceCodesInputTextView);
		
		fuelTypeTV.setVisibility(View.GONE);
		fuelLevelTV.setVisibility(View.GONE);
		fuelRateTV.setVisibility(View.GONE);
		fuelConsumptionTV.setVisibility(View.GONE);
		
		myErrosTV.setText("Olá!\n");
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {
			
		}
		try {
			setupConnection();
			myErrosTV.setText(myErrosTV.getText() + "Setup Sucessfull\n");
		} catch (IOException e) {
			myErrosTV.setText(myErrosTV.getText() + "Erro no Setup\n");
			Toast.makeText(getApplicationContext(), "Couldn't fetch Streams", Toast.LENGTH_SHORT).show();
		}
		

		// THREADS
		myErrosTV.setText(myErrosTV.getText() + "Criando Thread UI\n");
		
		// Atualiza Interface Gráfica
		
		threadUI = new Thread(){
			@Override
			public void run(){
				while(true){
					new updateUIClass().execute("", "", "");
					
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {}
				}
			}
		};
		myErrosTV.setText(myErrosTV.getText() + "Criando Thread Fetch\n");
		
		// Recebe dados do ELM 327
		
		threadFetch = new Thread(){
			@Override
			public void run(){
				while(true){
					//Log.d("MyTag", "Chegou aqui2");
					//myErrosTV.setText(myErrosTV.getText() + "Método Run Thread Fetch Iniciado\n");
					readRPM();
					readSpeed();
					readFuelConsumption();
					readTempoLigado();
					readFuelType();
					readBatteryVolage();
					readMAFRate();
					readFuelLevel();
					readEngineFuelRate();
					readDistanceTravel();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {}
				}
			}
			private void readDistanceTravel()
			{
				try{
					ComandoOBD comandoDistanceTravel = new ComandoOBD("0131");
					String distRecebida = comandoDistanceTravel.run(mInputStream, mOutputStream);
					stringDistTravelCC = distRecebida;
				} catch (IOException e){
					Toast.makeText(getApplicationContext(), "Couldn't fetch Speed", Toast.LENGTH_SHORT).show();
				}
			}
			
			
			private void readEngineFuelRate()
			{
				try{
					ComandoOBD comandoMAF = new ComandoOBD("015E");
					String mafRecebido = comandoMAF.run(mInputStream, mOutputStream);
					stringEngineFuelRate = mafRecebido;
				} catch (IOException e){
					Toast.makeText(getApplicationContext(), "Couldn't fetch Speed", Toast.LENGTH_SHORT).show();
				}
			}
			
			
			private void readMAFRate()
			{
				try{
					ComandoOBD comandoMAF = new ComandoOBD("0110");
					String mafRecebido = comandoMAF.run(mInputStream, mOutputStream);
					stringMAF = mafRecebido;
				} catch (IOException e){
					Toast.makeText(getApplicationContext(), "Couldn't fetch Speed", Toast.LENGTH_SHORT).show();
				}
			}
					
			
			private void readFuelLevel()
			{
				try{
					ComandoOBD comandoFuelLevel = new ComandoOBD("012F");
					String fuelLevelRecebido = comandoFuelLevel.run(mInputStream, mOutputStream);
					stringFuelLevel = fuelLevelRecebido;
				} catch (IOException e){
					Toast.makeText(getApplicationContext(), "Couldn't fetch Speed", Toast.LENGTH_SHORT).show();
				}
			}
			
			private void readRPM() {
				try{
					ComandoOBD comandoRPM = new ComandoOBD("010C");
					String rpmRecebido = comandoRPM.run(mInputStream, mOutputStream);
					stringRPM = rpmRecebido;
				} catch (IOException e){
					Toast.makeText(getApplicationContext(), "Couldn't fetch Speed", Toast.LENGTH_SHORT).show();
				}
			}

			private void readSpeed() {
				try{
					ComandoOBD comandoSpeed = new ComandoOBD("010D");
					String velocidadeRecebida = comandoSpeed.run(mInputStream, mOutputStream);
					stringSPEED = velocidadeRecebida;
				} catch (IOException e){
					Toast.makeText(getApplicationContext(), "Couldn't fetch RPM", Toast.LENGTH_SHORT).show();
				}
			}
			
			private void readFuelConsumption(){
				try{
					ComandoOBD comandoConsumo = new ComandoOBD("015E");
					String consumoRecebido = comandoConsumo.run(mInputStream, mOutputStream);
					stringConsumo = consumoRecebido;
				} catch (IOException e){
					Toast.makeText(getApplicationContext(), "Couldn't fetch Fuel Type", Toast.LENGTH_SHORT).show();
				}
			}
			
			private void readTempoLigado()
			{
				try{
					ComandoOBD comandoTempoLigado = new ComandoOBD("011F");
					String tempoRecebido = comandoTempoLigado.run(mInputStream, mOutputStream);
					stringTempoLigado = tempoRecebido;
				} catch (IOException e){
					Toast.makeText(getApplicationContext(), "Couldn't fetch Fuel Type", Toast.LENGTH_SHORT).show();
				}
			}
			
			private void readFuelType(){
				try{
					ComandoOBD comandoTipoCombustivel = new ComandoOBD("0151");
					String tipoCombustivelRecebido = comandoTipoCombustivel.run(mInputStream, mOutputStream);
					stringFuelType = tipoCombustivelRecebido;
				} catch (IOException e){
					Toast.makeText(getApplicationContext(), "Couldn't fetch Fuel Consumption", Toast.LENGTH_SHORT).show();
				}
			}
			
			private void readBatteryVolage(){
				try{
					ComandoOBD comandoBateria = new ComandoOBD("ATRV");
					String leituraBateria = comandoBateria.run(mInputStream, mOutputStream);
					stringBateria = leituraBateria;
				} catch (IOException e) {
					Toast.makeText(getApplicationContext(), "Couldn't fetch Battery Voltage", Toast.LENGTH_SHORT).show();
				}
			}

		};
		runOBDSystem(threadUI, threadFetch);
		myErrosTV.setText("");
	}

	private void runOBDSystem(Thread thread1, Thread thread2) {
		
		myErrosTV.setText(myErrosTV.getText() + "Iniciando Thread UI\n");
		thread1.start();
		myErrosTV.setText(myErrosTV.getText() + "Iniciando Thread Fetch\n");
		thread2.start();
	}

	private void setupConnection() throws IOException {
		mInputStream = blueSocket.getInputStream();
		mOutputStream = blueSocket.getOutputStream();
		ComandoOBD comandoConfiguracoes = new ComandoOBD("AT SP 0");
		comandoConfiguracoes.run(mInputStream, mOutputStream);
		
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.reading, menu);
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
	
	
	public class updateUIClass extends AsyncTask<String, String, String>{
	         /* Before starting background do some work.
	         * */
	        @Override
	        protected void onPreExecute() {
	        }

	        @Override
	        protected String doInBackground(String... params) {
	            return null;
	        }

	        /**
	         * Atualiza Strings mostradas na tela.
	         */
	        public String translateRPMString(String rpmString){
	        	if(rpmString == null){
	        		return "Valor não lido";
	        	}
	        	else if(rpmString == "NO DATA"){
	        		return "NO DATA";
	        	}
	//        	else return rpmString;
	        	int rpmValorInt = 0;
	        	String stringRPMCopiada = "" + stringRPM;
				stringRPMCopiada = stringRPMCopiada.replace(" ", "");
				if(stringRPMCopiada!= ""){
					rpmValorInt = Integer.parseInt(stringRPMCopiada,16) - Integer.parseInt("410C0000",16);
					rpmValorInt = rpmValorInt/4;
				}
				return String.valueOf(rpmValorInt);
	        }
	        
	        public String translateSpeedString(String speedString){
	        	if(speedString == null){
	        		return "Valor não lido";
	        	}
	        	else if(speedString == "NO DATA"){
	        		return "NO DATA";
	        	}
//	        	else return speedString;
	        	int speedValorInt = 0;
	        	String stringSpeedCopiada = "" + stringSPEED;
	        	stringSpeedCopiada = stringSpeedCopiada.replace(" ", "");
	        	if(stringSpeedCopiada != ""){
	        		speedValorInt = Integer.parseInt(stringSpeedCopiada, 16) - Integer.parseInt("410D00", 16);
	        	}
	        	return String.valueOf(speedValorInt);
	        }
	        
	        public String translateEngineRuntime(String runtimeString)
	        {
	        	if(runtimeString == null){
	        		return "Valor não lido";
	        	}
	        	else if(runtimeString == "NO DATA"){
	        		return "NO DATA";
	        	}
//	        	else return runtimeString;
	        	int runtimeInt = 0;
	        	long segundos= 0, minutos= 0 , horas= 0;
	        	String runtimeCopiada = "";
	        	runtimeCopiada = "" + runtimeString;
	        	String respostaRuntimeTraduzida = "String Vazia";
	        	runtimeCopiada = runtimeCopiada.replace(" ", "");
	        	runtimeCopiada = runtimeCopiada + "";
	        	if(runtimeCopiada != "" && runtimeCopiada != "null" && runtimeString != null){
	           		runtimeInt = Integer.parseInt(runtimeCopiada, 16) - Integer.parseInt("411F0000", 16);
	        		segundos = runtimeInt % 60;
		        	minutos = (runtimeInt / 60) % 60;
		        	horas = (runtimeInt / (60 * 60)) % 24;
		        	respostaRuntimeTraduzida = String.format("%02d:%02d:%02d", horas,minutos,segundos);
	        	}
	        	return respostaRuntimeTraduzida;
	        }
	        
	        public String translateMAFString(String MAFString){
	        	if(MAFString == null){
	        		return "Valor não lido";
	        	}
	        	else if(MAFString == "NO DATA"){
	        		return "NO DATA";
	        	}
//	        	else return MAFString;
	        	int MAFvalorInt = 0;
				String stringMAFCopiada = "";
				stringMAFCopiada = " " +  MAFString;
				
				stringMAFCopiada = stringMAFCopiada.replace(" ", "");
				if(stringMAFCopiada != "" && MAFString != null){
					MAFvalorInt = Integer.parseInt(stringMAFCopiada,16) - Integer.parseInt("41100000",16);
					MAFvalorInt = MAFvalorInt/100;
				}
				return String.valueOf(MAFvalorInt);
	        }
	        
	        
	        public String translateDistTravel(String distString)
	        {
	        	if(distString == null){
	        		return "Valor não lido";
	        	}
	        	else if(distString == "NO DATA"){
	        		return "NO DATA";
	        	}
	//        	else return rpmString;
	        	int distValorInt = 0;
	        	String stringDistTravelCopiada = "" + stringDistTravelCC;
	        	stringDistTravelCopiada = stringDistTravelCopiada.replace(" ", "");
				if(stringDistTravelCopiada!= ""){
					distValorInt = Integer.parseInt(stringDistTravelCopiada,16) - Integer.parseInt("41310000",16);
				}
				return String.valueOf(distValorInt);
	        }
	        
	        	
	        protected void onPostExecute(String file_url) {
	        	String stringVelocidadeToDisplay = "Velocidade:  " + translateSpeedString(stringSPEED) + " Km/h";
				String tempoLigadoToDisplay = "Tempo de Motor Ligado:  " + translateEngineRuntime(stringTempoLigado);
				String fuelTypeToDisplay = "Tipo de Combustivel:  " + stringFuelType;
				String batteryToDisplay = "Tensão na Bateria:  " + stringBateria;
				String stringRPMtoDisplay = "Rotações:  " + translateRPMString(stringRPM) + " rpm"; //"RPM:  " + rpmStringTraduzida + " rpm";
				String consumoToDisplay = "Consumo:  " + stringConsumo;
				String combustivelLevelToDisplay = "Nível do Combustível:  " + stringFuelLevel + " %";
				String mafToDisplay = "MAF rate:  " + translateMAFString(stringMAF) + " g/seg";
				String engineFuelRateToDisplay = "Litros/H:" +  stringEngineFuelRate;
				String distTravelCC = "Distancia de Viagem: " + translateDistTravel(stringDistTravelCC) + " Km";
				
				rpmTextView.setText(stringRPMtoDisplay);
				speedTextView.setText(stringVelocidadeToDisplay);
				fuelConsumptionTV.setText(consumoToDisplay);
				tempoLigadoTV.setText(tempoLigadoToDisplay);
				bateriaTV.setText(batteryToDisplay);
				fuelConsumptionTV.setText(consumoToDisplay);
				fuelTypeTV.setText(fuelTypeToDisplay);
				fuelLevelTV.setText(combustivelLevelToDisplay);
				mafTV.setText(mafToDisplay);
				fuelRateTV.setText(engineFuelRateToDisplay);
				distanceTravelTV.setText(distTravelCC);
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
                	try {
                		threadFetch.interrupt();
                		threadUI.interrupt();
						blueSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                	try {
                		threadFetch.interrupt();
                		threadUI.interrupt();
						blueSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
                	break;
                case BluetoothAdapter.STATE_ON:
                	break;
                case BluetoothAdapter.STATE_TURNING_ON:
                	break;
                }
            }
        }
    };
    
    @Override
    public void onBackPressed() {
        // Write your code here
    	try {
    		threadFetch.interrupt();
    		threadUI.interrupt();
			blueSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        super.onBackPressed();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();

        /* ... */

        // Unregister broadcast listeners
        unregisterReceiver(mReceiver);
    }
}

