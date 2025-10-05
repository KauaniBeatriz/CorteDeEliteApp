package com.barbearia.cortedelite;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

// Firebase e Firestore
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class PerfilActivity extends AppCompatActivity {

    private EditText editNome, editCpf, editTelefone, editEmail;
    private Button btnSalvar, btnLogout;

    // Instâncias do Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Sessão expirada. Faça login novamente.", Toast.LENGTH_LONG).show();
            // Redirecionar para a tela de Login
            startActivity(new Intent(PerfilActivity.this, MainActivity.class));
            finish();
            return;
        }

        userId = currentUser.getUid();

        // Inicializar Views
        editNome = findViewById(R.id.edit_perfil_nome);
        editCpf = findViewById(R.id.edit_perfil_cpf);
        editTelefone = findViewById(R.id.edit_perfil_telefone);
        editEmail = findViewById(R.id.edit_perfil_email);
        btnSalvar = findViewById(R.id.btn_salvar_perfil);
        btnLogout = findViewById(R.id.btn_logout);

        // Carrega dados na inicialização
        carregarDadosDoPerfil();

        // Lógica do botão Salvar
        btnSalvar.setOnClickListener(v -> salvarDadosDoPerfil());

        // Lógica do botão Logout
        btnLogout.setOnClickListener(v -> realizarLogout());
    }

    /**
     * Busca os dados adicionais do usuário (Nome, CPF, Telefone) no Firestore.
     */
    private void carregarDadosDoPerfil() {
        // O e-mail é fácil, vem direto do objeto de autenticação
        editEmail.setText(currentUser.getEmail());

        // Busca o restante dos dados na coleção "users"
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Preenche os campos com os dados do Firestore
                editNome.setText(documentSnapshot.getString("nome"));
                editCpf.setText(documentSnapshot.getString("cpf"));
                editTelefone.setText(documentSnapshot.getString("telefone"));
            } else {
                Toast.makeText(this, "Dados adicionais não encontrados. Preencha e salve.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Erro ao carregar dados do perfil.", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Salva ou atualiza os dados do perfil no Firestore.
     * CUMPRE O REQUISITO DE PERFIL DO USUÁRIO.
     */
    private void salvarDadosDoPerfil() {
        String nome = editNome.getText().toString().trim();
        String cpf = editCpf.getText().toString().trim();
        String telefone = editTelefone.getText().toString().trim();

        if (nome.isEmpty()) {
            Toast.makeText(this, "O nome não pode ser vazio.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> perfil = new HashMap<>();
        perfil.put("nome", nome);
        perfil.put("cpf", cpf);
        perfil.put("telefone", telefone);

        // Salva/Atualiza o documento do usuário (merge: true evita apagar outros campos)
        db.collection("users").document(userId)
                .set(perfil, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(PerfilActivity.this, "✅ Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PerfilActivity.this, "❌ Erro ao salvar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    /**
     * Realiza o Logout do usuário.
     */
    private void realizarLogout() {
        mAuth.signOut();
        Toast.makeText(this, "Sessão encerrada.", Toast.LENGTH_SHORT).show();

        // Redirecionar para a Tela de Login e limpar o histórico de atividades
        Intent intent = new Intent(PerfilActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}