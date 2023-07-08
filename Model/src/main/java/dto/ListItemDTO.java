package dto;

import model.HasId;

public class ListItemDTO implements HasId<Long> {

    private Long idPaper;
    private Double gradeDifference;
    private String status;

    public ListItemDTO() {
        gradeDifference = -1D;
    }

    public ListItemDTO(Long idPaper, Double gradeDifference, String status) {
        this.idPaper = idPaper;
        this.gradeDifference = gradeDifference;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getGradeDifference() {
        return gradeDifference;
    }

    public void setGradeDifference(Double gradeDifference) {
        this.gradeDifference = gradeDifference;
    }

    public Long getIdPaper() {
        return idPaper;
    }

    public void setIdPaper(Long idPaper) {
        this.idPaper = idPaper;
    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public void setId(Long aLong) {

    }

    @Override
    public String toString() {
        if (gradeDifference < 0) {
            return "Paper " + idPaper + " " + status;
        }
        return "Paper " + idPaper + " " + status + " -> dif: " + gradeDifference;
    }
}
