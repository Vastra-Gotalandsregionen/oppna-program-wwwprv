package se.vgregion.portal.wwwprv.model;

import com.liferay.portal.model.User;
import se.vgregion.portal.wwwprv.model.jpa.DataPrivataUser;

/**
 * @author Patrik Bergström
 */
public class UserContainer {

    private User liferayUser;
    private DataPrivataUser dataPrivataUser;

    public UserContainer(User liferayUser, DataPrivataUser dataPrivataUser) {
        this.liferayUser = liferayUser;
        this.dataPrivataUser = dataPrivataUser;
    }

    public User getLiferayUser() {
        return liferayUser;
    }

    public DataPrivataUser getDataPrivataUser() {
        return dataPrivataUser;
    }
}
