package com.rafaelhibene.safewalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    //declarações
    private EditText etEmail, etSenha;
    private Button btEntrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.logoImage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //link do xml com java
        etEmail = findViewById(R.id.et_email_perfil);
        etSenha = findViewById(R.id.et_senha);
        btEntrar = findViewById(R.id.bt_entrar);

        // condig do botao de entrar
        btEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // pega os dados inseridos no ET
                String emailInserido = etEmail.getText().toString();
                String senhaInserida = etSenha.getText().toString();

                //condicionais de teste para ver se funciona
                if (emailInserido.equals("teste") && senhaInserida.equals("1234")) {
                    //chama a segunda activity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); //pra encerrar a activity de login
                } else {
                    etEmail.setError("Usuário ou senha inválidos");
                    etSenha.setError("Usuário ou senha inválidos");
                }
            }
        });

    } //FIM DO ONCREATE
}