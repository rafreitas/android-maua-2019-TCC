package br.maua.tcc_can03

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.consumerphysics.android.sdk.sciosdk.ScioCloud
import com.consumerphysics.android.sdk.sciosdk.ScioLoginActivity

class MainActivity : AppCompatActivity() {
    private val LOGIN_ACTIVITY_RESULT = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun loginSCiO(view: View) {
        val scioCloud = ScioCloud(this)

        if (!scioCloud.hasAccessToken()) {
            val intent = Intent(this, ScioLoginActivity::class.java)
            intent.putExtra(ScioLoginActivity.INTENT_REDIRECT_URI, "http://maua.br")
            intent.putExtra(
                ScioLoginActivity.INTENT_APPLICATION_ID,
                "564563e7-ce5a-4f31-8604-dcc475a7f9cd"
            )
            Toast.makeText(applicationContext, "Iniciando login...", Toast.LENGTH_LONG).show()
            startActivityForResult(intent, LOGIN_ACTIVITY_RESULT)
        } else {
            Toast.makeText(applicationContext, "Você já está logado!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            LOGIN_ACTIVITY_RESULT -> if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Você está logado!", Toast.LENGTH_LONG).show()
            } else {
                runOnUiThread {
                    val description = data!!.getStringExtra(ScioLoginActivity.ERROR_DESCRIPTION)
                    val errorCode = data.getIntExtra(ScioLoginActivity.ERROR_CODE, -1)
                    Toast.makeText(
                        this@MainActivity,
                        "An error has occurred.\nError code: $errorCode\nDescription: $description",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    fun logoutSCiO(view: View) {
        val scioCloud = ScioCloud(this)
        scioCloud.deleteAccessToken()
        Toast.makeText(applicationContext, "Você não está mais logado", Toast.LENGTH_LONG).show()
    }

    fun procurarBluetooth(view: View) {
        val scioCloud = ScioCloud(this)
        if (scioCloud.hasAccessToken()) {
            val intent = Intent(this, Procurar_Bluetooth::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(applicationContext, "Faça login SCiO primeiro", Toast.LENGTH_LONG).show()
        }
    }

    fun escanearSCiO(view: View) {
        val scioCloud = ScioCloud(this)
        if (scioCloud.hasAccessToken()) {
            val intent = Intent(this, Escanear_Amostra::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(
                applicationContext,
                "Faça login ou conecte o SCiO primeiro",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
