package br.maua.tcc_can03.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.consumerphysics.android.sdk.callback.device.ScioDeviceCallback;
import com.consumerphysics.android.sdk.callback.device.ScioDeviceConnectHandler;
import com.consumerphysics.android.sdk.sciosdk.ScioCloud;
import com.consumerphysics.android.sdk.sciosdk.ScioDevice;
import com.consumerphysics.android.sdk.sciosdk.ScioSensor;

import br.maua.tcc_can03.config.Constants;
import br.maua.tcc_can03.interfaces.IScioDevice;
import br.maua.tcc_can03.utils.StringUtils;

public class BaseScioActivity extends AppCompatActivity implements IScioDevice {

    private ScioCloud scioCloud;
    private ScioDevice scioDevice;
    private ScioSensor scioSensor;

    protected ScioCloud getScioCloud() {
        return scioCloud;
    }

    protected ScioDevice getScioDevice() {
        return scioDevice;
    }

    public ScioSensor getScioSensor() {
        return scioSensor;
    }

    protected boolean isScioSensorAvailable() {
        return scioSensor.getId() != null;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        scioCloud = new ScioCloud(this);
        scioSensor = new ScioSensor(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final String deviceAddress = getSharedPreferences().getString(Constants.SCIO_ADDRESS, null);
        if (!StringUtils.isEmpty(deviceAddress)) {
            connect(deviceAddress);
        }
    }

    @Override
    protected void onStop() {
        // Make sure scio device is disconnected or leaks may occur
        if (scioDevice != null) {
            scioDevice.disconnect();
            scioDevice = null;
        }

        super.onStop();
    }

    protected boolean isDeviceConnected() {
        return scioDevice != null && scioDevice.isConnected();
    }

    protected boolean isLoggedIn() {
        return scioCloud != null && scioCloud.hasAccessToken();
    }

    protected SharedPreferences getSharedPreferences() {
        return getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE);
    }

    protected void connect(final String deviceAddress) {
        if (scioDevice == null) {
            scioDevice = new ScioDevice(this, deviceAddress);
            scioDevice.setScioDisconnectCallback(new ScioDeviceCallback() {
                @Override
                public void execute() {
                    onScioDisconnected();
                }
            });

            scioDevice.setButtonPressedCallback(new ScioDeviceCallback() {
                @Override
                public void execute() {
                    onScioButtonClicked();
                }
            });

            scioDevice.connect(new ScioDeviceConnectHandler() {
                @Override
                public void onConnected() {
                    onScioConnected();
                }

                @Override
                public void onConnectFailed() {
                    onScioConnectionFailed();
                }

                @Override
                public void onTimeout() {
                    onScioConnectionFailed();
                }
            });
        }
        else {
            scioDevice.reconnect();
        }
    }

    protected boolean disconnect() {
        if (scioDevice != null) {
            scioDevice.disconnect(true);
            scioDevice = null;
            return true;
        }

        return false;
    }

    @Override
    public void onScioButtonClicked() {
    }

    @Override
    public void onScioConnected() {
    }

    @Override
    public void onScioConnectionFailed() {
    }

    @Override
    public void onScioDisconnected() {
    }
}
