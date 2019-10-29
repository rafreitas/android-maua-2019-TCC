package br.maua.tcc_can03.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import br.maua.tcc_can03.R;

public class MostrarResultadoActivity extends BaseScioActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_resultado);

        TextView tvTextoTitulo1 = findViewById(R.id.tvTextoTitulo1);
        TextView tvTextoTitulo2 = findViewById(R.id.tvTextoTitulo2);
        TextView tvTextoTitulo3 = findViewById(R.id.tvTextoTitulo3);
        TextView tvTextoTitulo4 = findViewById(R.id.tvTextoTitulo4);
        TextView tvTextoTitulo5 = findViewById(R.id.tvTextoTitulo5);
        TextView tvTextoTitulo6 = findViewById(R.id.tvTextoTitulo6);

        TextView tvValor1 = findViewById(R.id.tvValor1);
        TextView tvValor2 = findViewById(R.id.tvValor2);
        TextView tvValor3 = findViewById(R.id.tvValor3);
        TextView tvValor4 = findViewById(R.id.tvValor4);
        TextView tvValor5 = findViewById(R.id.tvValor5);
        TextView tvValor6 = findViewById(R.id.tvValor6);

        tvTextoTitulo1.setText(ExplorarActivity.tvTextoTitulo1);
        tvTextoTitulo2.setText(ExplorarActivity.tvTextoTitulo2);
        tvTextoTitulo3.setText(ExplorarActivity.tvTextoTitulo3);
        tvTextoTitulo4.setText(ExplorarActivity.tvTextoTitulo4);
        tvTextoTitulo5.setText(ExplorarActivity.tvTextoTitulo5);
        tvTextoTitulo6.setText(ExplorarActivity.tvTextoTitulo6);

        if (ExplorarActivity.mostrarModelos.size() == 1) {
            tvValor1.setText(ExplorarActivity.mostrarModelos.get(0));
            tvValor2.setText("Informação não disponível no momento");
            tvValor3.setText("Informação não disponível no momento");
            tvValor4.setText("Informação não disponível no momento");
            tvValor5.setText("Informação não disponível no momento");
            tvValor6.setText("Informação não disponível no momento");
        } else if (ExplorarActivity.mostrarModelos.size() == 2) {
            tvValor1.setText(ExplorarActivity.mostrarModelos.get(0));
            tvValor2.setText(ExplorarActivity.mostrarModelos.get(1));
            tvValor3.setText("Informação não disponível no momento");
            tvValor4.setText("Informação não disponível no momento");
            tvValor5.setText("Informação não disponível no momento");
            tvValor6.setText("Informação não disponível no momento");
        } else if (ExplorarActivity.mostrarModelos.size() == 3) {
            tvValor1.setText(ExplorarActivity.mostrarModelos.get(0));
            tvValor2.setText(ExplorarActivity.mostrarModelos.get(1));
            tvValor3.setText(ExplorarActivity.mostrarModelos.get(2));
            tvValor4.setText("Informação não disponível no momento");
            tvValor5.setText("Informação não disponível no momento");
            tvValor6.setText("Informação não disponível no momento");
        } else if (ExplorarActivity.mostrarModelos.size() == 4) {
            tvValor1.setText(ExplorarActivity.mostrarModelos.get(0));
            tvValor2.setText(ExplorarActivity.mostrarModelos.get(1));
            tvValor3.setText(ExplorarActivity.mostrarModelos.get(2));
            tvValor4.setText(ExplorarActivity.mostrarModelos.get(3));
            tvValor5.setText("Informação não disponível no momento");
            tvValor6.setText("Informação não disponível no momento");
        } else if (ExplorarActivity.mostrarModelos.size() == 5) {
            tvValor1.setText(ExplorarActivity.mostrarModelos.get(0));
            tvValor2.setText(ExplorarActivity.mostrarModelos.get(1));
            tvValor3.setText(ExplorarActivity.mostrarModelos.get(2));
            tvValor4.setText(ExplorarActivity.mostrarModelos.get(3));
            tvValor5.setText(ExplorarActivity.mostrarModelos.get(4));
            tvValor6.setText("Informação não disponível no momento");
        }else if (ExplorarActivity.mostrarModelos.size() == 6) {
            tvValor1.setText(ExplorarActivity.mostrarModelos.get(0));
            tvValor2.setText(ExplorarActivity.mostrarModelos.get(1));
            tvValor3.setText(ExplorarActivity.mostrarModelos.get(2));
            tvValor4.setText(ExplorarActivity.mostrarModelos.get(3));
            tvValor5.setText(ExplorarActivity.mostrarModelos.get(4));
            tvValor6.setText(ExplorarActivity.mostrarModelos.get(5));
        }else{
            tvValor1.setText("Informação não disponível no momento");
            tvValor2.setText("Informação não disponível no momento");
            tvValor3.setText("Informação não disponível no momento");
            tvValor4.setText("Informação não disponível no momento");
            tvValor5.setText("Informação não disponível no momento");
            tvValor6.setText("Informação não disponível no momento");
            Toast.makeText(getApplicationContext(), "Infomações não disponíveis no momento. Verifique conexão ou utilize outro modelo.", Toast.LENGTH_LONG).show();
        }

        if(ExplorarActivity.confiancaModelo == true){
            tvValor1.setText("Informação não disponível no momento");
            tvValor2.setText("Informação não disponível no momento");
            tvValor3.setText("Informação não disponível no momento");
            tvValor4.setText("Informação não disponível no momento");
            tvValor5.setText("Informação não disponível no momento");
            tvValor6.setText("Informação não disponível no momento");
            Toast.makeText(getApplicationContext(), "Infomações não disponíveis no momento. Verifique conexão ou utilize outro modelo.", Toast.LENGTH_LONG).show();
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        connect("C4:BE:84:28:D1:19");
    }
}
