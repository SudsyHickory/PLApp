package org.mainapp.config.footballData;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TeamDTO {

    private Long id;
    private String shortName;
    private String crest;

    public TeamDTO() {
    }

    public TeamDTO(Long id, String shortName, String crest) {
        this.id = id;
        this.shortName = shortName;
        this.crest = crest;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCrest() {
        return crest;
    }

    public void setCrest(String crest) {
        this.crest = crest;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
