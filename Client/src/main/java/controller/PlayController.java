package controller;

import dto.ListItemDTO;
import dto.ListItemsDTO;
import dto.PaperSentDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Grade;
import model.User;
import services.IObserver;
import services.IServices;
import services.ServiceException;
import utils.MessageAlert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TimerTask;


public class PlayController implements IObserver {
    ObservableList<ListItemDTO> modelPapers = FXCollections.observableArrayList();
    private IServices service;
    private User loggedUser;

    @FXML
    Label usernameLabel;
    @FXML
    Label paperStatusLabel; //todo rename elements in a general way
    @FXML
    Button logOutButton;
    @FXML
    ListView<ListItemDTO> papersListView;
    @FXML
    Label papersLabel;
    @FXML
    Label yourGradeLabel;
    @FXML
    Button insertGradeButton;
    @FXML
    TextField gradeTextField;

    boolean sentToWaiting = false; // todo remove this

    public void setService(IServices service) {
        this.service = service;
    }
    public void setUser(User user) {
        this.loggedUser = user;
    }
    public void initVisuals() {
        usernameLabel.setText("Hi, " + loggedUser.getUsername());
        paperStatusLabel.setVisible(false);

        ListItemsDTO papersS = new ListItemsDTO();
        try {
            papersS = service.getPapers(loggedUser);
        } catch (ServiceException ex) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Error getting papers", ex.getMessage());
        }

        //todo how to add colour to listview items
        updateListView(papersS);
    }

    public void updateListView(ListItemsDTO papersS) {
        modelPapers.setAll(papersS.getItems());
        papersListView.setItems(modelPapers);
    }

    //todo a fill listview automatically
    @FXML
    public void logOutHandler() throws IOException {
        System.out.println("Logging out!\n");
        try {
            service.logout(loggedUser);
        } catch (ServiceException ex) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Error logging out", ex.getMessage());
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/LogInView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        Stage stage = (Stage) logOutButton.getScene().getWindow();
        LogInController logCtrl = fxmlLoader.getController();

        logCtrl.setService(service);
        stage.setScene(scene);
    }

    public void gradeAdded(ActionEvent actionEvent) {
        ListItemDTO selectedPaper = papersListView.getSelectionModel().getSelectedItem();
        String gradeS = gradeTextField.getText();
        Grade gradeD = new Grade(Double.parseDouble(gradeS), loggedUser);

        PaperSentDTO paperSentDTO = new PaperSentDTO(selectedPaper, gradeD, loggedUser);
         try {
             service.gradedPaper(paperSentDTO);
         }  catch (ServiceException ex) {
             MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Error grading paper", ex.getMessage());
         }

    }

    public void gradeOkayOne() {
        Platform.runLater(() -> {
            paperStatusLabel.setText("Grade saved, waiting for other corector..");
            paperStatusLabel.setVisible(true);
            new java.util.Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    paperStatusLabel.setVisible(false);
                }
            }, 1000);

        });
    }

    public void gradeOkayBoth() {
        Platform.runLater(() -> {
            paperStatusLabel.setVisible(true);
            paperStatusLabel.setText("Both grades okay");
            new java.util.Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                   paperStatusLabel.setVisible(false);
                }
            }, 1000);

        });
    }

    public void gradeRedo() {
        Platform.runLater(() -> {
            paperStatusLabel.setVisible(true);
            paperStatusLabel.setText("Bad grade, must redo!");
            new java.util.Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    paperStatusLabel.setVisible(false);
                }
            }, 1000);

        });
    }



}
