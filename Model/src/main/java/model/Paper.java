package model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Paper implements HasId<Long> {
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private Long id;
    @OneToMany
    private List<User> corectori;
    @OneToOne
    private Participant participant;
    @OneToMany
    private List<Grade> grades;
    @OneToOne
    private Grade finalGrade;
    private String status;

    public Paper() {
        corectori = new ArrayList<>();
        grades = new ArrayList<>();
        status = "toBeCorrected";
    }

    public Paper(Long id, List<User> corectori, Participant participant, List<Grade> grades, Grade finalGrade, String status) {
        this.id = id;
        this.corectori = corectori;
        this.participant = participant;
        this.grades = grades;
        this.finalGrade = finalGrade;
        this.status = status;
    }

    public void addGrade(Grade grade) {
        grades.add(grade);
    }

    public void addCorector(User user) {
        corectori.add(user);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public List<User> getCorectori() {
        return corectori;
    }

    public void setCorectori(List<User> corectori) {
        this.corectori = corectori;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }

    public Grade getFinalGrade() {
        return finalGrade;
    }

    public void setFinalGrade(Grade finalGrade) {
        this.finalGrade = finalGrade;
    }
}
