package board

import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.*


internal class Board : JPanel(), ActionListener, MouseListener {

    private var board: BoardData
    var gameInProgress = false
    var currentPlayer = 0
    var selectedRow = 0
    var selectedCol = 0
    private var legalMoves: Array<MoveTracker?>? = null
    var title: JLabel
    var newGame: JButton
    var message: JLabel
    private lateinit var Player1: String
    private lateinit var Player2: String

    init {
        addMouseListener(this);
        title = JLabel("Dammen!");
        title.font = Font("Serif", Font.CENTER_BASELINE, 40)
        title.horizontalAlignment = SwingConstants.CENTER
        title.foreground = Color.darkGray
        newGame = JButton("New Game")
        newGame.addActionListener(this)
        message = JLabel("", JLabel.CENTER)
        message.font = Font("Serif", Font.BOLD, 14)
        message.horizontalAlignment = SwingConstants.CENTER
        message.foreground = Color.darkGray
        board = BoardData
        getPlayersNames()

    }


    override fun actionPerformed(evt: ActionEvent) {
        val src = evt.source
        if (src == newGame)
            NewGame()
    }

    private fun NewGame() {
        board.setUpBoard()
        currentPlayer = BoardData.playerOne
        legalMoves = board.getLegalMoves(BoardData.playerOne)
        selectedRow = -1
        message.text = "It's $Player1's turn."
        gameInProgress = true
        newGame.isEnabled = true
        repaint()
    }

    private fun getPlayersNames() {
        val player1Name = JTextField("Player 1")
        val player2Name = JTextField("Player 2")

        val getNames = JPanel()
        getNames.layout = BoxLayout(getNames, BoxLayout.PAGE_AXIS)
        getNames.add(player1Name)
        getNames.add(player2Name)

        val result = JOptionPane.showConfirmDialog(
            null,
            getNames,
            "Enter Your Names!",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        )
        if (result == JOptionPane.OK_OPTION) {
            Player1 = player1Name.text
            Player2 = player2Name.text
        } else {
            Player1 = "Player 1"
            Player2 = "Player 2"
        }
    }

    fun gameOver(str: String) {
        message.text = str
        newGame.isEnabled = true
        gameInProgress = false
    }

    override fun mousePressed(evt: MouseEvent) {
        if (!gameInProgress) {
            message.text = "Start a new game."
        } else {
            val col: Int = (evt.getX() - 2) / 40
            val row: Int = (evt.getY() - 2) / 40
            if (col in 0..7 && row >= 0 && row < 8)
                ClickedSquare(row, col)
        }
    }

    fun MakeMove(move: MoveTracker) {
        board.makeMove(move) 
        if (move.isJump()) { 
            legalMoves = board.getLegalJumpsFrom(currentPlayer, move.toRow, move.toCol)
            if (legalMoves != null) { 
                if (currentPlayer == BoardData.playerOne) message.text = "$Player1, you must jump."
                else message.text = "$Player2, you must jump."
                selectedRow = move.toRow 
                selectedCol = move.toCol 
                repaint() 
                return
            }
        }
        if (currentPlayer == BoardData.playerOne) {
            currentPlayer = BoardData.playerTwo
            legalMoves = board.getLegalMoves(currentPlayer)
            if (legalMoves == null)
                gameOver("$Player1 wins!") else if (legalMoves!![0]!!.isJump())
                message.text = "$Player2, you must jump." else
                message.text = "It's $Player2's turn."
        } else {
            currentPlayer = BoardData.playerOne
            legalMoves = board.getLegalMoves(currentPlayer)
            if (legalMoves == null)
                gameOver("$Player2 wins!") else if (legalMoves!![0]!!.isJump())
                message.text = "$Player1, you must jump." else
                message.text = "It's $Player1's turn."
        }
        selectedRow = -1
        if (legalMoves != null) {
            var sameFromSquare = true
            for (i in 1 until legalMoves!!.size)
                if (legalMoves!![i]!!.fromRow != legalMoves!![0]!!.fromRow
                    || legalMoves!![i]!!.fromCol != legalMoves!![0]!!.fromCol
                ) {
                    sameFromSquare = false
                    break
                }
            if (sameFromSquare) {
                selectedRow = legalMoves!![0]!!.fromRow
                selectedCol = legalMoves!![0]!!.fromCol
            }
        }
        repaint() //repaints board
    }

