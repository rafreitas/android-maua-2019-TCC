package br.maua.tcc_can03;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.text.DecimalFormat;

public class MostrarResultadoActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_resultado);

        TextView tvTextoTitulo1 = findViewById(R.id.tvTextoTitulo1);
        TextView tvTextoTitulo2 = findViewById(R.id.tvTextoTitulo2);
        TextView tvValor1 = findViewById(R.id.tvValor1);
        TextView tvValor2 = findViewById(R.id.tvValor2);

        tvValor1.setText(Escanear_Amostra.tvTexto);

        double aDouble = Double.parseDouble(Escanear_Amostra.tvValor);
        DecimalFormat df = new DecimalFormat("#,###.00");
        tvValor2.setText(df.format(aDouble));

        tvTextoTitulo1.setText(Escanear_Amostra.tvTextoTitulo1);
        tvTextoTitulo2.setText(Escanear_Amostra.tvTextoTitulo2);
    }
}
