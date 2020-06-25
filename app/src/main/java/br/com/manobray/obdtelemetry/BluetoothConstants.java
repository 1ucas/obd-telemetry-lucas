package br.com.manobray.obdtelemetry;

import android.bluetooth.BluetoothSocket;

public class BluetoothConstants {
    public static final int SOCKET_NULL = 1;
    public static final int SOCKET_CREATED = 2;
    public static final int SOCKET_FALLBACK_ERROR = 3;
    public static final int SOCKET_CONNECTED = 4;
    public static final int CONNECTION_STARTED = 5;

    public static BluetoothSocket passingSocket;
}
