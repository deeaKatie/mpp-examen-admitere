package dto;

import model.Grade;
import model.HasId;
import model.User;

public class PaperSentDTO implements HasId<Long> {

    private ListItemDTO lucrare;
    private Grade grade;
    private User corector;

    public PaperSentDTO() {
    }

    public PaperSentDTO(ListItemDTO lucrare, Grade grade, User corector) {
        this.lucrare = lucrare;
        this.grade = grade;
        this.corector = corector;
    }

    public ListItemDTO getLucrare() {
        return lucrare;
    }

    public void setLucrare(ListItemDTO lucrare) {
        this.lucrare = lucrare;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public User getCorector() {
        return corector;
    }

    public void setCorector(User corector) {
        this.corector = corector;
    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public void setId(Long aLong) {

    }
}
