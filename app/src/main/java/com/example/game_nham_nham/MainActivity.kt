package com.example.game_nham_nham

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// A Main Activity é lógica a tela inicial do jogo, onde o jogador pode acessar o jogo, as opções e os créditos.
class MainActivity : AppCompatActivity() {
    private var isMuted = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnSoundToggle: ImageButton = findViewById(R.id.music_button)

        // Inicializa o som e define o ícone inicial
        btnSoundToggle.setImageResource(MusicManager.getCurrentIcon())
        // Define o comportamento do botão de som, que alterna entre mutar e desmutar a música
        btnSoundToggle.setOnClickListener {
            MusicManager.toggleMute(btnSoundToggle)
        }

        // Inicializa os botões da tela
        val enterButton: ImageButton = findViewById(R.id.enter_button)
        val optionButton: ImageButton = findViewById(R.id.option_button)
        val creditsButton: ImageButton = findViewById(R.id.credits_button)
        val creditsView: View = findViewById(R.id.credits_view)
        val closeCreditsButton: Button = findViewById(R.id.close_credits_button)

        // Define o comportamento do botão de entrar, que leva o jogador para a tela de jogo, GameActivity
        enterButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }
        // Define o comportamento do botão de opções, que ainda não está implementado e exibe um diálogo de aviso
        optionButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Aviso")
            builder.setMessage("Não há opções disponíveis no momento.")
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }
        // Define o comportamento do botão de créditos, que exibe a view de créditos
        creditsButton.setOnClickListener {
            creditsView.visibility = View.VISIBLE
        }
        closeCreditsButton.setOnClickListener {
            creditsView.visibility = View.GONE
        }
    }

    // Inicializa a música ao abrir a tela
    override fun onStart() {
        super.onStart()
        MusicManager.initialize(this, R.raw.music)
        MusicManager.play()
    }

    // Pausa a música ao fechar a tela
    override fun onStop() {
        super.onStop()
    }
}