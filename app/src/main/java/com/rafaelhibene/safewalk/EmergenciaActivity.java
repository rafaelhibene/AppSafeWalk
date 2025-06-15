package com.rafaelhibene.safewalk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EmergenciaActivity extends AppCompatActivity {

    // constante para identificar requisição de permissão de localização
    private static final int REQUEST_LOCATION_PERMISSION = 1000;

    // cliente para acessar localização do dispositivo
    private FusedLocationProviderClient fusedLocationClient;

    // componentes
    private Button btEmergencia, btEnviarLocalizacao;
    private EditText etNumero;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergencia);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializa cliente de localização
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // link do xml
        btEmergencia = findViewById(R.id.bt_emergencia);
        btEnviarLocalizacao = findViewById(R.id.bt_enviar_localizacao);
        bottomNavigationView = findViewById(R.id.bnv_bottom);
        etNumero = findViewById(R.id.et_numero);

        // configura botão de emergência para abrir discador com número 190
        btEmergencia.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:190"));
            startActivity(intent);
        });

        // configura botão para enviar localização via WhatsApp
        btEnviarLocalizacao.setOnClickListener(v -> enviarLocalizacaoWhatsApp());

        // Menu
        bottomNavigationView.setSelectedItemId(R.id.tab_emergencia);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.tab_home) {
                startActivity(new Intent(EmergenciaActivity.this, MainActivity.class));
                return true;
            } else if (id == R.id.tab_emergencia) {
                // Já está nessa activity
                return true;
            } else if (id == R.id.tab_perfil) {
                startActivity(new Intent(EmergenciaActivity.this, PerfilActivity.class));
                return true;
            }
            return false;
        });
    }

    /**
     * Envia a localização atual via WhatsApp para o contato informado pelo usuário.
     * Requisita permissão de localização se necessário.
     */
    private void enviarLocalizacaoWhatsApp() {
        // pega o contato de seguranca e guarda na variavel
        String contatoSeguranca = etNumero.getText().toString().trim();

        // verifica se o número do contato foi digitado
        if (contatoSeguranca.isEmpty()) {
            Toast.makeText(this, "Por favor, digite o número do contato de segurança.", Toast.LENGTH_SHORT).show();
            return;
        }

        // verifica permissão de localização
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // solicita permissão se ainda não foi concedida
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            // se ja tem permissao, obtem última localização conhecida
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();

                    // mensagem contendo o link para o Google Maps com localização
                    String mensagem = "SafeWalk: Estou em perigo, minha atual localização é: https://maps.google.com/?q="
                            + lat + "," + lon;

                    enviarMensagemWhatsApp(contatoSeguranca, mensagem);
                } else {
                    Toast.makeText(this, "Não foi possível obter a localização.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Erro ao obter localização: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    /**
     * Abre o WhatsApp para enviar a mensagem para o número informado.
     * @param numero Número do contato no formato internacional (ex: 5511999999999)
     * @param mensagem Texto da mensagem a ser enviada
     */
    private void enviarMensagemWhatsApp(String numero, String mensagem) {
        try {
            // remove caracteres que não são dígitos para formatar o telefone corretamente
            String telefone = numero.replaceAll("[^\\d]", "");

            // URL para enviar mensagem via WhatsApp API
            String url = "https://api.whatsapp.com/send?phone=" + telefone + "&text=" + Uri.encode(mensagem);

            // cria intent para abrir WhatsApp
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            intent.setPackage("com.whatsapp");
            startActivity(intent);

        } catch (Exception e) {
            Toast.makeText(this, "Erro ao abrir WhatsApp.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Callback da resposta da solicitação de permissão.
     * Se permissão for concedida, tenta enviar a localização.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permissão concedida: tenta enviar localização novamente
                enviarLocalizacaoWhatsApp();
            } else {
                Toast.makeText(this, "Permissão de localização negada.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
