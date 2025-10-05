package com.barbearia.cortedelite; // Seu pacote

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

// Importações do Firebase
import com.google.firebase.auth.FirebaseAuth;

public class CadastroActivity extends AppCompatActivity {

    private EditText editNome, editCpf, editTelefone, editEmailPrincipal,
            editEmailConfirmacao, editSenha, editSenhaConfirmacao;
    private Button btnCadastrar;

    // Declaração do objeto Firebase Authentication
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        // --- INICIALIZAÇÃO DE COMPONENTES ---
        // Inicializa o Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Inicializar Views (Conexão do Java com o XML)
        editNome = findViewById(R.id.edit_nome);
        editCpf = findViewById(R.id.edit_cpf);
        editTelefone = findViewById(R.id.edit_telefone);
        editEmailPrincipal = findViewById(R.id.edit_email_principal);
        editEmailConfirmacao = findViewById(R.id.edit_email_confirmacao);
        editSenha = findViewById(R.id.edit_senha);
        editSenhaConfirmacao = findViewById(R.id.edit_senha_confirmacao);
        btnCadastrar = findViewById(R.id.btn_cadastrar);

        // --- LÓGICA DO BOTÃO CADASTRAR ---
        btnCadastrar.setOnClickListener(v -> {
            if (validarCampos()) {
                criarNovoUsuario();
            }
        });

        // Se houver um botão "LOG IN" no seu mockup de cadastro,
        // você deve adicionar a lógica de navegação de volta para a MainActivity (Login) aqui.
    }

    /**
     * Função para criar o usuário no servidor do Firebase
     */
    private void criarNovoUsuario() {
        String email = editEmailPrincipal.getText().toString().trim();
        String senha = editSenha.getText().toString();

        // ----------------------------------------------------
        // LÓGICA PARA USUÁRIO DE TESTE OFFLINE: admin / senha admin
        // ----------------------------------------------------
        if (email.equals("admin") && senha.equals("senha admin")) {
            Toast.makeText(CadastroActivity.this, "✅ Cadastro OFFLINE BEM-SUCEDIDO! (Usuário de Teste)", Toast.LENGTH_LONG).show();

            // Simular navegação para a tela principal (Agenda)
            // Intent intent = new Intent(CadastroActivity.this, AgendaActivity.class);
            // startActivity(intent);

            finish(); // Fecha a tela de cadastro
            return; // Sai da função, ignorando a chamada ao Firebase
        }
        // ----------------------------------------------------


        // Usa a API do Firebase para tentar criar o novo usuário
        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // SUCESSO! O usuário foi criado no servidor do Firebase.
                        Toast.makeText(CadastroActivity.this, "✅ Cadastro realizado com sucesso!", Toast.LENGTH_LONG).show();

                        // TODO: Aqui, você salvaria os dados adicionais (Nome, CPF, Telefone) no Firestore.

                        // Agora, você deve levar o usuário para a tela principal (Agenda)
                        // Intent intent = new Intent(CadastroActivity.this, AgendaActivity.class);
                        // startActivity(intent);

                        finish(); // Fecha a tela de cadastro
                    } else {
                        // FALHA: Exibe a mensagem de erro que veio do servidor (API)
                        // Ex: "O e-mail já está em uso", "A senha é muito fraca"
                        Toast.makeText(CadastroActivity.this, "❌ Falha no cadastro: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }


    /**
     * Validação local dos campos (antes de enviar para o servidor)
     */
    private boolean validarCampos() {
        String nome = editNome.getText().toString().trim();
        String email = editEmailPrincipal.getText().toString().trim();
        String emailConf = editEmailConfirmacao.getText().toString().trim();
        String senha = editSenha.getText().toString();
        String senhaConf = editSenhaConfirmacao.getText().toString();

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!email.equals(emailConf)) {
            Toast.makeText(this, "O e-mail e a confirmação não coincidem.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!senha.equals(senhaConf)) {
            Toast.makeText(this, "A senha e a confirmação não coincidem.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (senha.length() < 6) {
            Toast.makeText(this, "A senha deve ter no mínimo 6 caracteres.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Se passar por todas as verificações
        return true;
    }
}