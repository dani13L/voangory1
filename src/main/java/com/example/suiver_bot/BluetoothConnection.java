package com.example.suiver_bot;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class BluetoothConnection extends AppCompatActivity {
    // composante dans Main
    private TextView statut;

    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;
    static final int STATE_STOP=6;

    final String INCOMING="3";
    final String ULTR_ON="4";
    final String PIR_ON="5";

    static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
   // static final UUID MY_UUID = UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");
    public static Handler handler;

    @SuppressLint({"MissingPermission", "NotConstructor"})
    public BluetoothConnection(Context context) {
        this.statut = MainActivity.statut;
        toutHandler();
    }

    //Methode toutHandler-------------------------------------------------------------------------------------------
    private void toutHandler() {
         handler = new Handler(new Handler.Callback() {
            @SuppressLint("ResourceAsColor")
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case STATE_CONNECTING:
                        statut.setText("Connecting");
                        MainActivity.connectBtn.setBackgroundResource(R.drawable.nonconnecte);
                        MainActivity.checkConnection=false;
                        break;
                    case STATE_CONNECTED:
                        statut.setText("Connected");
                        MainActivity.checkConnection=true;
                        MainActivity.connectBtn.setBackgroundResource(R.drawable.connecte);
                        MainActivity.connectBtn.setText("connected");

                        break;
                    case STATE_CONNECTION_FAILED:
                        statut.setText("Connection Failed");
                        MainActivity.connectBtn.setBackgroundResource(R.drawable.nonconnecte);
                        MainActivity.checkConnection=false;
                        break;
                    case STATE_STOP:

                        break;
                        //Réception des donnés
                    case STATE_MESSAGE_RECEIVED:
                        byte[] readBuff = (byte[]) msg.obj;
                        String tempMsg = new String(readBuff, 0, msg.arg1);
                        Log.i("test","tempMsg="+tempMsg);
                        if(tempMsg.equals("o")){
                            MainActivity.btnCalibrer.setTextColor(Color.parseColor("#09AB63"));
                            MainActivity.btnCalibrer.setText("CAL OK");
                            MainActivity.calibrateFait=true;
                        }
                        break;
                }
                return true;
            }
        });
    }

    }





