package com.barbearia.cortedelite;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.app.DatePickerDialog;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.RadioGroup;
import android.widget.Toast;

// Importações do Firebase e Java
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AgendamentoActivity extends AppCompatActivity {

    private EditText editData;
    private Spinner spinnerBarbeiro, spinnerHorario;
    private RadioGroup radioGroupServico;
    private Button btnConfirmarAgendamento;

    // Instâncias do Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendamento);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inicializar Views (Conexão do Java com o XML)
        editData = findViewById(R.id.edit_data);
        spinnerBarbeiro = findViewById(R.id.spinner_barbeiro);
        spinnerHorario = findViewById(R.id.spinner_horario);
        radioGroupServico = findViewById(R.id.radio_group_servico);
        btnConfirmarAgendamento = findViewById(R.id.btn_confirmar_agendamento);

        // POPULAR SPINNERS (Simulação de dados)
        popularSpinnerBarbeiros();
        popularSpinnerHorarios(new String[]{"09:00", "10:30", "11:00", "13:30"});

        // SELETOR DE DATA
        editData.setOnClickListener(v -> mostrarDatePickerDialog());

        // LÓGICA DE AGENDAMENTO (Comunicação com API)
        btnConfirmarAgendamento.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() != null) {
                salvarAgendamentoNoFirestore();
            } else {
                Toast.makeText(this, "Erro: Usuário não autenticado. Faça login novamente.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- Métodos de UI e Seleção ---

    private void popularSpinnerBarbeiros() {
        String[] barbeiros = new String[]{"Selecione o Barbeiro", "José (Barbeiro 1)", "Pedro (Barbeiro 2)", "Lucas (Barbeiro 3)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, barbeiros);
        spinnerBarbeiro.setAdapter(adapter);
    }

    private void popularSpinnerHorarios(String[] horarios) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, horarios);
        spinnerHorario.setAdapter(adapter);
    }

    private void mostrarDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String dataSelecionada = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year1);
                    editData.setText(dataSelecionada);
                    // TODO: Chamar aqui a função que buscaria horários disponíveis NO FIREBASE.
                }, year, month, day);
        datePickerDialog.show();
    }

    // --- Métodos de API e Notificação ---

    private void salvarAgendamentoNoFirestore() {
        // 1. Coletar dados da UI
        String barbeiro = spinnerBarbeiro.getSelectedItem().toString();
        String data = editData.getText().toString();
        String horario = spinnerHorario.getSelectedItem().toString();

        int selectedId = radioGroupServico.getCheckedRadioButtonId();

        if (editData.getText().toString().isEmpty() || barbeiro.equals("Selecione o Barbeiro") || selectedId == -1) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios para agendar.", Toast.LENGTH_SHORT).show();
            return;
        }

        Button selectedRadioButton = findViewById(selectedId);
        String servico = selectedRadioButton.getText().toString();

        // 2. Criar um objeto Map para enviar ao Firestore
        Map<String, Object> agendamento = new HashMap<>();
        agendamento.put("userId", mAuth.getCurrentUser().getUid());
        agendamento.put("barbeiro", barbeiro);
        agendamento.put("servico", servico);
        agendamento.put("data", data);
        agendamento.put("horario", horario);
        agendamento.put("status", "Agendado");

        // 3. Enviar para o Firestore (Coleção "agendamentos")
        db.collection("agendamentos")
                .add(agendamento)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AgendamentoActivity.this, "✅ Agendamento Confirmado! ", Toast.LENGTH_LONG).show();

                    // **** REQUISITO CUMPRIDO: AGENDAMENTO DE NOTIFICAÇÃO ****
                    agendarNotificacao(data, horario, barbeiro);

                    finish(); // Retorna para a tela de Agenda
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AgendamentoActivity.this, "❌ Erro ao agendar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    /**
     * Agendar o AlarmManager para disparar a notificação 30 minutos antes do horário.
     * CUMPRE O REQUISITO DE NOTIFICAÇÕES.
     */
    private void agendarNotificacao(String data, String horario, String barbeiro) {
        try {
            // Formato completo: "dia/mes/ano hora:minuto"
            String dataHoraStr = data + " " + horario;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date dataAgendamento = sdf.parse(dataHoraStr);

            long tempoAgendamentoMs = dataAgendamento.getTime();

            // Define o alarme para 30 minutos ANTES do agendamento
            long trintaMinutosAntes = tempoAgendamentoMs - (30 * 60 * 1000);

            // Configura o Intent que será enviado ao NotificationReceiver
            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.putExtra("BARBEIRO", barbeiro);
            intent.putExtra("HORARIO", horario);

            // Usa o tempo atual (tempo em milissegundos) como requestCode para garantir unicidade
            int requestCode = (int) System.currentTimeMillis();

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
            );

            // Configura o AlarmManager
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            if (alarmManager != null) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        trintaMinutosAntes,
                        pendingIntent
                );
                Toast.makeText(this, "Lembrete agendado para 30min antes!", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao configurar lembrete. Verifique o formato da data.", Toast.LENGTH_LONG).show();
        }
    }
}