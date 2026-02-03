package chess;
import java.util.Collection;
import java.util.Objects;
import java.util.ArrayList;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor pieceColor;
    private PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch (type) {
            case KING:
                return kingMoves(board, myPosition);
            case QUEEN:
                return queenMoves(board, myPosition);
            case BISHOP:
                return bishopMoves(board, myPosition);
            case KNIGHT:
                return knightMoves(board, myPosition);
            case ROOK:
                return rookMoves(board, myPosition);
            case PAWN:
                return pawnMoves(board, myPosition);
            default:
                return new ArrayList<>();
        }
    }

    // helper for if position is on board
    private boolean isOnBoard(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

    // helper for if empty or enemy
    private boolean canMoveTo(ChessBoard board, ChessPosition position) {
        ChessPiece pieceAtEnd = board.getPiece(position);

        if (pieceAtEnd == null) return true;

        return pieceAtEnd.getTeamColor() != this.pieceColor;
    }

    // helper for queen rook and bishop
    private Collection<ChessMove> slidingMoves(ChessBoard board, ChessPosition myPosition, int[][] directions) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            while (isOnBoard(newRow, newCol)) {
                ChessPosition newPos = new ChessPosition(newRow, newCol);
                ChessPiece pieceAtEnd = board.getPiece(newPos);

                if (pieceAtEnd == null) {
                    moves.add(new ChessMove(myPosition, newPos, null));
                } else if (pieceAtEnd.getTeamColor() != this.pieceColor) {
                    moves.add(new ChessMove(myPosition, newPos, null));
                    break;
                } else {
                    // spot has a friendly piece, cannot move
                    break;
                }

                newRow += dir[0];
                newCol += dir[1];
            }
        }
        return moves;
    }

    // KING
    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        int[][] directions = {
                {1, -1},  {1, 0},  {1, 1},
                {0, -1},           {0, 1},
                {-1, -1}, {-1, 0}, {-1, 1}
        };

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (isOnBoard(newRow, newCol)) {
                ChessPosition newPos = new ChessPosition(newRow, newCol);
                if (canMoveTo(board, newPos)) {
                    moves.add(new ChessMove(myPosition, newPos, null));
                }
            }
        }
        return moves;
    }

    // QUEEN
    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        int[][] directions = {
                {1, -1},  {1, 0},  {1, 1},
                {0, -1},           {0, 1},
                {-1, -1}, {-1, 0}, {-1, 1}
        };

        moves.addAll(slidingMoves(board, myPosition, directions));
        return moves;
    }

    // BISHOP
    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        int[][] directions = {
                {1, -1},  {1, 1},
                {-1 ,-1}, {-1, 1}
        };

        moves.addAll(slidingMoves(board, myPosition, directions));
        return moves;
    }

    // ROOK
    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        int[][] directions = {
                      {1, 0},
                {0, -1},    {0, 1},
                      {-1, 0}
        };

        moves.addAll(slidingMoves(board, myPosition, directions));
        return moves;
    }

    // KNIGHT
    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        int[][] knightDirections = {
                   {2, -1}, {2, 1},
                {1, -2},       {1, 2},

                {-1, -2},      {-1, 2},
                   {-2, -1}, {-2, 1}
        };

        for (int[] dir : knightDirections) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (isOnBoard(newRow, newCol)) {
                ChessPosition newPos = new ChessPosition(newRow, newCol);
                if (canMoveTo(board, newPos)) {
                    moves.add(new ChessMove(myPosition, newPos, null));
                }
            }
        }
        return moves;
    }

    // PAWN
    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        int direction;
        int startRow;
        int promotionRow;

        if (pieceColor == ChessGame.TeamColor.WHITE) {
            direction = 1;
            startRow = 2;
            promotionRow = 8;
        } else {
            direction = -1;
            startRow = 7;
            promotionRow = 1;
        }

        int newRow = row + direction;
        if (isOnBoard(newRow , col)) {
            ChessPosition newPos = new ChessPosition(newRow, col);
            if (board.getPiece(newPos) == null) {
                addPawnMove(moves, myPosition, newPos, newRow, promotionRow);

                if (row == startRow) {
                    int doubleRow = row + (2*direction);
                    ChessPosition doublePos = new ChessPosition(doubleRow, col);
                    if (board.getPiece(doublePos) == null) {
                        moves.add(new ChessMove(myPosition, doublePos, null));
                    }
                }
            }
        }

        int[] captureCols = {col - 1, col + 1};
        for (int captureCol : captureCols) {
            if (isOnBoard(newRow, captureCol)) {
                ChessPosition capturePos = new ChessPosition(newRow, captureCol);
                ChessPiece pieceAtEnd = board.getPiece(capturePos);
                if (pieceAtEnd != null && pieceAtEnd.getTeamColor() != this.pieceColor) {
                    addPawnMove(moves, myPosition, capturePos, newRow, promotionRow);
                }
            }
        }
        return moves;
    }

    // PAWN promotion helper
    private void addPawnMove(Collection<ChessMove> moves, ChessPosition start, ChessPosition end, int endRow, int promotionRow) {
        if (endRow == promotionRow) {
            moves.add(new ChessMove(start, end, PieceType.QUEEN));
            moves.add(new ChessMove(start, end, PieceType.BISHOP));
            moves.add(new ChessMove(start, end, PieceType.KNIGHT));
            moves.add(new ChessMove(start, end, PieceType.ROOK));
        } else {
            moves.add(new ChessMove(start, end, null));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;
        if (this == o) return true;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return pieceColor + " " + type;
    }
}
