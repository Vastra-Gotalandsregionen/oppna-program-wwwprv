package se.vgregion.portal.wwwprv.model;

import com.liferay.portal.kernel.model.User;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserContainer that = (UserContainer) o;

        return !(dataPrivataUser.getLiferayUserId() != null ? !dataPrivataUser.getLiferayUserId().equals(that.dataPrivataUser.getLiferayUserId()) : that.dataPrivataUser.getLiferayUserId() != null);
    }

    @Override
    public int hashCode() {
        return dataPrivataUser.getLiferayUserId() != null ? dataPrivataUser.getLiferayUserId().hashCode() : 0;
    }
}
