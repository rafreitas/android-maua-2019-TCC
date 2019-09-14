package br.maua.tcc_can03;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.consumerphysics.android.sdk.callback.cloud.ScioCloudAnalyzeCallback;
import com.consumerphysics.android.sdk.callback.cloud.ScioCloudModelsCallback;
import com.consumerphysics.android.sdk.callback.device.ScioDeviceBatteryHandler;
import com.consumerphysics.android.sdk.callback.device.ScioDeviceCalibrateHandler;
import com.consumerphysics.android.sdk.callback.device.ScioDeviceConnectHandler;
import com.consumerphysics.android.sdk.callback.device.ScioDeviceScanHandler;
import com.consumerphysics.android.sdk.model.ScioBattery;
import com.consumerphysics.android.sdk.model.ScioModel;
import com.consumerphysics.android.sdk.model.ScioReading;
import com.consumerphysics.android.sdk.model.attribute.ScioAttribute;
import com.consumerphysics.android.sdk.model.attribute.ScioDatetimeAttribute;
import com.consumerphysics.android.sdk.model.attribute.ScioNumericAttribute;
import com.consumerphysics.android.sdk.model.attribute.ScioStringAttribute;
import com.consumerphysics.android.sdk.sciosdk.ScioCloud;
import com.consumerphysics.android.sdk.sciosdk.ScioDevice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Escanear_Amostra extends Procurar_Bluetooth {
    Button btnScan;
    Button btnCalibrar;
    Button btnObterAmostraID;
    Button btnAnalisar;
    ArrayList dados = new ArrayList<String>();
    private ScioReading scan;
    static String tvTexto;
    static String tvValor;
    static String tvData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escanear__amostra);
        final ScioDevice myScio = new ScioDevice(getApplicationContext(), Procurar_Bluetooth.address);
        final ScioCloud scioCloud = new ScioCloud(this);
        final ListView Lista = findViewById(R.id.listViewBT);
        myScio.connect(new ScioDeviceConnectHandler() {
            @Override
            public void onConnected() {
                Log.d("SCiOScan", "SCiO was connected successfully");
            }

            @Override
            public void onConnectFailed() {
                Log.d("SCiOScan", "Error while connecting");
            }

            @Override
            public void onTimeout() {
                Log.d("SCiOScan", "Timeout while connecting");
            }
        });

        myScio.readBattery(new ScioDeviceBatteryHandler() {
            @Override
            public void onSuccess(final ScioBattery battery) {
                TextView testeBateria = findViewById(R.id.bateriaStatus);
                testeBateria.setText(battery.getChargePercentage() + "%");
                Log.d("BateriaApp", "Bateria: " + battery.getChargePercentage() + "%");
            }

            @Override
            public void onError() {
                Log.d("BateriaApp", "Erro obter bateria");
            }

            @Override
            public void onTimeout() {
                Log.d("BateriaApp", "Erro timeout bateria");
            }
        });

        btnScan = findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Escaneando amostra...", Toast.LENGTH_SHORT).show();
                myScio.scan(new ScioDeviceScanHandler() {
                    @Override
                    public void onSuccess(final ScioReading reading) {
                        scan = reading;
                        Toast.makeText(getApplicationContext(), "SCiO conseguiu ler amostra!", Toast.LENGTH_SHORT).show();
                        Log.d("SCiOScan", "SCiO conseguiu ler amostra!");
                    }

                    @Override
                    public void onNeedCalibrate() {
                        Toast.makeText(getApplicationContext(), "SCiO precisa ser calibrado", Toast.LENGTH_SHORT).show();
                        Log.d("SCiOScan", "SCiO precisa ser calibrado");
                    }

                    @Override
                    public void onError() {
                        Log.d("SCiOScan", "Error while connecting");
                    }

                    @Override
                    public void onTimeout() {
                        Log.d("SCiOScan", "Timeout while connecting");
                    }
                });
            }
        });

        btnCalibrar = findViewById(R.id.btnCalibrar);
        btnCalibrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Calibrando SCiO...", Toast.LENGTH_SHORT).show();
                myScio.calibrate(new ScioDeviceCalibrateHandler() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "SCiO calibrado", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.i("SCiOScan", "SCiO calibrado");
                    }

                    @Override
                    public void onError() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Erro calibração", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.i("SCiOScan", "Erro calibracao SCiO");
                    }

                    @Override
                    public void onTimeout() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Time out calibração", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.i("SCiOScan", "Timed out SCiO");
                    }
                });
            }
        });

        btnObterAmostraID = findViewById(R.id.btnObterAmostraID);
        btnObterAmostraID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scioCloud.getModels(new ScioCloudModelsCallback() {
                    @Override
                    public void onSuccess(final List<ScioModel> models) {
                        for (ScioModel model : models) {
                            Log.d("ModeloID", "Model ID=" + model.getId() + "  Name=" + model.getName());
                            dados.addAll(Collections.singleton(model.getName()));
                            mostrarModelos(dados, Lista);
                        }
                    }

                    @Override
                    public void onError(int errorCode, String message) {
                        Log.i("DemoApp", "get models on Error");
                        Log.i("DemoApp", "----->" + message);
                    }
                });
            }
        });

        btnAnalisar = findViewById(R.id.btnAnalisar);
        btnAnalisar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                analyze(scan, "88b13152-5455-44ef-a401-49069bf7d8a2", scioCloud);
            }
        });
    }

    private void mostrarModelos(ArrayList<String> dados, ListView Lista) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dados);
        Lista.setAdapter(arrayAdapter);
    }

    public void analyze(final ScioReading leituraAmostra, final String modelId, final ScioCloud scioCloud) {
        scioCloud.analyze(leituraAmostra, modelId, new ScioCloudAnalyzeCallback() {
            @Override
            public void onSuccess(final ScioModel model) {
                if (model.getAttributes() != null && !model.getAttributes().isEmpty()) {
                    List<ScioAttribute> modelAttributes = model.getAttributes();
                    ScioAttribute attribute = modelAttributes.get(0);
                    String attributeType = attribute.getAttributeType().toString();
                    String attributeUnit = attribute.getUnits();
                    String attributeValue;
                    Log.i("DemoApp analyze", "Resultado analisado com sucesso!");
                    switch (attribute.getAttributeType()) {
                        case STRING:
                            attributeValue = ((ScioStringAttribute) attribute).getValue();
                            tvTexto = attributeValue;
                            break;
                        case NUMERIC:
                            attributeValue = String.valueOf(((ScioNumericAttribute) (attribute)).getValue());
                            tvValor = attributeValue;
                            break;
                        case DATE_TIME:
                            attributeValue = ((ScioDatetimeAttribute) (attribute)).getValue().toString();
                            tvData = attributeValue;
                            break;
                        default:
                            attributeValue = "N/A";
                    }
                    Log.i("DemoApp analyze", "Result model attribute type: " + attributeType);
                    Log.i("DemoApp analyze", "Result model Get Units: " + attributeUnit);
                    Log.i("DemoApp analyze", "Result model Value: " + attributeValue);
                    Toast.makeText(getApplicationContext(), "Gordura: " + attributeValue + " " + attributeUnit, Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(getApplicationContext(), MostrarResultadoActivity.class);
                    startActivity(intent);
                } else {
                    Log.i("DemoApp analyze", "Atributo vazio");
                }
            }

            @Override
            public void onError(int errorCode, String message) {
                Log.e("DemoApp analyze", "on error: " + message);
            }
        });
    }
}