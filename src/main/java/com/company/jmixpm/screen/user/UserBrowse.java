package com.company.jmixpm.screen.user;

import com.company.jmixpm.app.UserService;
import com.company.jmixpm.entity.Project;
import com.company.jmixpm.entity.User;
import io.jmix.core.DataManager;
import io.jmix.core.LoadContext;
import io.jmix.core.MetadataTools;
import io.jmix.ui.Notifications;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.Filter;
import io.jmix.ui.component.GroupTable;
import io.jmix.ui.navigation.Route;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@UiController("User.browse")
@UiDescriptor("user-browse.xml")
@LookupComponent("usersTable")
@Route("users")
public class UserBrowse extends StandardLookup<User> {
    @Autowired
    private GroupTable<User> usersTable;
    @Autowired
    private Filter filter;
    @Autowired
    private MetadataTools metadataTools;
    @Autowired
    private Notifications notifications;
    @Autowired
    private UserService userService;
    private Project filterProject;
    @Autowired
    private DataManager dataManager;

    @Subscribe("showIN")
    public void onShowINClick(Button.ClickEvent event) {
        User selected = usersTable.getSingleSelected();
        notifications.create()
                .withCaption("User: " + metadataTools.getInstanceName(selected))
                .show();
    }

    /*@Install(to = "usersDl", target = Target.DATA_LOADER)
    private List<User> usersDlLoadDelegate(LoadContext<User> loadContext) {
        LoadContext.Query query = loadContext.getQuery();
        if (filterProject != null
                && query != null) {
            return userService.getUsersNotInProject(filterProject, query.getFirstResult(), query.getMaxResults());
        }

        return dataManager.loadList(loadContext);
    }*/


    public UserBrowse setFilterProject(Project filterProject) {
        this.filterProject = filterProject;

        if (filterProject != null) {
            filter.setVisible(false);
        }

        return this;
    }
}