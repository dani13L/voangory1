package com.example.suiver_bot;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    public static TextView statut;
    private Button validPid,btnRight,btnLeft,btnAccelere,btnRecule,validVitesse;
    public static Button btnCalibrer;
    public static Button connectBtn;
    private EditText kpText,kdText, vitessMinText, vitessMaxText;
    private Spinner spinner;
    private String mode[]={"Libre","suiver"};

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice[] btArray;
    private BluetoothConnection bluetooth;
    public static boolean checkConnection=false;
    private ListView listview;
    private ClientClass clientClass;
    public static boolean calibrateFait=false;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        compsanteToCode();
        //spinner*********************************************************************************************************
        ArrayAdapter aa=new ArrayAdapter(this,android.R.layout.simple_spinner_item,mode);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==1){
                    if(checkConnection){
                        if(calibrateFait) {
                            clientClass.sendData("s");
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Ataovy clibration", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(MainActivity.this, "connction non etablie", Toast.LENGTH_SHORT).show();
                    }
                }
                else if(i==0){
                    if(checkConnection){
                    clientClass.sendData("f");
                    }
                    else{
                        Toast.makeText(MainActivity.this, "connction non etablie", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        activerBlue();
        bluetooth = new BluetoothConnection(MainActivity.this);

        //chargement des bluetooth accociés dans un liste view
        listview = afficheBluetooth(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        builder.setView(listview);
        final AlertDialog dialog = builder.create();

        //Action d'afficher la Liste Bluetooth
        connectBtn.setOnClickListener(view -> dialog.show());

        // Action pour le listeView contient les bluetooth
        listview.setOnItemClickListener((adapterView, view, i, l) -> {
            clientClass = new ClientClass(btArray[i]);
            clientClass.start();
            statut.setText("Connecting...");
            dialog.dismiss();
        });

        //action de bouton valider pid*********************************************************************************************
        validPid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkConnection) {
                    String kp = kpText.getText().toString();
                    String kd=kdText.getText().toString();
                    clientClass.sendData("p");
                    clientClass.sendData(kp);
                    clientClass.sendData(kd);
                    Toast.makeText(MainActivity.this, "kp =" + kp + " kd ="+kd, Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MainActivity.this, "connction non etablie", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //action de bouton valider vitesse*********************************************************************************************
        validVitesse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkConnection) {
                    String vmin = vitessMinText.getText().toString();
                    String vmax= vitessMaxText.getText().toString();
                    clientClass.sendData("v");
                    clientClass.sendData(vmin);
                    clientClass.sendData(vmax);
                    Toast.makeText(MainActivity.this, "vmin =" + vmin + " vmax"+ vmax, Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MainActivity.this, "connction non etablie", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //test moteur droite*********************************************************************************************
        btnRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(checkConnection) {
                    if(motionEvent.getAction()==MotionEvent.ACTION_DOWN) {
                        clientClass.sendData("r");
                        Toast.makeText(MainActivity.this, "test moteur droite", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    else if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                        clientClass.sendData("m");
                        return true;
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "connction non etablie", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        //test moteur gauche*********************************************************************************************
        btnLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (checkConnection) {
                    if(motionEvent.getAction()==MotionEvent.ACTION_DOWN) {
                        clientClass.sendData("l");
                        Toast.makeText(MainActivity.this, "test moteur gauche", Toast.LENGTH_SHORT).show();
                    }
                    else if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                        clientClass.sendData("m");
                    }

                } else {
                    Toast.makeText(MainActivity.this, "connction non etablie", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        //btn avancer*********************************************************************************************
        btnAccelere.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (checkConnection) {
                    if(motionEvent.getAction()==MotionEvent.ACTION_DOWN) {
                        clientClass.sendData("a");
                    }
                    else if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                        clientClass.sendData("z");
                    }

                } else {
                    Toast.makeText(MainActivity.this, "connction non etablie", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        // btn reculer*********************************************************************************************
        btnRecule.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (checkConnection) {
                    if(motionEvent.getAction()==MotionEvent.ACTION_DOWN) {
                        clientClass.sendData("q");
                    }
                    else if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                        clientClass.sendData("z");
                    }

                } else {
                    Toast.makeText(MainActivity.this, "connction non etablie", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        //btn calibrer*********************************************************************************************
        btnCalibrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkConnection){
                    clientClass.sendData("c");
                    btnCalibrer.setText("LOAD CAL");
                }
                else {
                    Toast.makeText(MainActivity.this, "connction non etablie", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    //activation  bluetooth--------------------------------------------------------------------------------------------------------------
    @SuppressLint("MissingPermission")
    public void activerBlue() {
        while (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
    }

    //Methode qui retoune les listes des bluetooth associés------------------------------------------------------------------------------------------------------------------------------------
    @SuppressLint("MissingPermission")
    public ListView afficheBluetooth(Context context) {
        ListView listViewe = new ListView(context);
        Set<BluetoothDevice> bt = mBluetoothAdapter.getBondedDevices();
        String[] strings = new String[bt.size()];
        btArray = new BluetoothDevice[bt.size()];
        int index = 0;
        if (bt.size() > 0) {
            for (BluetoothDevice device : bt) {
                btArray[index] = device;
                strings[index] = device.getName();
                index++;
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, strings);
        listViewe.setAdapter(adapter);
        return listViewe;
    }

    //Methode findviewByid------------------------------------------------------------------------------------------------------------------------------------
    public void compsanteToCode(){
        statut=(TextView) findViewById(R.id.txt_staut);
        validPid=(Button) findViewById(R.id.btn_validpid);
        connectBtn=(Button) findViewById(R.id.connect_btn);
        kpText=(EditText) findViewById(R.id.value_kp);
        kdText=(EditText) findViewById(R.id.value_kd);
        btnRight=(Button) findViewById(R.id.btn_mdroite);
        btnLeft=(Button) findViewById(R.id.btn_mgauche);
        btnAccelere=(Button) findViewById(R.id.btn_avancer);
        btnRecule=(Button) findViewById(R.id.btn_reculer);
        btnCalibrer=(Button) findViewById(R.id.btn_calibre);
        validVitesse=(Button) findViewById(R.id.btn_validvitesse);
        vitessMinText=(EditText) findViewById(R.id.value_vmin);
        vitessMaxText=(EditText) findViewById(R.id.value_vmax);
        spinner=(Spinner) findViewById(R.id.spinner_mode);
    }
}