    private fun ClickedSquare(row: Int, col: Int) {
        for (i in legalMoves!!.indices) {
            if (legalMoves!![i]!!.fromRow == row && legalMoves!![i]!!.fromCol == col) {
                selectedRow = row
                selectedCol = col
                if (currentPlayer == BoardData.playerOne)
                    message.text = "It's $Player1's turn." else message.text = "It's $Player2's turn."
                repaint()
                return
            }
        }
        if (selectedRow < 0) {
            message.text = "Select a piece to move."
            return
        }
        for (i in legalMoves!!.indices) {
            if (legalMoves!![i]!!.fromRow == selectedRow && legalMoves!![i]!!.fromCol == selectedCol
                && legalMoves!![i]!!.toRow == row && legalMoves!![i]!!.toCol == col) {
                MakeMove(legalMoves!![i]!!)
                return
            }
        }
        message.text = "Where do you want to move it?"
    }


    override fun paintComponent(g: Graphics) {
        g.color = Color(139, 119, 101)
        g.fillRect(0, 0, 324, 324)
        for (row in 0..7) {
            for (col in 0..7) {

                //paints squares
                if (row % 2 == col % 2) g.color = Color(139, 119, 101) else g.color = Color(238, 203, 173)
                g.fillRect(2 + col * 40, 2 + row * 40, 40, 40)
                when (board.pieceAt(row, col)) {
                    BoardData.playerOne -> {
                        g.color = Color.lightGray
                        g.fillOval(4 + col * 40, 4 + row * 40, 36, 36)
                    }
                    BoardData.playerTwo -> {
                        g.color = Color.darkGray
                        g.fillOval(4 + col * 40, 4 + row * 40, 36, 36)
                    }
                    BoardData.playerOneKing -> {
                        g.color = Color.lightGray
                        g.fillOval(4 + col * 40, 4 + row * 40, 36, 36)
                        g.color = Color.white
                        g.drawString("K", 27 + col * 40, 36 + row * 40)
                    }
                    BoardData.playerTwoKing -> {
                        g.color = Color.darkGray
                        g.fillOval(4 + col * 40, 4 + row * 40, 36, 36)
                        g.color = Color.white
                        g.drawString("K", 27 + col * 40, 36 + row * 40)
                    }
                }
            }
        }
        if (gameInProgress) {
            g.color = Color(0, 255, 0)
            for (i in legalMoves!!.indices) {
                g.drawRect(2 + legalMoves!![i]!!.fromCol * 40, 2 + legalMoves!![i]!!.fromRow * 40, 39, 39)
            }
            if (selectedRow >= 0) {
                g.color = Color.white
                g.drawRect(2 + selectedCol * 40, 2 + selectedRow * 40, 39, 39)
                g.drawRect(3 + selectedCol * 40, 3 + selectedRow * 40, 37, 37)
                g.color = Color.green
                for (i in legalMoves!!.indices) {
                    if (legalMoves!![i]!!.fromCol == selectedCol && legalMoves!![i]!!.fromRow == selectedRow) g.drawRect(
                        2 + legalMoves!![i]!!.toCol * 40,
                        2 + legalMoves!![i]!!.toRow * 40,
                        39,
                        39
                    )
                }
            }
        }
    }

    override fun mouseEntered(evt: MouseEvent) {}
    override fun mouseClicked(evt: MouseEvent) {}
    override fun mouseReleased(evt: MouseEvent) {}
    override fun mouseExited(evt: MouseEvent) {}
}