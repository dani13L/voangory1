package com.example.suiver_bot;

import android.bluetooth.BluetoothSocket;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SendReceive extends Thread {
    private final BluetoothSocket bluetoothSocket;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public SendReceive(BluetoothSocket socket) {
        bluetoothSocket = socket;
        InputStream tempIn = null;
        OutputStream tempOut = null;

        try {
            tempIn = bluetoothSocket.getInputStream();
            tempOut = bluetoothSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        inputStream = tempIn;
        outputStream = tempOut;

    }
    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;
        while (true) {
            try {
                bytes = inputStream.read(buffer);
                BluetoothConnection.handler.obtainMessage(BluetoothConnection.STATE_MESSAGE_RECEIVED, bytes, -1, buffer).sendToTarget();
            } catch (IOException  e) {
                e.printStackTrace();
            }

        }
    }

    public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                Message message = Message.obtain();
                message.what = BluetoothConnection.STATE_CONNECTION_FAILED;
               BluetoothConnection.handler.sendMessage(message);

            }
        }
    }

