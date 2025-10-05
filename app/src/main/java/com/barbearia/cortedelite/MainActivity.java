package com.barbearia.cortedelite; // Seu pacote

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

// Importações do Firebase e Google Sign-In
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.common.SignInButton;

// Classes do Google Sign-In (necessárias para o requisito de Login com Google)
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class MainActivity extends AppCompatActivity {

    private EditText editUsername, editPassword;
    private Button btnLogin, btnForgotPassword, btnGoToCadastro;
    private SignInButton btnGoogleLogin;

    private FirebaseAuth mAuth; // Objeto de Autenticação do Firebase
    private GoogleSignInClient mGoogleSignInClient; // Cliente para Login com Google

    // Constante para identificar a chamada de Login com Google
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Inicialização do Firebase e Google Sign-In
        mAuth = FirebaseAuth.getInstance();
        configurarGoogleSignIn();

        // 2. Inicializar Views
        editUsername = findViewById(R.id.edit_username);
        editPassword = findViewById(R.id.edit_password);
        btnLogin = findViewById(R.id.btn_login);
        btnForgotPassword = findViewById(R.id.btn_forgot_password);
        btnGoToCadastro = findViewById(R.id.btn_go_to_cadastro);
        btnGoogleLogin = findViewById(R.id.btn_google_login);

        // 3. Lógica do Botão Login (E-mail e Senha)
        btnLogin.setOnClickListener(v -> {
            realizarLogin();
        });

        // 4. Lógica de Navegação: Login para Cadastro
        btnGoToCadastro.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CadastroActivity.class);
            startActivity(intent);
        });

        // 5. Lógica para "Entrar com o Google" (Requisito Funcional)
        btnGoogleLogin.setOnClickListener(v -> {
            signInGoogle();
        });

        // 6. Lógica para "Esqueci a Senha" (Requisito Funcional)
        btnForgotPassword.setOnClickListener(v -> {
            enviarLinkRecuperacao();
        });
    }

    /**
     * Configurações de Login com Google
     * O erro "default_web_client_id" é resolvido pelo recurso R.string.default_web_client_id
     * que deve estar no arquivo strings.xml.
     */
    private void configurarGoogleSignIn() {
        // Opções necessárias para que o Google saiba que queremos usar o Firebase Authentication
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Chave do Google Services (agora no strings.xml)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    /**
     * Tenta fazer Login com E-mail e Senha usando a API do Firebase
     */
    private void realizarLogin() {
        String email = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Preencha o e-mail e a senha.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // SUCESSO! Login efetuado.
                        Toast.makeText(MainActivity.this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();
                        // TODO: Navegar para a tela de Agenda
                        // Intent intent = new Intent(MainActivity.this, AgendaActivity.class);
                        // startActivity(intent);
                        finish();
                    } else {
                        // FALHA. E-mail ou senha incorretos ou não cadastrados.
                        Toast.makeText(MainActivity.this, "Falha no Login: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Inicia o fluxo de Login com Google
     */
    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Envia o link de recuperação de senha por e-mail (Requisito)
     */
    private void enviarLinkRecuperacao() {
        String email = editUsername.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Digite seu e-mail no campo antes de usar esta função.", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Link de recuperação de senha enviado para " + email, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Erro ao enviar link: E-mail não encontrado ou inválido.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    // **NOTA IMPORTANTE:** O código que lida com o resultado do Login com Google (método onActivityResult)
    // e a integração final com o Firebase Auth deve ser adicionado para completar o Login com Google.
}