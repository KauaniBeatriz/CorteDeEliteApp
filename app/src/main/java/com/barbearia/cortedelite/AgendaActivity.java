package com.barbearia.cortedelite;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;

public class AgendaActivity extends AppCompatActivity {

    private Button btnAgendarNovoHorario, btnMeuPerfil, btnMeusAgendamentos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        // Inicializar Views
        btnAgendarNovoHorario = findViewById(R.id.btn_agendar_novo_horario);
        btnMeuPerfil = findViewById(R.id.btn_meu_perfil);
        btnMeusAgendamentos = findViewById(R.id.btn_meus_agendamentos);

        // 1. Navegação para Agendar Novo Horário
        btnAgendarNovoHorario.setOnClickListener(v -> {
            Intent intent = new Intent(AgendaActivity.this, AgendamentoActivity.class);
            startActivity(intent);
        });

        // 2. Navegação para Meu Perfil
        btnMeuPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(AgendaActivity.this, PerfilActivity.class);
            startActivity(intent);

        });

        // 3. Navegação para Histórico de Agendamentos
        btnMeusAgendamentos.setOnClickListener(v -> {
            Intent intent = new Intent(AgendaActivity.this, HistoricoActivity.class);
            startActivity(intent);
        });
    }
}