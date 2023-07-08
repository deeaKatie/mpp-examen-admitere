package services;

import dto.ListItemDTO;
import dto.ListItemsDTO;
import model.User;

import java.util.ArrayList;

public interface IServices {
    User checkLogIn(User user,IObserver client) throws ServiceException;
    void logout(User user) throws ServiceException;

    ListItemsDTO getPapers(User corector) throws ServiceException;
}
