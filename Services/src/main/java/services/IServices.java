package services;

import dto.ListItemDTO;
import dto.ListItemsDTO;
import dto.PaperSentDTO;
import model.User;

import java.util.ArrayList;

public interface IServices {
    User checkLogIn(User user,IObserver client) throws ServiceException;
    void logout(User user) throws ServiceException;

    ListItemsDTO getPapers(User corector) throws ServiceException;

    void gradedPaper(PaperSentDTO paperSentDTO) throws ServiceException;
}
