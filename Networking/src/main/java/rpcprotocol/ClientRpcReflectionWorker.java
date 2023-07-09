package rpcprotocol;

import dto.ListItemDTO;
import dto.ListItemsDTO;
import dto.PaperSentDTO;
import model.User;
import services.IObserver;
import services.IServices;
import services.ServiceException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class ClientRpcReflectionWorker implements Runnable, IObserver {
    private IServices service;
    private Socket connection;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;
    private static Response okResponse = (new Response.Builder().type(ResponseType.OK)).build();

    public ClientRpcReflectionWorker(IServices service, Socket connection) {
        this.service = service;
        this.connection = connection;

        try {
            this.output = new ObjectOutputStream(connection.getOutputStream());
            this.output.flush();
            this.input = new ObjectInputStream(connection.getInputStream());
            this.connected = true;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        while (this.connected) {
            try {
                Object request = this.input.readObject();
                System.out.println(request);
                Response response = this.handleRequest((Request) request);
                System.out.println(response);
                if (response != null) {
                    this.sendResponse(response);
                }
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        try {
            this.input.close();
            this.output.close();
            this.connection.close();
        } catch (IOException ex) {
            System.out.println("Error " + ex);
        }
    }

    private void sendResponse(Response response) throws IOException {
        System.out.println("sending response " + response);
        this.output.writeObject(response);
        this.output.flush();
    }

    private Response handleRequest(Request request) {
        Response response = null;
        String handlerName = "handle" + request.type();
        System.out.println("HandlerName " + handlerName);

        try {
            Method method = this.getClass().getDeclaredMethod(handlerName, Request.class);
            response = (Response) method.invoke(this, request);
            System.out.println("Method " + handlerName + " invoked");
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
            ex.printStackTrace();
        }

        return response;
    }


    private Response handleLOGIN(Request request) {
        System.out.println("Login request ..." + request.type());
        User user = (User) request.data();

        try {
            User newUser = this.service.checkLogIn(user, this);
            return (new Response.Builder()).type(ResponseType.OK).data(newUser).build();
        } catch (ServiceException ex) {
            this.connected = false;
            return (new Response.Builder()).type(ResponseType.ERROR).data(ex.getMessage()).build();
        }
    }

    private Response handleLOGOUT(Request request) {
        System.out.println("Logout request...");
        User user = (User) request.data();

        try {
            this.service.logout(user);
            this.connected = false;
            System.out.println("WORKER -> log out");
            return (new Response.Builder()).type(ResponseType.OK).build();
        } catch (ServiceException ex) {
            return (new Response.Builder()).type(ResponseType.ERROR).data(ex.getMessage()).build();
        }
    }

    private Response handleGET_PAPERS(Request request) { //todo fix sout in eaxh methhod
        System.out.println("WORKER -> GET_PAPERS");

        try {
            ListItemsDTO items = service.getPapers((User) request.data());
            return (new Response.Builder()).type(ResponseType.OK).data(items).build();
        } catch (ServiceException ex) {
            return (new Response.Builder()).type(ResponseType.ERROR).data(ex.getMessage()).build();
        }
    }

    private Response handlePAPER_GRADE(Request request) {
        System.out.println("WORKER -> PAPER_GRADE");
        try {
            service.gradedPaper((PaperSentDTO) request.data()); //todo smarter to use updats?
            return (new Response.Builder()).type(ResponseType.OK).build();
        } catch (ServiceException ex) {
            return (new Response.Builder()).type(ResponseType.ERROR).data(ex.getMessage()).build();
        }
    }

    @Override
    public void gradeOkayOne() {
        System.out.println("WORKER -> OKAY_ONE");
        try {
            sendResponse((new Response.Builder()).type(ResponseType.OK_ONE).build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void gradeOkayBoth() {
        System.out.println("WORKER -> OKAY_BOTH");
        try {
            sendResponse((new Response.Builder()).type(ResponseType.OK_BOTH).build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void gradeRedo() {
        System.out.println("WORKER -> REDO");
        try {
            sendResponse((new Response.Builder()).type(ResponseType.REDO).build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
