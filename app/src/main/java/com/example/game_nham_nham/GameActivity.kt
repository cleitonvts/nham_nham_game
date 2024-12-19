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

class GameActivity : AppCompatActivity() {

    private var currentPlayer = 1 // Jogador atual (1 ou 2)
    private var selectedPiece: Drawable? = null // Peça selecionada
    private var selectedPieceSize: Int = 0 // Tamanho da peça selecionada
    private var selectedPieceButton: ImageButton? = null // Botão da peça selecionada
    private var invisibleDrawable: Drawable? = null // Drawable invisível
    private val board = Array(3) { Array(3) { Pair(0, 0) } } // Tabuleiro: (jogador, tamanho da peça)
    private var isMuted = false

    // Map para armazenar os drawables originais das peças
    private val originalDrawables = mutableMapOf<ImageButton, Drawable?>()

    private var isFirstPlayerSelected = false

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

        // Define o ícone de acordo com o estado atual
        btnSoundToggle.setImageResource(MusicManager.getCurrentIcon())

        // Alterna o estado do som ao clicar no botão
        btnSoundToggle.setOnClickListener {
            MusicManager.toggleMute(btnSoundToggle)
        }

        // Inicializa o MediaPlayer, mas só toca a música se o MediaPlayer não estiver inicializado
        if (MusicManager.mediaPlayer == null) {
            MusicManager.initialize(this, R.raw.music) // Substitua com o arquivo de música adequado
        }

        // Se a música não estiver mudo, começa a tocar
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

        // Inicializa o drawable invisível
        invisibleDrawable = gameBoard[0].drawable

        // Referência ao layout de vitória
        val winsView: RelativeLayout = findViewById(R.id.wins_view)
        val winsMessage: TextView = findViewById(R.id.credits_message_2)
        val returnMainActivityButton: ImageButton = findViewById(R.id.restart_game)
        val restartGameButton: ImageButton = findViewById(R.id.return_main_activity)
        val newGameButton: ImageButton = findViewById(R.id.new_game_button)
        var instructionsView: RelativeLayout = findViewById(R.id.intructions_view)
        var instructionsButton: ImageButton = findViewById(R.id.intructions_button)
        var returnGameButton: ImageButton = findViewById(R.id.return_game_button)
        val returnButton: ImageButton = findViewById(R.id.return_button)

        // Botões de peças dos jogadores
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

        // Salva os drawables originais das peças
        (player1Pieces + player2Pieces).forEach { button ->
            originalDrawables[button] = button.drawable

            // Configura evento de clique para selecionar a peça
            button.setOnClickListener {
                if (button.isEnabled) { // Só permite selecionar se o botão estiver ativo
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
                if (selectedPiece != null) { // Verifica se uma peça foi selecionada
                    val (currentPlayerOnBoard, currentSizeOnBoard) = board[index / 3][index % 3]

                    // Se o espaço estiver vazio ou a peça selecionada for maior
                    if (currentPlayerOnBoard == 0 || selectedPieceSize > currentSizeOnBoard) {
                        button.setImageDrawable(selectedPiece) // Atualiza o ícone no tabuleiro
                        board[index / 3][index % 3] = Pair(currentPlayer, selectedPieceSize) // Atualiza o tabuleiro
                        selectedPieceButton?.isEnabled = false // Desativa a peça usada
                        selectedPieceButton?.setImageDrawable(invisibleDrawable) // Torna a peça invisível
                        selectedPiece = null // Limpa a peça selecionada
                        selectedPieceButton = null // Limpa o botão selecionado

                        // Verifica vitória
                        if (checkWinCondition(currentPlayer)) {
                            showWinView(currentPlayer, winsView, winsMessage, player1Pieces + player2Pieces)
                        } else if (isBoardFull()) { // Verifica se o tabuleiro está cheio e não há vencedor
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
            instructionsView.visibility = View.VISIBLE // Mostra a visualização de instruções
        }

        // Configura o clique do botão "Voltar ao Jogo"
        returnGameButton.setOnClickListener {
            instructionsView.visibility = View.GONE // Esconde a visualização de instruções
        }

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

        returnButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    // Verifica a condição de vitória
    private fun checkWinCondition(player: Int): Boolean {
        // Verifica linhas, colunas e diagonais
        for (i in 0..2) {
            if ((board[i][0].first == player && board[i][1].first == player && board[i][2].first == player) || // Linhas
                (board[0][i].first == player && board[1][i].first == player && board[2][i].first == player)    // Colunas
            ) return true
        }

        // Verifica diagonais
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

        // Desativa todos os botões de peças
        playerPieces.forEach { it.isEnabled = false }
    }

    // Reinicia o jogo
    private fun restartGame(
        gameBoard: Array<ImageButton>,
        playerPieces: Array<ImageButton>,
        winsView: RelativeLayout
    ) {
        // Limpa o tabuleiro
        gameBoard.forEach {
            it.setImageDrawable(null)
        }

        // Reinicia o tabuleiro lógico
        for (i in 0..2) {
            for (j in 0..2) {
                board[i][j] = Pair(0, 0)
            }
        }

        // Restaura as peças dos jogadores
        playerPieces.forEach { button ->
            button.isEnabled = true
            button.setImageDrawable(originalDrawables[button]) // Restaura o drawable original
        }

        // Esconde a tela de vitória
        winsView.visibility = View.GONE

        // Reseta o jogador inicial
        currentPlayer = 1
    }
    override fun onStart() {
        super.onStart()
        // Garantir que a música continue tocando ao iniciar a activity
        if (!MusicManager.isMuted) {
            MusicManager.play() // Continua tocando se não estiver mudo
        }
    }

    override fun onStop() {
        super.onStop()
        // Libera o MediaPlayer quando a activity for parada
    }

    private fun isBoardFull(): Boolean {
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j].first == 0) { // Se algum espaço estiver vazio (0), o tabuleiro não está cheio
                    return false
                }
            }
        }
        return true // Se não houver espaços vazios, o tabuleiro está cheio
    }


    private fun showDrawView(winsView: RelativeLayout, winsMessage: TextView) {
        winsMessage.text = "Empate! Nenhum jogador ganhou..."
        winsView.visibility = View.VISIBLE
    }
}