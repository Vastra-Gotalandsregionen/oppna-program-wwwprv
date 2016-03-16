package se.vgregion.portal.wwwprv.model.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Patrik Björk
 */
@Entity
@Table(name = "vgr_dataprivata_globalsetting")
public class GlobalSetting {

    @Id
    private String key;

    private String value;

    public GlobalSetting() {
    }

    public GlobalSetting(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
