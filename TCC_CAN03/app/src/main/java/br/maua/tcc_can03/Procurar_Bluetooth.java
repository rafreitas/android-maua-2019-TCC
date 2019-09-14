package br.maua.tcc_can03;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.consumerphysics.android.scioconnection.utils.BLEUtils;
import com.consumerphysics.android.sdk.callback.device.ScioDeviceConnectHandler;
import com.consumerphysics.android.sdk.sciosdk.ScioDevice;

import java.util.ArrayList;

public class Procurar_Bluetooth extends AppCompatActivity {

    final static int REQUEST_ENABLE_BT = 1;
    static String address;
    static boolean bluetoothAtivo = false;
    Button btnAtualizarBT;
    ArrayList<String> stringArrayList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    BluetoothAdapter btAdapt;
    BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName() + "";
                if (deviceName.startsWith("SCiO")) {
                    stringArrayList.add(device.getName());
                    arrayAdapter.notifyDataSetChanged();
                    address = device.getAddress();
                    final ScioDevice myScio = new ScioDevice(getApplicationContext(), device.getAddress());
                    myScio.connect(new ScioDeviceConnectHandler() {
                        @Override
                        public void onConnected() {
                            Log.d("SCiOApp", "SCiO conectado!");
                            Toast.makeText(getApplicationContext(), "SCiO conectado!", Toast.LENGTH_SHORT).show();
                            bluetoothAtivo = true;
                            finish();
                        }

                        @Override
                        public void onConnectFailed() {
                            Log.d("SCiOApp", "Falha na conexão");
                        }

                        @Override
                        public void onTimeout() {
                            Toast.makeText(getApplicationContext(), "Tempo excedido", Toast.LENGTH_SHORT).show();
                            Log.d("SCiOApp", "Tempo excedido");
                        }
                    });
                } else {
                    Log.d("SCiOApp", "Array vazia");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_procurar__bluetooth);

        int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
        permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
        if (permissionCheck != 0) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
        }

        btnAtualizarBT = findViewById(R.id.btnAtualizarBT);
        btAdapt = BLEUtils.getBluetoothAdapter(this);

        if (btAdapt == null) {
            Log.d("onCreate", "Dispositivo não suportado");
        } else {
            Intent btEnableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(btEnableIntent, REQUEST_ENABLE_BT);
        }

        btnAtualizarBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayAdapter.clear();
                btAdapt.startDiscovery();
                Log.d("btnAtualizarBT", "Buscando dispositivo");
                Toast.makeText(getApplicationContext(), "Buscando dispositivo", Toast.LENGTH_SHORT).show();
                IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(myReceiver, intentFilter);
            }
        });

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringArrayList);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy", "Dispositivo destruido");
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        super.unregisterReceiver(receiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == -RESULT_OK) {
            Log.d("RESULT_OK", "Bluetooth ligado");
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}