package com.rafaelhibene.safewalk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PerfilActivity extends AppCompatActivity {

    // Declaração dos componentes
    private EditText etNomePerfil, etSobrenomePerfil, etTelefonePerfil, etEmailPerfil;
    private ImageView imgPerfil;
    private Button btAlterarImagem, btSalvarPerfil;
    private BottomNavigationView bottomNavigationView;
    private ActivityResultLauncher<Intent> launcherGaleria;

    // Nome do arquivo do SharedPreferences
    private static final String PREFS_NAME = "perfil";
    private static final String KEY_IMAGEM_URI = "imagem_uri";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Links XML e java
        imgPerfil = findViewById(R.id.img_perfil);
        btAlterarImagem = findViewById(R.id.bt_alterar_imagem);
        btSalvarPerfil = findViewById(R.id.bt_salvar_perfil);  // Certifique-se que este botão está no XML
        etNomePerfil = findViewById(R.id.et_nome_perfil);
        etSobrenomePerfil = findViewById(R.id.et_sobrenome_perfil);
        etTelefonePerfil = findViewById(R.id.et_telefone_perfil);
        etEmailPerfil = findViewById(R.id.et_email_perfil);

        // SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Carrega dados dos EditTexts
        etNomePerfil.setText(prefs.getString("nome", ""));
        etSobrenomePerfil.setText(prefs.getString("sobrenome", ""));
        etTelefonePerfil.setText(prefs.getString("telefone", ""));
        etEmailPerfil.setText(prefs.getString("email", ""));

        // Carrega a imagem de perfil salva
        String imagemUriString = prefs.getString(KEY_IMAGEM_URI, null);
        if (imagemUriString != null) {
            Uri imagemUri = Uri.parse(imagemUriString);
            Glide.with(this)
                    .load(imagemUri)
                    .circleCrop()
                    .into(imgPerfil);
        }

        // Recebe dados via Intent e salva no SharedPreferences se existirem
        SharedPreferences.Editor editor = prefs.edit();

        String nomeRecebido = getIntent().getStringExtra("nome");
        if (nomeRecebido != null) {
            etNomePerfil.setText(nomeRecebido);
            editor.putString("nome", nomeRecebido);
        }

        String sobrenomeRecebido = getIntent().getStringExtra("sobrenome");
        if (sobrenomeRecebido != null) {
            etSobrenomePerfil.setText(sobrenomeRecebido);
            editor.putString("sobrenome", sobrenomeRecebido);
        }

        String telefoneRecebido = getIntent().getStringExtra("telefone");
        if (telefoneRecebido != null) {
            etTelefonePerfil.setText(telefoneRecebido);
            editor.putString("telefone", telefoneRecebido);
        }

        String emailRecebido = getIntent().getStringExtra("email");
        if (emailRecebido != null) {
            etEmailPerfil.setText(emailRecebido);
            editor.putString("email", emailRecebido);
        }

        editor.apply();

        // Configura botão de alterar imagem para abrir galeria
        launcherGaleria = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imagemSelecionada = result.getData().getData();
                        Glide.with(this)
                                .load(imagemSelecionada)
                                .circleCrop()
                                .into(imgPerfil);

                        // Salva URI da imagem no SharedPreferences
                        SharedPreferences.Editor editorUri = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                        editorUri.putString(KEY_IMAGEM_URI, imagemSelecionada.toString());
                        editorUri.apply();
                    }
                }
        );

        btAlterarImagem.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            launcherGaleria.launch(intent);
        });

        // Salva alterações nos EditTexts
        btSalvarPerfil.setOnClickListener(v -> {
            String nome = etNomePerfil.getText().toString().trim();
            String sobrenome = etSobrenomePerfil.getText().toString().trim();
            String telefone = etTelefonePerfil.getText().toString().trim();
            String email = etEmailPerfil.getText().toString().trim();

            SharedPreferences.Editor editor2 = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
            editor2.putString("nome", nome);
            editor2.putString("sobrenome", sobrenome);
            editor2.putString("telefone", telefone);
            editor2.putString("email", email);
            editor2.apply();

            Toast.makeText(this, "Perfil salvo com sucesso!", Toast.LENGTH_SHORT).show();
        });

        // Menu
        bottomNavigationView = findViewById(R.id.bnv_bottom);
        bottomNavigationView.setSelectedItemId(R.id.tab_perfil);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.tab_home) {
                startActivity(new Intent(PerfilActivity.this, MainActivity.class));
                return true;
            } else if (id == R.id.tab_emergencia) {
                startActivity(new Intent(PerfilActivity.this, EmergenciaActivity.class));
                return true;
            } else if (id == R.id.tab_perfil) {
                return true; // Já está na tela de perfil
            }
            return false;
        });
    }
}
