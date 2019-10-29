package br.maua.tcc_can03.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.consumerphysics.android.sdk.callback.cloud.ScioCloudUserCallback;
import com.consumerphysics.android.sdk.model.ScioUser;
import com.consumerphysics.android.sdk.sciosdk.ScioLoginActivity;

import java.util.Calendar;

import br.maua.tcc_can03.R;
import br.maua.tcc_can03.config.Constants;

public class MainActivity extends BaseScioActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private final static int LOGIN_ACTIVITY_RESULT = 1;
    private static final String REDIRECT_URL = "http://maua.br";
    private static final String APPLICATION_KEY = "564563e7-ce5a-4f31-8604-dcc475a7f9cd";
    // UI
    private ProgressDialog progressDialog;
    // Members
    private String deviceName;
    private String username;
    private String deviceAddress = "C4:BE:84:28:D1:19";
    private String modelId = "912ba2f9-e60b-4145-841a-11fd4a3b8829";
    private String modelName = "Sigla";
    //Data
    Calendar c = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getScioCloud().hasAccessToken() && username == null) {
            getScioUser();
        }
        updateDisplay();
        if (!isDeviceConnected() && isLoggedIn()) {
            connect(deviceAddress);
        }
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
                updateDisplay();
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
                updateDisplay();
                Log.d(TAG, "SCiO foi conectado!");
            }
        });
    }

    @Override
    public void onScioDisconnected() {
        super.onScioDisconnected();
        Log.d(TAG, "SCiO desconectado.");
        updateDisplay();
    }

    private void updateDisplay() {
        final SharedPreferences pref = getSharedPreferences();
        deviceName = pref.getString(Constants.SCIO_NAME, null);
        deviceAddress = pref.getString(Constants.SCIO_ADDRESS, "C4:BE:84:28:D1:19");
        username = pref.getString(Constants.USER_NAME, "usuário");
        modelName = pref.getString(Constants.MODEL_NAME, "Sigla");
        modelId = pref.getString(Constants.MODEL_ID, "49b4fdf7-2311-4f5a-aa3e-b610962a33fb");

        TextView olaInicioTextView = findViewById(R.id.olaInicioTextView);
        if(!isLoggedIn()) {
            username = "usuário";
        }

        int hour = c.get(Calendar.HOUR);
        int AMPM = c.get(Calendar.AM_PM);
        if(hour >= 6 && hour <= 12 && AMPM == 0) {
            Log.d("Horário", "Bom dia!" + " Horas: " + hour);
            olaInicioTextView.setText("Bom dia, " + username);
        }else if(hour >= 0 && hour < 7 && AMPM == 1){
            Log.d("Horário", "Boa tarde!" + " Horas: " + hour);
            olaInicioTextView.setText("Boa tarde, " + username);
        }else {
            Log.d("Horário", "Boa noite!" + " Horas: " + hour);
            olaInicioTextView.setText("Boa noite, " + username);
        }
    }

    private void dismissingProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void doLogin(final View view) {
        if (!isLoggedIn()) {
            final Intent intent = new Intent(this, ScioLoginActivity.class);
            intent.putExtra(ScioLoginActivity.INTENT_REDIRECT_URI, REDIRECT_URL);
            intent.putExtra(ScioLoginActivity.INTENT_APPLICATION_ID, APPLICATION_KEY);
            startActivityForResult(intent, LOGIN_ACTIVITY_RESULT);
        }
        else {
            Log.d(TAG, "Você já está logado");
            getScioUser();
        }
    }

    private void getScioUser() {
        progressDialog = ProgressDialog.show(this, "Aguarde", "Obtendo informações do usuário...", true);
        getScioCloud().getScioUser(new ScioCloudUserCallback() {
            @Override
            public void onSuccess(final ScioUser user) {
                storeUsername(user.getFirstName() + " " + user.getLastName());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Bem-vindo " + user.getFirstName() + " " + user.getLastName(), Toast.LENGTH_SHORT).show();
                        updateDisplay();
                        dismissingProgress();
                    }
                });
            }
            @Override
            public void onError(final int code, final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Erro para obter informações do usuário.", Toast.LENGTH_SHORT).show();
                        dismissingProgress();
                    }
                });
            }
        });
    }

    private void storeUsername(final String username) {
        this.username = username;
        getSharedPreferences().edit().putString(Constants.USER_NAME, username).apply();
    }

    public void doLogout(final View view) {
        if (getScioCloud() != null) {
            getScioCloud().deleteAccessToken();
            disconnect();
            Toast.makeText(getApplicationContext(), "Você não está logado", Toast.LENGTH_SHORT).show();
            updateDisplay();
        }
    }

    public void doConnect(final View view) {
        if (getScioDevice() != null && isLoggedIn()) {
            if (isDeviceConnected()) {
                Toast.makeText(getApplicationContext(), "Você já está conectado", Toast.LENGTH_SHORT).show();
                return;
            }else {
                Toast.makeText(getApplicationContext(), "Conectando...", Toast.LENGTH_SHORT).show();
                connect(deviceAddress);
            }
        }else {
            Toast.makeText(getApplicationContext(), "Faça login primeiro", Toast.LENGTH_SHORT).show();
        }
    }

    public void explorar(final View view){
        if (isDeviceConnected() && isLoggedIn()) {
            Intent intent = new Intent(this, ExplorarActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(getApplicationContext(), "Faça login ou conecte o SCIO primeiro", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
