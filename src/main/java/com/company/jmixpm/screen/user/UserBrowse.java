package com.company.jmixpm.screen.user;

import com.company.jmixpm.entity.User;
import io.jmix.core.MetadataTools;
import io.jmix.ui.Notifications;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.GroupTable;
import io.jmix.ui.navigation.Route;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("User.browse")
@UiDescriptor("user-browse.xml")
@LookupComponent("usersTable")
@Route("users")
public class UserBrowse extends StandardLookup<User> {
    @Autowired
    private GroupTable<User> usersTable;

    @Autowired
    private MetadataTools metadataTools;
    @Autowired
    private Notifications notifications;

    @Subscribe("showIN")
    public void onShowINClick(Button.ClickEvent event) {
        User selected = usersTable.getSingleSelected();
        notifications.create()
                .withCaption("User: " + metadataTools.getInstanceName(selected))
                .show();
    }
}