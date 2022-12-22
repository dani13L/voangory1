package com.example.suiver_bot;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Message;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ClientClass extends Thread {
    private BluetoothDevice device;
    private BluetoothSocket socket;
    public static boolean checkOtherConnection=false;

    @SuppressLint("MissingPermission")
    public ClientClass(BluetoothDevice device1) {
        device = device1;
        try {
            socket = device.createRfcommSocketToServiceRecord(BluetoothConnection.MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("MissingPermission")
    public void run() {
        try {
            socket.connect();
            Message message = Message.obtain();
            message.what = BluetoothConnection.STATE_CONNECTED;
            BluetoothConnection.handler.sendMessage(message);

           SendReceive sendReceive = new SendReceive(socket);
            sendReceive.start();
        } catch (IOException e) {
            e.printStackTrace();
            Message message = Message.obtain();
            message.what =BluetoothConnection.STATE_CONNECTION_FAILED;
            BluetoothConnection.handler.sendMessage(message);
        }
    }
// Miehode d'envoie de  donne
    public void sendData(String data) {
        if(MainActivity.checkConnection) {
            SendReceive send = new SendReceive(socket);
            send.write(data.getBytes());
        }
        else{
            Message message = Message.obtain();
            message.what = BluetoothConnection.STATE_CONNECTION_FAILED;
           BluetoothConnection.handler.sendMessage(message);
        }
    }

    // Methode de deconnexion
    public void deconnect(){
        if(socket!=null){
            try {
                socket.close();
                Message message = Message.obtain();
                message.what = BluetoothConnection.STATE_CONNECTION_FAILED;
                BluetoothConnection.handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}