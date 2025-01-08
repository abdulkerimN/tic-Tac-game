import java.util.*;

class TicTacToe {
    static class Move {
        int row;
        int col;
        char player;

        Move(int row, int col, char player) {
            this.row = row;
            this.col = col;
            this.player = player;
        }
    }

    private static final int MAX_UNDOS = 2;
    private static final int HISTORY_SIZE = 5; // Number of moves to keep in the history queue

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean playAgain;

        do {
            char[][] board = initializeBoard();
            LinkedList<Move> moveHistory = new LinkedList<>();
            Queue<Move> recentMoves = new LinkedList<>(); // Queue to store recent moves
            Stack<Move> undoStackX = new Stack<>();
            Stack<Move> undoStackO = new Stack<>();
            int undoCountX = MAX_UNDOS;
            int undoCountO = MAX_UNDOS;
            char player = 'X';
            boolean gameOver = false;

            while (!gameOver) {
                printBoard(board);
                System.out.print("Player " + player + " enter row and column (0, 1, or 2), 'u' to undo, or 'v' to view recent moves: ");
                String input = scanner.next();

                if (input.equalsIgnoreCase("u")) {
                    gameOver = handleUndo(player, board, moveHistory, recentMoves, undoStackX, undoStackO, undoCountX, undoCountO);
                    if (player == 'X') undoCountX--; else undoCountO--;
                    player = switchPlayer(player);
                } else if (input.equalsIgnoreCase("v")) {
                    displayRecentMoves(recentMoves);
                } else {
                    gameOver = processMove(input, scanner, board, moveHistory, recentMoves, undoStackX, undoStackO, player);
                    if (!gameOver) player = switchPlayer(player);
                }
            }

            System.out.println("Game Over! Here is the move history:");
            displayMoveHistory(moveHistory);
            System.out.print("Do you want to play again? (yes/no): ");
            playAgain = scanner.next().equalsIgnoreCase("yes");
        } while (playAgain);

        scanner.close();
    }


// Below are the multiple methods used
    
    private static char[][] initializeBoard() {
        char[][] board = new char[3][3];
        for (int row = 0; row < board.length; row++) {
            Arrays.fill(board[row], ' ');
        }
        return board;
    }

    
    private static boolean handleUndo(char player, char[][] board, LinkedList<Move> moveHistory, Queue<Move> recentMoves, Stack<Move> undoStackX, Stack<Move> undoStackO, int undoCountX, int undoCountO) {
        if (player == 'X' && !undoStackX.isEmpty() && undoCountX > 0) {
            undoMove(board, moveHistory, recentMoves, undoStackX);
            System.out.println("Undo successful. Player X has " + (undoCountX - 1) + " undos left.");
            return false;
        } else if (player == 'O' && !undoStackO.isEmpty() && undoCountO > 0) {
            undoMove(board, moveHistory, recentMoves, undoStackO);
            System.out.println("Undo successful. Player O has " + (undoCountO - 1) + " undos left.");
            return false;
        } else {
            System.out.println("No moves to undo or no undos left!");
            return false;
        }
    }

    
    private static void undoMove(char[][] board, LinkedList<Move> moveHistory, Queue<Move> recentMoves, Stack<Move> undoStack) {
        Move lastMove = undoStack.pop();
        board[lastMove.row][lastMove.col] = ' ';
        moveHistory.removeLast();
        recentMoves.remove(lastMove);
    }

    
    private static boolean processMove(String input, Scanner scanner, char[][] board, LinkedList<Move> moveHistory, Queue<Move> recentMoves, Stack<Move> undoStackX, Stack<Move> undoStackO, char player) {
        try {
            int row = Integer.parseInt(input);
            int col = scanner.nextInt();
            if (isValidMove(row, col, board)) {
                placeMove(board, row, col, player, moveHistory, recentMoves, undoStackX, undoStackO);
                return checkGameOver(board, player);
            } else {
                System.out.println("Invalid move. Try again!");
                return false;
            }
        } catch (NumberFormatException | InputMismatchException e) {
            System.out.println("Invalid input. Enter row and column as numbers.");
            scanner.nextLine(); // Clear the invalid input
            return false;
        }
    }

    
    private static boolean isValidMove(int row, int col, char[][] board) {
        return row >= 0 && row < 3 && col >= 0 && col < 3 && board[row][col] == ' ';
    }

    
    private static void placeMove(char[][] board, int row, int col, char player, LinkedList<Move> moveHistory, Queue<Move> recentMoves, Stack<Move> undoStackX, Stack<Move> undoStackO) {
        board[row][col] = player;
        Move move = new Move(row, col, player);
        moveHistory.add(move);
        if (player == 'X') {
            undoStackX.push(move);
        } else {
            undoStackO.push(move);
        }
        recentMoves.add(move);
        if (recentMoves.size() > HISTORY_SIZE) {
            recentMoves.poll();
        }
    }

    
    private static boolean checkGameOver(char[][] board, char player) {
        if (haveWon(board, player)) {
            printBoard(board);
            System.out.println("Player " + player + " has won!");
            return true;
        } else if (isBoardFull(board)) {
            printBoard(board);
            System.out.println("The game is a draw!");
            return true;
        }
        return false;
    }

    
    private static char switchPlayer(char currentPlayer) {
        return currentPlayer == 'X' ? 'O' : 'X';
    }

    
    public static boolean haveWon(char[][] board, char player) {
        // Check the rows
        for (int row = 0; row < board.length; row++) {
            if (board[row][0] == player && board[row][1] == player && board[row][2] == player) {
                return true;
            }
        }

        // Check the columns
        for (int col = 0; col < board[0].length; col++) {
            if (board[0][col] == player && board[1][col] == player && board[2][col] == player) {
                return true;
            }
        }

        // Check the diagonals
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return true;
        }

        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) {
            return true;
        }
        return false;
    }

    
    public static boolean isBoardFull(char[][] board) {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    
    public static void printBoard(char[][] board) {
        System.out.println("  0   1   2 ");
        for (int row = 0; row < board.length; row++) {
            System.out.print(row + " ");
            for (int col = 0; col < board[row].length; col++) {
                System.out.print(board[row][col]);
                if (col < board[row].length - 1) {
                    System.out.print(" | ");
                }
            }
            System.out.println();
            if (row < board.length - 1) {
                System.out.println("  ---------");
            }
        }
    }

    
    public static void displayMoveHistory(LinkedList<Move> moveHistory) {
        for (Move move : moveHistory) {
            System.out.println("Player " + move.player + " moved to (" + move.row + ", " + move.col + ")");
        }
    }

   
    public static void displayRecentMoves(Queue<Move> recentMoves) {
        for (Move move : recentMoves) {
            System.out.println("Player " + move.player + " moved to (" + move.row + ", " + move.col + ")");
        }
    }
}