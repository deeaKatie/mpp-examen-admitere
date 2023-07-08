package rpcprotocol;

public enum RequestType {
    LOGIN,
    LOGOUT,
    GET_PAPERS,
    PAPER_GRADE;
    private RequestType() {
    }
}
