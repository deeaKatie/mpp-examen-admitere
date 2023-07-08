package dto;

import model.HasId;

public class ListItemDTO implements HasId<Long> {

    private Long idPaper;

    public ListItemDTO() {
    }

    public ListItemDTO(Long idPaper) {
        this.idPaper = idPaper;
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
}
