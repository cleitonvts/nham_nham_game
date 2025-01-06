package com.example.game_nham_nham

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// A GameActivity é a tela principal do jogo, onde o jogador pode jogar o jogo Nham Nham.
class GameActivity : AppCompatActivity() {

    // Variáveis de estado do jogo
    private var currentPlayer = 1
    private var selectedPiece: Drawable? = null
    private var selectedPieceSize: Int = 0
    private var selectedPieceButton: ImageButton? = null
    private var invisibleDrawable: Drawable? = null
    private val board = Array(3) { Array(3) { Pair(0, 0) } } // Inicializa o tabuleiro vazio
    private var isMuted = false

    // Mapeia os botões de peças aos drawables originais
    private val originalDrawables = mutableMapOf<ImageButton, Drawable?>()

    private var isFirstPlayerSelected = false

    // Método chamado quando a activity é criada pela primeira vez
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnSoundToggle: ImageButton = findViewById(R.id.music_button)
        // Inicializa o som e define o ícone inicial
        btnSoundToggle.setImageResource(MusicManager.getCurrentIcon())

        // Gerencia o estado do som ao clicar no botão
        btnSoundToggle.setOnClickListener {
            MusicManager.toggleMute(btnSoundToggle)
        }
        if (MusicManager.mediaPlayer == null) {
            MusicManager.initialize(this, R.raw.music)
        }
        if (!MusicManager.isMuted) {
            MusicManager.play()
        }

        // Referências ao tabuleiro
        val gameBoard = arrayOf(
            findViewById<ImageButton>(R.id.bord_space_0),
            findViewById<ImageButton>(R.id.bord_space_1),
            findViewById<ImageButton>(R.id.bord_space_2),
            findViewById<ImageButton>(R.id.bord_space_3),
            findViewById<ImageButton>(R.id.bord_space_4),
            findViewById<ImageButton>(R.id.bord_space_5),
            findViewById<ImageButton>(R.id.bord_space_6),
            findViewById<ImageButton>(R.id.bord_space_7),
            findViewById<ImageButton>(R.id.bord_space_8)
        )

        // Define o drawable invisível, para esconder as peças
        invisibleDrawable = gameBoard[0].drawable

        // Referências aos elementos da tela
        val winsView: RelativeLayout = findViewById(R.id.wins_view)
        val winsMessage: TextView = findViewById(R.id.credits_message_2)
        val returnMainActivityButton: ImageButton = findViewById(R.id.restart_game)
        val restartGameButton: ImageButton = findViewById(R.id.return_main_activity)
        val newGameButton: ImageButton = findViewById(R.id.new_game_button)
        var instructionsView: RelativeLayout = findViewById(R.id.intructions_view)
        var instructionsButton: ImageButton = findViewById(R.id.intructions_button)
        var returnGameButton: ImageButton = findViewById(R.id.return_game_button)
        val returnButton: ImageButton = findViewById(R.id.return_button)

        // Referências aos botões de peças dos jogadores
        val player1Pieces = arrayOf(
            findViewById<ImageButton>(R.id.first_small_player_1),
            findViewById<ImageButton>(R.id.second_small_player_1),
            findViewById<ImageButton>(R.id.third_small_player_1),
            findViewById<ImageButton>(R.id.first_medium_player_1),
            findViewById<ImageButton>(R.id.second_medium_player_1),
            findViewById<ImageButton>(R.id.third_medium_player_1),
            findViewById<ImageButton>(R.id.first_big_player_1),
            findViewById<ImageButton>(R.id.second_big_player_1),
            findViewById<ImageButton>(R.id.third_big_player_1)
        )

        val player2Pieces = arrayOf(
            findViewById<ImageButton>(R.id.first_small_player_2),
            findViewById<ImageButton>(R.id.second_small_player_2),
            findViewById<ImageButton>(R.id.third_small_player_2),
            findViewById<ImageButton>(R.id.first_medium_player_2),
            findViewById<ImageButton>(R.id.second_medium_player_2),
            findViewById<ImageButton>(R.id.third_medium_player_2),
            findViewById<ImageButton>(R.id.first_big_player_2),
            findViewById<ImageButton>(R.id.second_big_player_2),
            findViewById<ImageButton>(R.id.third_big_player_2)
        )

