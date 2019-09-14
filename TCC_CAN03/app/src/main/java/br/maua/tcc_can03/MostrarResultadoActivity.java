package br.maua.tcc_can03;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MostrarResultadoActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_resultado);

        TextView tvTextoTitulo1 = findViewById(R.id.tvTextoTitulo1);
        TextView tvTextoTitulo2 = findViewById(R.id.tvTextoTitulo2);
        TextView tvTextoTitulo3 = findViewById(R.id.tvTextoTitulo3);
        TextView tvValor1 = findViewById(R.id.tvValor1);
        TextView tvValor2 = findViewById(R.id.tvValor2);
        TextView tvValor3 = findViewById(R.id.tvValor3);

        tvValor1.setText(Escanear_Amostra.tvTexto);
        tvValor2.setText(Escanear_Amostra.tvValor);
        tvValor3.setText(Escanear_Amostra.tvData);

    }
}
