import android.content.Context
import android.media.MediaPlayer
import android.widget.ImageButton
import com.example.game_nham_nham.R

object MusicManager {
    var mediaPlayer: MediaPlayer? = null
    var isMuted: Boolean = false
    private var currentButton: ImageButton? = null

    fun initialize(context: Context, resId: Int) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, resId)
            mediaPlayer?.isLooping = true
        }
    }

    fun play() {
        if (!isMuted && mediaPlayer != null) {
            mediaPlayer?.start()
        }
    }

    fun pause() {
        mediaPlayer?.pause()
    }

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

    fun getCurrentIcon(): Int {
        return if (isMuted) R.drawable.ic_muted else R.drawable.ic_unmuted
    }
}
