package model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Grade implements HasId<Long> {
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private Long id;
    private Double value;
    @OneToOne
    private User teacher;

    public Grade() {
        value = -1D;
        teacher = new User();
    }

    public Grade(Long id, Double value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Grade{" +
                "id=" + id +
                ", value=" + value +
                '}';
    }
}
