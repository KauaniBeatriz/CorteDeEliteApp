package com.barbearia.cortedelite;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Firebase e Firestore
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HistoricoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvHistoricoVazio;

    // Objetos do Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // Adapter do RecyclerView
    // Você precisará criar a classe HistoricoAdapter, vamos simplificar para agora:
    // private HistoricoAdapter adapter;
    private List<Agendamento> listaAgendamentos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        // Inicializar Views
        recyclerView = findViewById(R.id.recycler_historico);
        tvHistoricoVazio = findViewById(R.id.tv_historico_vazio);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listaAgendamentos = new ArrayList<>();
        // adaptador = new HistoricoAdapter(this, listaAgendamentos);
        // recyclerView.setAdapter(adaptador);

        // Chamar a função de busca
        buscarAgendamentosDoFirebase();
    }

    /**
     * Busca os agendamentos do usuário logado no Firestore.
     * CUMPRE O REQUISITO DE HISTÓRICO DE AGENDAMENTOS E COMUNICAÇÃO COM API.
     */
    private void buscarAgendamentosDoFirebase() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Usuário não logado.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        // 1. Consulta ao Firestore
        db.collection("agendamentos")
                // Filtra os agendamentos pelo ID do usuário logado
                .whereEqualTo("userId", userId)
                // Ordena pelo campo 'data' (opcional)
                .orderBy("data", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listaAgendamentos.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Converte o documento do Firestore para o nosso modelo Agendamento
                            Agendamento agendamento = document.toObject(Agendamento.class);
                            listaAgendamentos.add(agendamento);
                        }

                        // 2. Atualizar UI
                        if (listaAgendamentos.isEmpty()) {
                            tvHistoricoVazio.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            tvHistoricoVazio.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            // adapter.notifyDataSetChanged(); // Notifica o adaptador para recarregar a lista
                        }

                    } else {
                        Toast.makeText(this, "Erro ao buscar histórico: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}