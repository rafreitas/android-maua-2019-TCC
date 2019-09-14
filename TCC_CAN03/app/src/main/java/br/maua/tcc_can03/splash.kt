package br.maua.tcc_can03

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity

class splash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        changeToLogin()
    }

    //Function change screen
    fun changeToLogin() {
        val intent = Intent(this, MainActivity::class.java)
        Handler().postDelayed({ intent.change() }, 2000)
    }

    //Function atribute screen to change
    fun Intent.change() {
        startActivity(this)
        finish()
    }
}
