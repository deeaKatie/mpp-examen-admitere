package service;

import exception.RepositoryException;
import model.User;
import repository.*;
import repository.IGameDBRepository;
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

}
