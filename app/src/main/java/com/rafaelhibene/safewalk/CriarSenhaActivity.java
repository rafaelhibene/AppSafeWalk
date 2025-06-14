package com.rafaelhibene.safewalk;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CriarSenhaActivity extends AppCompatActivity {

    private Button btCriarSenha;
    private EditText etCriarSenha;

    private TextView tvRequisitoTamanho, tvRequisitoEspecial, tvRequisitoNumero, tvRequisitoMaiuscula, tvRequisitoMinuscula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_criar_senha);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // recebendo dados da tela anterior
        Intent criarContaIntent = getIntent();
        String nomeCriarSenha = criarContaIntent.getStringExtra("nome");
        String sobrenomeCriarSenha = criarContaIntent.getStringExtra("sobrenome");
        String telefoneCriarSenha = criarContaIntent.getStringExtra("telefone");
        String emailCriarSenha = criarContaIntent.getStringExtra("email");

        // linkando elementos da tela
        btCriarSenha = findViewById(R.id.bt_criar_senha);
        etCriarSenha = findViewById(R.id.et_criar_senha);

        tvRequisitoTamanho = findViewById(R.id.tv_requisito_tamanho);
        tvRequisitoEspecial = findViewById(R.id.tv_requisito_especial);
        tvRequisitoNumero = findViewById(R.id.tv_requisito_numero);
        tvRequisitoMaiuscula = findViewById(R.id.tv_requisito_maiuscula);
        tvRequisitoMinuscula = findViewById(R.id.tv_requisito_minuscula);

        // escuta mudanças no campo de senha
        etCriarSenha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validarSenha(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // ação ao clicar no botão
        btCriarSenha.setOnClickListener(view -> {
            String senha = etCriarSenha.getText().toString();

            if (!senhaValida(senha)) {
                Toast.makeText(this, "A senha não atende aos requisitos.", Toast.LENGTH_SHORT).show();
                return;
            }

            // envia os dados para proxima activity
            Intent perfilIntent = new Intent(CriarSenhaActivity.this, PerfilActivity.class);
            perfilIntent.putExtra("nome", nomeCriarSenha);
            perfilIntent.putExtra("sobrenome", sobrenomeCriarSenha);
            perfilIntent.putExtra("telefone", telefoneCriarSenha);
            perfilIntent.putExtra("email", emailCriarSenha);
            perfilIntent.putExtra("senha", senha);
            startActivity(perfilIntent);
        });
    }

    // valida cada regra da senha e muda cor dos TextViews
    private void validarSenha(String senha) {
        setCor(tvRequisitoTamanho, senha.length() >= 8);
        setCor(tvRequisitoEspecial, senha.matches(".*[!@#\\$%\\^&\\*\\(\\)_\\+=\\[\\]\\{\\}\\|:;\"'<>,.?/~`-].*"));
        setCor(tvRequisitoNumero, senha.matches(".*\\d.*"));
        setCor(tvRequisitoMaiuscula, senha.matches(".*[A-Z].*"));
        setCor(tvRequisitoMinuscula, senha.matches(".*[a-z].*"));
    }

    // checa se todos os requisitos foram atendidos
    private boolean senhaValida(String senha) {
        return senha.length() >= 8 &&
                senha.matches(".*[!@#\\$%\\^&\\*\\(\\)_\\+=\\[\\]\\{\\}\\|:;\"'<>,.?/~`-].*") &&
                senha.matches(".*\\d.*") &&
                senha.matches(".*[A-Z].*") &&
                senha.matches(".*[a-z].*");
    }

    // muda cor conforme condição
    private void setCor(TextView tv, boolean valido) {
        tv.setTextColor(valido ? Color.parseColor("#2E7D32") : Color.parseColor("#D32F2F")); // verde ou vermelho
    }
}
