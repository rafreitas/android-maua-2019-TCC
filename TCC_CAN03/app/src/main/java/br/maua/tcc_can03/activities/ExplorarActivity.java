package br.maua.tcc_can03.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.consumerphysics.android.sdk.callback.cloud.ScioCloudAnalyzeCallback;
import com.consumerphysics.android.sdk.callback.device.ScioDeviceBatteryHandler;
import com.consumerphysics.android.sdk.callback.device.ScioDeviceCalibrateHandler;
import com.consumerphysics.android.sdk.callback.device.ScioDeviceScanHandler;
import com.consumerphysics.android.sdk.model.ScioBattery;
import com.consumerphysics.android.sdk.model.ScioModel;
import com.consumerphysics.android.sdk.model.ScioReading;
import com.consumerphysics.android.sdk.model.attribute.ScioAttribute;
import com.consumerphysics.android.sdk.model.attribute.ScioDatetimeAttribute;
import com.consumerphysics.android.sdk.model.attribute.ScioNumericAttribute;
import com.consumerphysics.android.sdk.model.attribute.ScioStringAttribute;

import java.util.ArrayList;
import java.util.List;

import br.maua.tcc_can03.R;

public class ExplorarActivity extends BaseScioActivity {
    private static final String TAG = ExplorarActivity.class.getSimpleName();
    // UI
    private ProgressDialog progressDialog;
    // SCiO
    private ScioDeviceCalibrateHandler scioDeviceCalibrateHandler;
    //MostrarModelo
    static String tvTexto;
    static String tvValor;
    static String tvData;
    static String tvTextoTitulo1;
    static String tvTextoTitulo2;
    static String tvTextoTitulo3;
    static String tvTextoTitulo4;
    static String tvTextoTitulo5;
    static String tvTextoTitulo6;
    List modelosID = preencherModelosID();
    List modelosNome = preencherModelosNome();
    static ArrayList<String> mostrarModelos = new ArrayList<>();
    static boolean confiancaModelo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressDialog = ProgressDialog.show(this, "Aguarde", "Carregando informações...", true);
        connect("C4:BE:84:28:D1:19");
        final int MILISEGUNDOS = 2000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getScioDevice().readBattery(new ScioDeviceBatteryHandler() {
                    @Override
                    public void onSuccess(final ScioBattery scioBattery) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateBattery(scioBattery.getChargePercentage(), scioBattery.isCharging());

                            }
                        });
                    }
                    @Override
                    public void onError() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Erro na leitura da bateria.", Toast.LENGTH_SHORT).show();
                                dismissingProgress();
                            }
                        });
                    }
                    @Override
                    public void onTimeout() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Erro: Timeout na leitura da bateria.", Toast.LENGTH_SHORT).show();
                                dismissingProgress();
                            }
                        });
                    }
                });
            }
        }, MILISEGUNDOS);
    }

    @Override
    protected void onStop() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        super.onStop();
    }

    @Override
    public void onScioConnectionFailed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Conexão com o sensor falhou", Toast.LENGTH_SHORT).show();
                dismissingProgress();
            }
        });
    }

    @Override
    public void onScioConnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "SCiO foi conectado!");
            }
        });
    }

    @Override
    public void onScioDisconnected() {
        super.onScioDisconnected();
        Log.d(TAG, "SCiO desconectado.");
    }

    @Override
    public void onScioButtonClicked() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissingProgress();
                progressDialog = ProgressDialog.show(ExplorarActivity.this, "Aguarde", "Analisando...", false);
                getScioDevice().scan(new ScioDeviceScanHandler() {
                    @Override
                    public void onSuccess(final ScioReading reading) {
                        mostrarModelos.clear();
                        confiancaModelo = false;
                        analisar(reading);
                    }
                    @Override
                    public void onNeedCalibrate() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Antes do scan, calibre o sensor.", Toast.LENGTH_SHORT).show();
                                dismissingProgress();
                            }
                        });
                    }
                    @Override
                    public void onError() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Erro durante scan.", Toast.LENGTH_SHORT).show();
                                dismissingProgress();
                            }
                        });
                    }
                    @Override
                    public void onTimeout() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Erro: Timeout durante scan.", Toast.LENGTH_SHORT).show();
                                dismissingProgress();
                            }
                        });
                    }
                });
            }
        });
    }

    private void updateBattery(int nivel, boolean status) {
        TextView tvBateria = findViewById(R.id.tvBateria);
        if (status){
            tvBateria.setText("Carregando: " + nivel + "%");
        }else if (nivel <= 15 && !status){
            tvBateria.setText("Bateria fraca: " + nivel + "%");
        }else{
            tvBateria.setText(nivel + "%");
        }
        dismissingProgress();
    }

    private void dismissingProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void doCalibrate(final View view) {
        if (!isDeviceConnected()) {
            Toast.makeText(getApplicationContext(), "SCiO não está conectado", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(getApplicationContext(), "Calibrando", Toast.LENGTH_SHORT).show();
        getScioDevice().calibrate(scioDeviceCalibrateHandler);
    }

    public void scanAndAnalyze(View view){
        if (!isDeviceConnected()) {
            Toast.makeText(getApplicationContext(), "SCiO não está conectado", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog = ProgressDialog.show(this, "Aguarde", "Analisando...", false);
        getScioDevice().scan(new ScioDeviceScanHandler() {
            @Override
            public void onSuccess(final ScioReading reading) {
                mostrarModelos.clear();
                confiancaModelo = false;
                analisar(reading);
            }
            @Override
            public void onNeedCalibrate() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Antes do scan, calibre o sensor.", Toast.LENGTH_SHORT).show();
                        dismissingProgress();
                    }
                });
            }
            @Override
            public void onError() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Erro durante scan.", Toast.LENGTH_SHORT).show();
                        dismissingProgress();
                    }
                });
            }
            @Override
            public void onTimeout() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Erro: Timeout durante scan.", Toast.LENGTH_SHORT).show();
                        dismissingProgress();
                    }
                });
            }
        });
    }

    public void analisar(ScioReading reading) {
        for (int i = 0; i < modelosID.size(); i++) {
            analyzeScioReading(reading, String.valueOf(modelosID.get(i)));
        }
        tvTextoTitulo1 = String.valueOf(modelosNome.get(0));
        tvTextoTitulo2 = String.valueOf(modelosNome.get(1));
        tvTextoTitulo3 = String.valueOf(modelosNome.get(2));
        tvTextoTitulo4 = String.valueOf(modelosNome.get(3));
        tvTextoTitulo5 = String.valueOf(modelosNome.get(4));
        tvTextoTitulo6 = String.valueOf(modelosNome.get(5));

        final int MILISEGUNDOS = 17000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissingProgress();
                Intent intent = new Intent(getApplicationContext(), MostrarResultadoActivity.class);
                startActivity(intent);
            }
        }, MILISEGUNDOS);
    }

    private void analyzeScioReading(ScioReading reading, String modelId) {
        getScioCloud().analyze(reading, modelId, new ScioCloudAnalyzeCallback() {
            @Override
            public void onSuccess(final ScioModel model) {
                if (model.getAttributes() != null && !model.getAttributes().isEmpty()) {
                    List<ScioAttribute> modelAttributes = model.getAttributes();
                    ScioAttribute attribute = modelAttributes.get(0);
                    String attributeValue;
                    Log.i("SCiO Analise", "Resultado analisado com sucesso!");
                    switch (attribute.getAttributeType()) {
                        case STRING:
                            attributeValue = ((ScioStringAttribute) attribute).getValue();
                            tvTexto = attributeValue;
                            mostrarModelos.add(tvTexto);
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
                    if (model.getType().equals(ScioModel.Type.ESTIMATION)) {
                        Log.i("SCiO Analise", "Result model Value: " + attributeValue);
                    } else {
                        Log.i("SCiO Analise", "Result model Value: " + attributeValue + " (" + String.format("%.2f", attribute.getConfidence()) + ")");
                    }
                    if((mostrarModelos.get(0).equals("EVA") || mostrarModelos.get(0).equals("PBT") || mostrarModelos.get(0).equals("PC")
                            || mostrarModelos.get(0).equals("PEBD") || mostrarModelos.get(0).equals("PET") || mostrarModelos.get(0).equals("PLA")
                            || mostrarModelos.get(0).equals("PMMA") || mostrarModelos.get(0).equals("PVB")) && attribute.getConfidence() < 0.40 ){
                        confiancaModelo = true;
                    }
                } else {
                    Log.i("SCiO Analise", "Atributo vazio");
                }
            }
            @Override
            public void onError(int errorCode, String message) {
                if(mostrarModelos.size() == 0){
                    dismissingProgress();
                    Toast.makeText(getApplicationContext(), "Erro: Tempo excedido. Tente novamente.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.e("SCiO Analise", "on error: " + message);
            }
        });
    }

    private ArrayList<String> preencherModelosID() {
        ArrayList<String> dados = new ArrayList<>();
        dados.add("49b4fdf7-2311-4f5a-aa3e-b610962a33fb");
        dados.add("102058e1-36cb-4ae4-b669-f82229122fef");
        dados.add("d0d888cd-9172-4e77-a382-45cfbb761d9e");
        dados.add("1fca6279-4eb2-4911-8666-9ca25300ce5c");
        dados.add("2733aac0-e46b-4617-a7e3-ab02836451c3");
        dados.add("7146e89d-63db-4d2c-9b8f-6bd33f8a30da");
        return dados;
    }

    private ArrayList<String> preencherModelosNome() {
        ArrayList<String> dados = new ArrayList<>();
        dados.add("Sigla");
        dados.add("Nome");
        dados.add("Alongamento na Ruptura [%]");
        dados.add("Dureza");
        dados.add("Resistência a Tração [MPa]");
        dados.add("Módulo de Elasticidade [GPa]");
        return dados;
    }
}
