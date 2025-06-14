package com.rafaelhibene.safewalk;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.result.ActivityResultLauncher;


import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PerfilActivity extends AppCompatActivity {

    //declarações
    private EditText etNomePerfil, etSobrenomePerfil, etTelefonePerfil, etEmailPerfil;
    private static final int REQUEST_CODE_GALERIA = 100;
    private ImageView imgPerfil;
    private ActivityResultLauncher<Intent> launcherGaleria;
    private Button btAlterarImagem;
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        //link do xml com java
        imgPerfil = findViewById(R.id.img_perfil);
        btAlterarImagem = findViewById(R.id.bt_alterar_imagem);

        // inicializa o launcher da galeria
        launcherGaleria = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imagemSelecionada = result.getData().getData();
                        Glide.with(this)
                                .load(imagemSelecionada) // URI da imagem selecionada
                                .circleCrop()             // Faz o recorte circular
                                .into(imgPerfil);         // Define a imagem no ImageView
                    }
                }
        );

        // botão para alterar imagem
        btAlterarImagem.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            launcherGaleria.launch(intent);
        });

        //recebe os dados da activity criar senha e preenche os EditTexts
        etNomePerfil = findViewById(R.id.et_nome_perfil);
        String nomeRecebido = getIntent().getStringExtra("nome");
        if (nomeRecebido != null) {
            etNomePerfil.setText(nomeRecebido);
        }

        etSobrenomePerfil = findViewById(R.id.et_sobrenome_perfil);
        String sobrenomeRecebido = getIntent().getStringExtra("sobrenome");
        if (sobrenomeRecebido != null) {
            etSobrenomePerfil.setText(sobrenomeRecebido);
        }

        etTelefonePerfil = findViewById(R.id.et_telefone_perfil);
        String telefoneRecebido = getIntent().getStringExtra("telefone");
        if (telefoneRecebido != null) {
            etTelefonePerfil.setText(telefoneRecebido);
        }

        etEmailPerfil = findViewById(R.id.et_email_perfil);
        String emailRecebido = getIntent().getStringExtra("email");
        if (emailRecebido != null) {
            etEmailPerfil.setText(emailRecebido);
        }



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
                // já está no perfil
                return true;
            }
            return false;
        });
    }
}

