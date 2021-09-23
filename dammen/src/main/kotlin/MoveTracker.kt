package board

class MoveTracker(row1: Int, col1: Int, row2: Int, col2: Int) {

    val fromRow = row1
    val fromCol = col1
    val toRow = row2
    val toCol = col2

    fun isJump(): Boolean {
        return fromRow - toRow == 2 || fromRow - toRow == -2
    }
}