package board


object BoardData {

    private const val blank: Int = 0
    const val playerOne: Int = 1
    const val playerOneKing: Int = 2
    const val playerTwo: Int = 3
    const val playerTwoKing: Int = 4
    var board: Array<IntArray> = Array(0) { IntArray(0) }

    init {
        board = Array(8) { IntArray(8) }
        setUpBoard()
    }

    fun setUpBoard() {
        for (row in 0..7) {
            for (col in 0..7) {
                if (row % 2 == col % 2) {
                    if (row < 3)
                        board[row][col] = playerTwo
                    else if (row > 4)
                        board[row][col] = playerOne
                    else
                        board[row][col] = blank
                } else
                    board[row][col] = blank
            }
        }
    }

    fun pieceAt(row: Int, col: Int): Int {
        return board[row][col]
    }

    fun makeMove(move: MoveTracker) {
        makeMove(move.fromRow, move.fromCol, move.toRow, move.toCol)
    }

    fun makeMove(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int) {
        board[toRow][toCol] = board[fromRow][fromCol]
        board[fromRow][fromCol] = blank
        if (fromRow - toRow == 2 || fromRow - toRow == -2) {
            val jumpRow = (fromRow + toRow) / 2
            val jumpCol = (fromCol + toCol) / 2
            board[jumpRow][jumpCol] = blank
        }
        if (toRow == 0 && board[toRow][toCol] == playerOne) {
            board[toRow][toCol] = playerOneKing
        }
        if (toRow == 7 && board[toRow][toCol] == playerTwo) {
            board[toRow][toCol] = playerTwoKing
        }
    }

    fun getLegalMoves(player: Int): Array<MoveTracker?>? {
        if (player != playerOne && player != playerTwo)
            return null
        val playerKing: Int = if (player == playerOne) {
            playerOneKing
        } else {
            playerTwoKing
        }
        val moves: ArrayList<MoveTracker> = ArrayList()
        for (row in 0..7) {
            for (col in 0..7) {
                if (board[row][col] == player || board[row][col] == playerKing) {
                    if (canJump(player, row, col, row + 1, col + 1, row + 2, col + 2)) moves.add(
                        MoveTracker(
                            row,
                            col,
                            row + 2,
                            col + 2
                        )
                    )
                    if (canJump(player, row, col, row - 1, col + 1, row - 2, col + 2)) moves.add(
                        MoveTracker(
                            row,
                            col,
                            row - 2,
                            col + 2
                        )
                    )
                    if (canJump(player, row, col, row + 1, col - 1, row + 2, col - 2)) moves.add(
                        MoveTracker(
                            row,
                            col,
                            row + 2,
                            col - 2
                        )
                    )
                    if (canJump(player, row, col, row - 1, col - 1, row - 2, col - 2)) moves.add(
                        MoveTracker(
                            row,
                            col,
                            row - 2,
                            col - 2
                        )
                    )
                }
            }
        }
        if (moves.size == 0) {
            for (row in 0..7) {
                for (col in 0..7) {
                    if (board[row][col] == player || board[row][col] == playerKing) {
                        if (canMove(player, row, col, row + 1, col + 1)) moves.add(
                            MoveTracker(
                                row,
                                col,
                                row + 1,
                                col + 1
                            )
                        )
                        if (canMove(player, row, col, row - 1, col + 1)) moves.add(
                            MoveTracker(
                                row,
                                col,
                                row - 1,
                                col + 1
                            )
                        )
                        if (canMove(player, row, col, row + 1, col - 1)) moves.add(
                            MoveTracker(
                                row,
                                col,
                                row + 1,
                                col - 1
                            )
                        )
                        if (canMove(player, row, col, row - 1, col - 1)) moves.add(
                            MoveTracker(
                                row,
                                col,
                                row - 1,
                                col - 1
                            )
                        )
                    }
                }
            }
        }
        return if (moves.size == 0) {
            null
        } else {
            val moveArray: Array<MoveTracker?> = arrayOfNulls(moves.size)
            for (i in moves.indices) {
                moveArray[i] = moves[i]
            }
            moveArray
        }
    }

    fun getLegalJumpsFrom(player: Int, row: Int, col: Int): Array<MoveTracker?>? {
        if (player != playerOne && player != playerTwo)
            return null
        val playerKing: Int = if (player == playerOne) {
            playerOneKing
        } else {
            playerTwoKing
        }
        val moves: ArrayList<MoveTracker> = ArrayList()
        if (board[row][col] == player || board[row][col] == playerKing) {
            if (canJump(player, row, col, row + 1, col + 1, row + 2, col + 2)) moves.add(
                MoveTracker(
                    row,
                    col,
                    row + 2,
                    col + 2
                )
            )
            if (canJump(player, row, col, row - 1, col + 1, row - 2, col + 2)) moves.add(
                MoveTracker(
                    row,
                    col,
                    row - 2,
                    col + 2
                )
            )
            if (canJump(player, row, col, row + 1, col - 1, row + 2, col - 2)) moves.add(
                MoveTracker(
                    row,
                    col,
                    row + 2,
                    col - 2
                )
            )
            if (canJump(player, row, col, row - 1, col - 1, row - 2, col - 2)) moves.add(
                MoveTracker(
                    row,
                    col,
                    row - 2,
                    col - 2
                )
            )
        }
        return if (moves.size == 0) {
            null
        } else {
            val moveArray: Array<MoveTracker?> = arrayOfNulls(moves.size)
            for (i in moves.indices) {
                moveArray[i] = moves[i]
            }
            moveArray
        }
    }

    private fun canJump(player: Int, r1: Int, c1: Int, r2: Int, c2: Int, r3: Int, c3: Int): Boolean {
        if (r3 < 0 || r3 >= 8 || c3 < 0 || c3 >= 8)
            return false
        if (board[r3][c3] != blank)
            return false
        return if (player == playerOne) {
            if (board[r1][c1] == playerOne && r3 > r1)
                return false
            !(board[r2][c2] != playerTwo && board[r2][c2] != playerTwoKing)
        } else {
            if (board[r1][c1] == playerTwo && r3 < r1)
                return false
            !(board[r2][c2] != playerOne && board[r2][c2] != playerOneKing)
        }
    }

    private fun canMove(player: Int, r1: Int, c1: Int, r2: Int, c2: Int): Boolean {
        if (r2 < 0 || r2 >= 8 || c2 < 0 || c2 >= 8)
            return false
        if (board[r2][c2] != blank)
            return false
        return if (player == playerOne) {
            !(board[r1][c1] == playerOne && r2 > r1)
        } else {
            !(board[r1][c1] == playerTwo && r2 < r1)
        }
    }
}