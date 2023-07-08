import model.Paper;
import model.Participant;
import model.User;
import repository.*;
import service.Service;
import services.IServices;
import utils.AbstractServer;
import utils.RpcConcurrentServer;
import utils.ServerException;

import java.io.IOException;
import java.util.Properties;

public class StartServer {
    private static int defaultPort = 55555;

    public static void main(String[] args) {


        //todo get rid of this if you want
        Properties serverProps=new Properties();
        try {
            serverProps.load(StartServer.class.getResourceAsStream("server.properties"));
            System.out.println("Server properties set. ");
            serverProps.list(System.out);
        } catch (IOException var21) {
            System.err.println("Cannot find server.properties " + var21);
            return;
        }

        IUserRepository userRepository = new UserDBRepository();
        IGradeDBRepository gradeDBRepository = new GradeDBRepository();
        IParticipantDBRepository participantDBRepository = new ParticipantDBRepository();
        IPaperDBRepository paperDBRepository = new PaperDBRepository();

        //addData(userRepository, gradeDBRepository, participantDBRepository, paperDBRepository);

        IServices service=new Service(userRepository, gradeDBRepository,
                participantDBRepository, paperDBRepository);

        System.out.println("Users: ");
        for( var u : userRepository.getAll() ) {
            System.out.println(u);
        }
        System.out.println("Done");

        System.out.println("Papers");
        for( var u : paperDBRepository.getAll() ) {
            System.out.println(u);
        }
        System.out.println("Done");

        int serverPort = defaultPort;
        try {
            serverPort = Integer.parseInt(serverProps.getProperty("server.port"));
        } catch (NumberFormatException ex) {
            System.err.println("Wrong  Port Number" + ex.getMessage());
            System.err.println("Using default port " + defaultPort);
        }

        System.out.println("Starting server on port: " + serverPort);
        AbstractServer server = new RpcConcurrentServer(serverPort, service);
        try {
            server.start();
        } catch (ServerException ex) {
            System.err.println("Error starting the server" + ex.getMessage());
        } finally {
            try {
                server.stop();
            } catch (ServerException ex) {
                System.err.println("Error stopping server " + ex.getMessage());
            }

        }
    }

    private static void addData(IUserRepository userRepository, IGradeDBRepository gradeDBRepository,
                                IParticipantDBRepository participantDBRepository, IPaperDBRepository paperDBRepository) {
        //CORECTORI
        User c1 = new User("sally", "s");
        User c2 = new User("mark", "m");
        User c3 = new User("ella", "e");

        userRepository.add(c1);
        userRepository.add(c2);
        userRepository.add(c3);

        //PARTICIPANTI
        Participant pa1 = new Participant("Matt");
        Participant pa2 = new Participant("Rufus");
        Participant pa3 = new Participant("Alexis");
        Participant pa4 = new Participant("Margery");
        Participant pa5 = new Participant("Samuel");
        Participant pa6 = new Participant("Fabiana");

        participantDBRepository.add(pa1);
        participantDBRepository.add(pa2);
        participantDBRepository.add(pa3);
        participantDBRepository.add(pa4);
        participantDBRepository.add(pa5);
        participantDBRepository.add(pa6);

        //PAPERS
        Paper p1 = new Paper();
        p1.addCorector(c1);
        p1.addCorector(c2);
        p1.setParticipant(pa1);

        Paper p2 = new Paper();
        p2.addCorector(c1);
        p2.addCorector(c3);
        p2.setParticipant(pa2);

        Paper p3 = new Paper();
        p3.addCorector(c2);
        p3.addCorector(c3);
        p3.setParticipant(pa3);

        Paper p4 = new Paper();
        p4.addCorector(c2);
        p4.addCorector(c1);
        p4.setParticipant(pa4);

        Paper p5 = new Paper();
        p5.addCorector(c3);
        p5.addCorector(c1);
        p5.setParticipant(pa5);

        Paper p6 = new Paper();
        p6.addCorector(c3);
        p6.addCorector(c2);
        p6.setParticipant(pa6);

        paperDBRepository.add(p1);
        paperDBRepository.add(p2);
        paperDBRepository.add(p3);
        paperDBRepository.add(p4);
        paperDBRepository.add(p5);
        paperDBRepository.add(p6);
    }
}
