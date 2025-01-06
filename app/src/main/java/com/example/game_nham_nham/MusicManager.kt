import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.widget.ImageButton
import com.example.game_nham_nham.R

// O MusicManager é responsável por gerenciar a música de fundo do jogo, permitindo que o jogador a mute e desmute.
object MusicManager {
    var mediaPlayer: MediaPlayer? = null
    var isMuted: Boolean = false
    private var currentButton: ImageButton? = null

    // Inicializa o MusicManager com o contexto e o ID do recurso de áudio
    fun initialize(context: Context, resId: Int) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, resId)
            mediaPlayer?.isLooping = true
        }
    }

    // Toca a música de fundo
    fun play() {
        if (!isMuted && mediaPlayer != null) {
            mediaPlayer?.start()
        }
    }

    // Pausa a música de fundo
    fun pause() {
        mediaPlayer?.pause()
    }

    // Alterna entre mutar e desmutar a música de fundo
    fun toggleMute(button: ImageButton) {
        isMuted = !isMuted
        currentButton = button
        if (isMuted) {
            pause()
            button.setImageResource(R.drawable.ic_muted)
        } else {
            play() // Se desmutar, toca a música
            button.setImageResource(R.drawable.ic_unmuted)
        }
    }

    // Retorna o ícone atual com base no estado de mutado
    fun getCurrentIcon(): Int {
        return if (isMuted) R.drawable.ic_muted else R.drawable.ic_unmuted
    }
}
