package board

import java.awt.Color
import javax.swing.JFrame


fun main() {
    val game = JFrame()

    game.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    game.contentPane
    game.pack()
    game.setSize(345, 454)
    game.isResizable = true

    game.layout = null
    game.isVisible = true
    game.background = Color(225, 225, 225)

    val board = Board()
    game.add(board)
    game.add(board.title)
    game.add(board.newGame)
    game.add(board.message)

    board.setBounds(0, 80, 324, 324)
    board.title.setBounds(0, 0, 324, 50)
    board.newGame.setBounds(112, 50, 100, 30)
    board.message.setBounds(0, 404, 324, 30)
}