        (player1Pieces + player2Pieces).forEach { button ->
            originalDrawables[button] = button.drawable

            // Configuração dos cliques nas peças
            button.setOnClickListener {
                if (button.isEnabled) {
                    if (currentPlayer == 1 && button in player1Pieces || currentPlayer == 2 && button in player2Pieces) {
                        selectedPiece = button.drawable
                        selectedPieceSize = when (button.id) {
                            R.id.first_small_player_1, R.id.second_small_player_1, R.id.third_small_player_1,
                            R.id.first_small_player_2, R.id.second_small_player_2, R.id.third_small_player_2 -> 1
                            R.id.first_medium_player_1, R.id.second_medium_player_1, R.id.third_medium_player_1,
                            R.id.first_medium_player_2, R.id.second_medium_player_2, R.id.third_medium_player_2 -> 2
                            else -> 3
                        }
                        selectedPieceButton = button
                        Toast.makeText(this, "Peça selecionada!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Não é o seu turno!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Configuração dos cliques no tabuleiro
        gameBoard.forEachIndexed { index, button ->
            button.setOnClickListener {
                if (selectedPiece != null) {
                    val (currentPlayerOnBoard, currentSizeOnBoard) = board[index / 3][index % 3]


                    if (currentPlayerOnBoard == 0 || selectedPieceSize > currentSizeOnBoard) {
                        button.setImageDrawable(selectedPiece)
                        board[index / 3][index % 3] = Pair(currentPlayer, selectedPieceSize)
                        selectedPieceButton?.isEnabled = false
                        selectedPieceButton?.setImageDrawable(invisibleDrawable)
                        selectedPiece = null
                        selectedPieceButton = null

                        // Verifica fim de jogo
                        if (checkWinCondition(currentPlayer)) {
                            showWinView(currentPlayer, winsView, winsMessage, player1Pieces + player2Pieces)
                        } else if (isBoardFull()) {
                            showDrawView(winsView, winsMessage)
                        } else {
                            // Troca o turno
                            currentPlayer = if (currentPlayer == 1) 2 else 1
                        }
                    } else {
                        Toast.makeText(this, "Você não pode colocar uma peça menor aqui!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Selecione uma peça primeiro!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        instructionsView.visibility = View.GONE

        // Configura o clique do botão "Instruções"
        instructionsButton.setOnClickListener {
            instructionsView.visibility = View.VISIBLE
        }

        // Configura o clique do botão "Voltar ao Jogo"
        returnGameButton.setOnClickListener {
            instructionsView.visibility = View.GONE
        }

        // Botão para iniciar um novo jogo mesmo que o jogo atual não tenha terminado
        newGameButton.setOnClickListener {
            restartGame(gameBoard, player1Pieces + player2Pieces, winsView)
            Toast.makeText(this, "Novo jogo iniciado!", Toast.LENGTH_SHORT).show()
        }

        // Botão para reiniciar o jogo
        restartGameButton.setOnClickListener {
            restartGame(gameBoard, player1Pieces + player2Pieces, winsView)
        }

        // Botão para retornar à MainActivity
        returnMainActivityButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Botão para retornar à MainActivity
        returnButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    // Verifica a condição de vitória
    private fun checkWinCondition(player: Int): Boolean {

        for (i in 0..2) {
            if ((board[i][0].first == player && board[i][1].first == player && board[i][2].first == player) || // Linhas
                (board[0][i].first == player && board[1][i].first == player && board[2][i].first == player)    // Colunas
            ) return true
        }

        if ((board[0][0].first == player && board[1][1].first == player && board[2][2].first == player) ||
            (board[0][2].first == player && board[1][1].first == player && board[2][0].first == player)
        ) return true

        return false
    }

    // Mostra o layout de vitória
    private fun showWinView(
        winningPlayer: Int,
        winsView: RelativeLayout,
        winsMessage: TextView,
        playerPieces: Array<ImageButton>
    ) {
        winsMessage.text = "Jogador $winningPlayer venceu!"
        winsView.visibility = View.VISIBLE

        playerPieces.forEach { it.isEnabled = false }
    }

    // Reinicia o jogo, ou seja, limpa o tabuleiro e restaura as peças dos jogadores
    private fun restartGame(
        gameBoard: Array<ImageButton>,
        playerPieces: Array<ImageButton>,
        winsView: RelativeLayout
    ) {
        gameBoard.forEach {
            it.setImageDrawable(null)
        }

        for (i in 0..2) {
            for (j in 0..2) {
                board[i][j] = Pair(0, 0)
            }
        }

        playerPieces.forEach { button ->
            button.isEnabled = true
            button.setImageDrawable(originalDrawables[button]) // Restaura o drawable original
        }

        winsView.visibility = View.GONE

        currentPlayer = 1
    }

    // Inicializa a música ao abrir a tela
    override fun onStart() {
        super.onStart()
        if (!MusicManager.isMuted) {
            MusicManager.play()
        }
    }

    // Pausa a música ao fechar a tela
    override fun onStop() {
        super.onStop()
    }

    // Verifica se o tabuleiro está cheio
    private fun isBoardFull(): Boolean {
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j].first == 0) {
                    return false
                }
            }
        }
        return true // Se não houver espaços vazios, o tabuleiro está cheio
    }

    // Mostra o layout de empate
    private fun showDrawView(winsView: RelativeLayout, winsMessage: TextView) {
        winsMessage.text = "Empate! Nenhum jogador ganhou..."
        winsView.visibility = View.VISIBLE
    }
}