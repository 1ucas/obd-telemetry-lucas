package com.manobray.firstbluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ComandoOBD {

	private static final String placeHolderString = "9x9x9abcdx";
	private String comando;
	private String dadoRecebido;
	
	public ComandoOBD(String command){
		this.comando = command;
		this.dadoRecebido = ComandoOBD.placeHolderString;
	}
	
	public String run(InputStream in, OutputStream out) throws IOException{
		sendCommand(out);
		readResult(in);
		return dadoRecebido;
	}

	private void readResult(InputStream in) throws IOException {
		byte b = 0;
	    StringBuilder res = new StringBuilder();
	    // read until '>' arrives
	    while ((char) (b = (byte) in.read()) != '>')
	      res.append((char) b);
	    dadoRecebido = res.toString().trim();
	    dadoRecebido = dadoRecebido.substring(dadoRecebido.lastIndexOf(13) + 1);
	    if(this.comando.equals("0151")) dadoRecebido = res.toString().trim();
	}

	private void sendCommand(OutputStream out) throws IOException {
		comando += "\r";
		out.write(comando.getBytes());
	    out.flush();
	    try {
			Thread.sleep(100);  // era pra ser 200
		} catch (InterruptedException e) {
		}
	}
}
