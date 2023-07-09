package service;

import dto.ListItemDTO;
import dto.ListItemsDTO;
import dto.PaperSentDTO;
import exception.RepositoryException;
import model.Grade;
import model.Paper;
import model.User;
import repository.*;
import services.IObserver;
import services.IServices;
import services.ServiceException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Service implements IServices {

    private IUserRepository userRepository;
    IGradeDBRepository gradeDBRepository;
    IParticipantDBRepository participantDBRepository;
    IPaperDBRepository paperDBRepository;
    private Map<Long, IObserver> loggedClients;
    private final int defaultThreadsNo = 5;

    public Service(IUserRepository userRepository, IGradeDBRepository gradeDBRepository,
                   IParticipantDBRepository participantDBRepository, IPaperDBRepository paperDBRepository) {
        this.userRepository = userRepository;
        this.gradeDBRepository = gradeDBRepository;
        this.participantDBRepository = participantDBRepository;
        this.paperDBRepository = paperDBRepository;
        this.loggedClients = new ConcurrentHashMap<>();
    }

    public synchronized User checkLogIn(User user, IObserver client) throws ServiceException {
        User userToFind;
        System.out.println("USER CHECKLLOG IN: " + user);
        try {
            System.out.println("+++++++++++++++ B4 repo!\n");
            userToFind = userRepository.findUserByUsername(user.getUsername());

        } catch (RepositoryException re) {
            throw new ServiceException(re.getMessage());
        }
        if (loggedClients.containsKey(userToFind.getId())) {
            throw new ServiceException("User already logged in.");
        }
        if (Objects.equals(userToFind.getPassword(), user.getPassword())) {
            user.setId(userToFind.getId());
            loggedClients.put(user.getId(), client);
            return userToFind;
        } else {
            throw new ServiceException("Incorrect Password");
        }
    }

    @Override
    public synchronized void logout(User user) throws ServiceException {
        if (loggedClients.containsKey(user.getId())) {
            loggedClients.remove(user.getId());
        } else{
            throw new ServiceException("User not logged in");
        }
    }

    @Override
    public ListItemsDTO getPapers(User corecotr) throws ServiceException {
        System.out.println("SERV -> getPapers");
        List<Paper> papers = (List<Paper>) paperDBRepository.getAll();
        ListItemsDTO papersToSend = new ListItemsDTO();


        System.out.println("Corector: " + corecotr);
        for (var p : papers) {

            for (var corector : p.getCorectori()) {

                if (Objects.equals(corector.getUsername(), corecotr.getUsername())) {

                    if (Objects.equals(p.getStatus(), "toBeRecorrected") || p.getGrades().size() < 2) {
                        Double gradeDifference = -1D;
                        if (p.getGrades().size() == 2) {
                            gradeDifference = Math.abs(p.getGrades().get(1).getValue() - p.getGrades().get(0).getValue());
                        }
                        ListItemDTO paperDTO = new ListItemDTO(p.getId(), gradeDifference, p.getStatus());
                        papersToSend.addItem(paperDTO);
                        System.out.println("Added: " + paperDTO);
                    }
                }
            }

        }
        return papersToSend;


    }


    @Override
    public void gradedPaper(PaperSentDTO paperSentDTO) throws ServiceException {
        if (paperSentDTO.getGrade().getValue() < 0 || paperSentDTO.getGrade().getValue() > 10) {
            throw new ServiceException("Grade not accepted!");
        }

        Paper paper = new Paper();
        try {
             paper = paperDBRepository.findById(paperSentDTO.getLucrare().getIdPaper());
        } catch (RepositoryException ex) {
            throw new ServiceException(ex.getMessage());
        }




        var corectorId = paperSentDTO.getCorector().getId();
        var client = loggedClients.get(corectorId);

        // 0 -  first corector to add grade
        if (paper.getGrades().size() == 0) {
            System.out.println("SRV -> GRADES SIZE 0");
            paper.addGrade(paperSentDTO.getGrade());
            gradeDBRepository.add(paperSentDTO.getGrade());
            paperDBRepository.update(paper, paper.getId());
            System.out.println("SRV -> gradeOkayOne");
            client.gradeOkayOne();

            // 1 -  2nd corector to add grade
        }  else if (paper.getGrades().size() == 1) {
            System.out.println("SRV -> GRADES SIZE 1");

            if (Objects.equals(paper.getGrades().get(0).getTeacher().getId(), paperSentDTO.getCorector().getId())) {
                // paper already had a greade from this teacher
                throw new ServiceException("Grade not accepted, paper already has grade from this proffesor!");
            }
            paper.addGrade(paperSentDTO.getGrade());
            gradeDBRepository.add(paperSentDTO.getGrade());
            paperDBRepository.update(paper, paper.getId());

            // check dif
            Double dif = Math.abs(paper.getGrades().get(1).getValue() - paper.getGrades().get(0).getValue());
            System.out.println("SRV -> Dif: " + dif);
            if (dif > 1) {
                // bad
                System.out.println("SRV -> gradeRedo");
                paper.setStatus("toBeRecorrected");
                client.gradeRedo();
            } else {
                // good
                System.out.println("SRV -> gradeOkayBoth");
                client.gradeOkayBoth();
            }


            // 2 -  both people have added grade, if to recalc, then replace grade, else error
        } else if (paper.getGrades().size() == 2 && paper.getStatus() == "toBeRecorrected") {
            System.out.println("SRV -> GRADES SIZE 2 & RECORECTED");
            for (Grade g : paper.getGrades()) {
                if (g.getTeacher().getId() == paperSentDTO.getCorector().getId()) {
                    // if this is the grade that was supposed to be replaced
                    paper.getGrades().remove(g);
                    paper.addGrade(paperSentDTO.getGrade());
                    break;
                }
            }
            Double dif =  Math.abs(paper.getGrades().get(1).getValue() - paper.getGrades().get(0).getValue());
            gradeDBRepository.add(paperSentDTO.getGrade());
            paperDBRepository.update(paper, paper.getId());
            System.out.println("SRV -> Dif: " + dif);
            if (dif > 1) {
                // bad
                System.out.println("SRV -> gradeRedo");
                paper.setStatus("toBeRecorrected");
                client.gradeRedo();
            } else {
                // good
                paper.setStatus("Done");
                System.out.println("SRV -> gradeOkayBoth");
                client.gradeOkayBoth();
            }

        } else {
            throw new ServiceException("Grade not accepted!");
        }





    }
}
