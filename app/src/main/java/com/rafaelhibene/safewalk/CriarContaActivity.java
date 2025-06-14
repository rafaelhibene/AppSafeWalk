package com.rafaelhibene.safewalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CriarContaActivity extends AppCompatActivity {

    private EditText etNomeCriarConta, etSobrenomeCriarConta, etTelefoneCriarConta, etEmailCriarConta;
    private Button btCriarConta;
    private TextView tvJaTenhoUmaConta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_criar_conta);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //ENVIAR DADOS A OUTRA ACTIVITY
        //Pegando os dados dos EditTexts
        etNomeCriarConta = findViewById(R.id.et_nome_perfil);
        etSobrenomeCriarConta = findViewById(R.id.et_sobrenome_perfil);
        etTelefoneCriarConta = findViewById(R.id.et_telefone_perfil);
        etEmailCriarConta = findViewById(R.id.et_email_perfil);

        //Linkando o botao
        btCriarConta = findViewById(R.id.bt_criar_conta);


        btCriarConta.setOnClickListener(view -> {
            //atribui o texto do ET à uma variável
            String nomeCriarConta = etNomeCriarConta.getText().toString().trim();
            String sobrenomeCriarConta = etSobrenomeCriarConta.getText().toString().trim();
            String telefoneCriarConta = etTelefoneCriarConta.getText().toString().trim();
            String emailCriarConta = etEmailCriarConta.getText().toString().trim();

            // Verifica se algum campo está vazio
            if (nomeCriarConta.isEmpty() || sobrenomeCriarConta.isEmpty()
                    || telefoneCriarConta.isEmpty() || emailCriarConta.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verifica se o telefone está no formato (xx)9xxxx-xxxx
            if (telefoneCriarConta.length() != 11) {
                Toast.makeText(this, "Telefone inválido. Deve conter 11 dígitos com o DDD.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verifica se o e-mail contém @
            if (!emailCriarConta.contains("@")) {
                Toast.makeText(this, "E-mail inválido. Insira um e-mail válido.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Se passou nas validações envia os dados para a próxima Activity
            Intent criarContaIntent = new Intent(CriarContaActivity.this, CriarSenhaActivity.class);
            criarContaIntent.putExtra("nome", nomeCriarConta);
            criarContaIntent.putExtra("sobrenome", sobrenomeCriarConta);
            criarContaIntent.putExtra("telefone", telefoneCriarConta);
            criarContaIntent.putExtra("email", emailCriarConta);
            startActivity(criarContaIntent);
        });


        //ir para activity de login caso ja tenha uma conta
        tvJaTenhoUmaConta = findViewById(R.id.tv_ja_tenho_uma_conta);

        tvJaTenhoUmaConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CriarContaActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


    }
}