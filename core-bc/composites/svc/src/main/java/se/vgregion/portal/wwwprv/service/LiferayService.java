package se.vgregion.portal.wwwprv.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.comparator.UserFirstNameComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.vgregion.portal.wwwprv.model.UserContainer;
import se.vgregion.portal.wwwprv.model.jpa.DataPrivataUser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Patrik Bergström
 */
@Service
public class LiferayService {

    private static final String FILE_UPLOADER_ROLE_NAME = "Filuppladdare";

    private UserLocalService userLocalService;
    private RoleLocalService roleLocalService;
    private UserGroupRoleLocalService userGroupRoleLocalService;

    @Autowired
    private DataPrivataService dataPrivataService;

    public LiferayService() {
        this.userLocalService = UserLocalServiceUtil.getService();
        this.roleLocalService = RoleLocalServiceUtil.getService();
        this.userGroupRoleLocalService = UserGroupRoleLocalServiceUtil.getService();
    }

    /**
     * Finds users which have the relevant site role.
     *
     * @param companyId
     * @return
     * @throws LiferayServiceException
     */
    // todo Make cacheable or change design so we don't (potentially) hit the db so frequently.
    @Transactional
    public List<UserContainer> getUploaderUsers(long companyId) throws LiferayServiceException {

        List<UserContainer> userContainers = new ArrayList<>();

        try {
            boolean andSearch = true;

            LinkedHashMap<String, Object> params = new LinkedHashMap<>();

            List<User> users = userLocalService.search(companyId, null, null, null, null, null,
                    WorkflowConstants.STATUS_APPROVED, params, andSearch, 0,
                    userLocalService.getUsersCount(), new UserFirstNameComparator(true));

            filterOutNonFileUploaders(users);

            // So now we have all users which should be managed. Those which aren't already we "create".
            for (User user : users) {
                DataPrivataUser dataPrivataUser = dataPrivataService.getUserById(user.getUserId());

                if (dataPrivataUser == null) {
                    dataPrivataUser = new DataPrivataUser(user.getUserId());
                    dataPrivataService.persistNewUser(dataPrivataUser);
                }

                userContainers.add(new UserContainer(user, dataPrivataUser));
            }

            return userContainers;

        } catch (SystemException | PortalException e) {
            throw new RuntimeException(e);
        }
    }

    private void filterOutNonFileUploaders(List<User> users) throws SystemException, PortalException {
        Iterator<User> iterator = users.iterator();

        while (iterator.hasNext()) {
            User next = iterator.next();

            List<UserGroupRole> userGroupRoles = userGroupRoleLocalService.getUserGroupRoles(next.getUserId());

            boolean isFileUploader = false;
            for (UserGroupRole userGroupRole : userGroupRoles) {
                if (userGroupRole.getRole().getName().equals(FILE_UPLOADER_ROLE_NAME)) {
                    isFileUploader = true;
                }
            }

            if (!isFileUploader) {
                iterator.remove();
            }

        }
    }

    /*private Long getRoleId(long companyId, String roleName) throws LiferayServiceException {

        try {
            List<Role> roles = roleLocalService.search(companyId, roleName, null, new LinkedHashMap<String, Object>(), 0,
                    roleLocalService.getRolesCount(), new RoleNameComparator(true));
*//*
            List<Role> roles = roleLocalService.search(companyId, roleName, null, new LinkedHashMap<String, Object>(), 0,
                    roleLocalService.getRolesCount(), new OrderByComparator() {
                        @Override
                        public int compare(Object o, Object o1) {
                            return ((Role) o).getName().compareTo(((Role) o1).getName());
                        }

                        *//*
*//*@Override
                        public String getOrderBy() {
                            return "ASC";
                        }*//**//*

                    });
*//*

            if (roles.size() != 1) {
                throw new LiferayServiceException("Det måste finnas en och endast en roll som matchar mot " + roleName + ".");
            }

            return roles.get(0).getRoleId();

        } catch (SystemException e) {
            throw new RuntimeException(e);
        }
    }*/
}