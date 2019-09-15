package br.maua.tcc_can03;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.consumerphysics.android.sdk.callback.cloud.ScioCloudAnalyzeCallback;
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
import java.util.List;

public class Escanear_Amostra extends Procurar_Bluetooth {
    Button btnScan;
    Button btnCalibrar;
    List modelosID = preencherModelosID();
    List modelosNome = preencherModelosNome();
    private ScioReading scan;
    static String tvTexto;
    static String tvValor;
    static String tvData;
    static String tvTextoTitulo1;
    static String tvTextoTitulo2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escanear__amostra);
        final ScioDevice myScio = new ScioDevice(getApplicationContext(), Procurar_Bluetooth.address);
        final ScioCloud scioCloud = new ScioCloud(this);
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
            @SuppressLint("SetTextI18n")
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
                        Log.d("SCiOScan", "SCiO conseguiu ler amostra!");
                        analisar(scioCloud);
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
                if(myScio.isCalibrationNeeded()){
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
                }else{
                    Toast.makeText(getApplicationContext(), "SCiO já calibrado!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void analisar(ScioCloud scioCloud) {
        for (int i = 0; i < modelosID.size(); i++) {
            resultadoAnalise(scan, String.valueOf(modelosID.get(i)), scioCloud);
        }
        Toast.makeText(getApplicationContext(), "Analisando...", Toast.LENGTH_LONG).show();
        tvTextoTitulo1 = String.valueOf(modelosNome.get(0));
        tvTextoTitulo2 = String.valueOf(modelosNome.get(1));

        final int MILISEGUNDOS = 5000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MostrarResultadoActivity.class);
                startActivity(intent);
            }
        }, MILISEGUNDOS);
    }

    public void resultadoAnalise(final ScioReading leituraAmostra, String modelId, final ScioCloud scioCloud) {
        scioCloud.analyze(leituraAmostra, modelId, new ScioCloudAnalyzeCallback() {
            @Override
            public void onSuccess(final ScioModel model) {
                if (model.getAttributes() != null && !model.getAttributes().isEmpty()) {
                    List<ScioAttribute> modelAttributes = model.getAttributes();
                    ScioAttribute attribute = modelAttributes.get(0);
                    String attributeType = attribute.getAttributeType().toString();
                    String attributeUnit = attribute.getUnits();
                    String attributeValue;
                    Log.i("SCiO Analise", "Resultado analisado com sucesso!");
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
                    Log.i("SCiO Analise", "Result model attribute type: " + attributeType);
                    Log.i("SCiO Analise", "Result model Get Units: " + attributeUnit);
                    Log.i("SCiO Analise", "Result model Value: " + attributeValue);
                } else {
                    Log.i("SCiO Analise", "Atributo vazio");
                }
            }
            @Override
            public void onError(int errorCode, String message) {
                Log.e("SCiO Analise", "on error: " + message);
            }
        });
    }

    private ArrayList<String> preencherModelosID() {
        ArrayList<String> dados = new ArrayList<>();
        dados.add("ee829ab2-ac8c-4943-8196-103872b1d022");
        dados.add("88b13152-5455-44ef-a401-49069bf7d8a2");
        //dados.add("f0ced1a5-c4fd-4956-b51c-6c19f860e530");
        return dados;
    }

    private ArrayList<String> preencherModelosNome() {
        ArrayList<String> dados = new ArrayList<>();
        dados.add("Modelo Tipo Leite");
        dados.add("Modelo Gordura");
        //dados.add("f0ced1a5-c4fd-4956-b51c-6c19f860e530");
        return dados;
    }
}