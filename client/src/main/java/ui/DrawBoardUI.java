package ui;


import chess.*;
import static ui.EscapeSequences.*;

public class DrawBoardUI {
    private static final String LIGHT_SQ = SET_BG_COLOR_LIGHT_GREY;
    private static final String DARK_SQ  = SET_BG_COLOR_DARK_GREEN;
    private static final String BORDER   = SET_BG_COLOR_DARK_GREY;

    private static final String WHITE_PIECE = SET_TEXT_COLOR_WHITE;
    private static final String BLACK_PIECE = SET_TEXT_COLOR_BLACK;


    public static void draw(ChessBoard board, String color) {
        boolean isBlack = color.equals("BLACK");
        StringBuilder sb = new StringBuilder();
        sb.append(RESET_BG_COLOR).append(RESET_TEXT_COLOR).append("\n");
        String[] colLabels = {"a","b","c","d","e","f","g","h"};


        // top border
        sb.append(borderRow(colLabels, isBlack));


        // rows
        for (int r = 0; r <8; r++) {
            int row = isBlack ? (r + 1) : (8 - r );

            sb.append(borderCell(" " + row + " "));

            for (int c = 0; c < 8; c++) {
                int col = isBlack ? (8-c) : (c+1);
                boolean light = (row + col) % 2 != 0;
                sb.append(light ? LIGHT_SQ : DARK_SQ);
                sb.append(pieceString(board, row, col));
            }

            sb.append(RESET_BG_COLOR);
            sb.append(borderCell(" " + row + " "));
            sb.append("\n");
        }

        sb.append(borderRow(colLabels, isBlack));
        sb.append(RESET_BG_COLOR).append(RESET_TEXT_COLOR).append("\n");

        System.out.print(sb);


    }


    private static String borderRow(String[] colLabels, boolean isBlack) {
        StringBuilder sb = new StringBuilder();
        sb.append(borderCell("    "));
        for (int c = 0; c < 8; c++) {
            int index = isBlack ? (7 - c) : c;
            sb.append(borderCell("" + colLabels[index] + "   "));
        }
        sb.append(borderCell("   ")).append(RESET_BG_COLOR).append("\n");
        return sb.toString();
    }



    private static String borderCell(String text) {
        return BORDER + SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD + text + RESET_TEXT_BOLD_FAINT;
    }


    private static String pieceString(ChessBoard board, int row, int col) {
        ChessPiece piece = board.getPiece(new ChessPosition(row, col));
        if (piece == null) {
            return EMPTY;
        }
        String color = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_PIECE : BLACK_PIECE;
        return color + SET_TEXT_BOLD + getPieceSymbol(piece) + RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR;
    }

    private static String getPieceSymbol(ChessPiece piece) {
        boolean isBlack = piece.getTeamColor() == ChessGame.TeamColor.BLACK;
        return switch (piece.getPieceType()) {
            case KING -> isBlack ? BLACK_KING : WHITE_KING;
            case QUEEN -> isBlack ? BLACK_QUEEN : WHITE_QUEEN;
            case BISHOP -> isBlack ? BLACK_BISHOP : WHITE_BISHOP;
            case KNIGHT -> isBlack ? BLACK_KNIGHT : WHITE_KNIGHT;
            case ROOK -> isBlack ? BLACK_ROOK : WHITE_ROOK;
            case PAWN-> isBlack ? BLACK_PAWN : WHITE_PAWN;
        };
    }


}
