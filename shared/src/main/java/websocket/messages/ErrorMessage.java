package websocket.messages;

public class ErrorMessage extends ServerMessage {
    private String ErrorMessage;

    public ErrorMessage(String ErrorMessage) {
        super(ServerMessageType.ERROR);
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }
}
