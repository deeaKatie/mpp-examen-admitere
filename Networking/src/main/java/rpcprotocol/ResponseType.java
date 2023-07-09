package rpcprotocol;

public enum ResponseType {
    OK,
    ERROR,
    PAPERS_UPDATE,
    OK_ONE,
    OK_BOTH,
    REDO;
    private ResponseType() {
    }
}
