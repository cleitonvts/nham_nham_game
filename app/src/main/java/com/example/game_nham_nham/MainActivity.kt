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

        // Configura o listener para alternar o estado do som
        btnSoundToggle.setOnClickListener {
            MusicManager.toggleMute(btnSoundToggle)
        }

        val enterButton: ImageButton = findViewById(R.id.enter_button)
        val optionButton: ImageButton = findViewById(R.id.option_button)
        val creditsButton: ImageButton = findViewById(R.id.credits_button)
        val creditsView: View = findViewById(R.id.credits_view)
        val closeCreditsButton: Button = findViewById(R.id.close_credits_button)

        enterButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }
        optionButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Aviso")
            builder.setMessage("Não há opções disponíveis no momento.")
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }

        creditsButton.setOnClickListener {
            creditsView.visibility = View.VISIBLE
        }
        closeCreditsButton.setOnClickListener {
            creditsView.visibility = View.GONE
        }
    }

    override fun onStart() {
        super.onStart()
        // Inicializa o MediaPlayer ao entrar na Activity, usando o MusicManager
        MusicManager.initialize(this, R.raw.music) // Substitua com o arquivo de música adequado
        MusicManager.play()
    }

    override fun onStop() {
        super.onStop()
        // Libera o MediaPlayer ao sair da Activity, usando o MusicManager
    }
}