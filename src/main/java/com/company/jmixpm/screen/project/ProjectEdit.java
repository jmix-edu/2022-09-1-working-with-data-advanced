package com.company.jmixpm.screen.project;

import com.company.jmixpm.app.ProjectService;
import com.company.jmixpm.datatype.ProjectLabels;
import com.company.jmixpm.screen.user.UserBrowse;
import io.jmix.audit.snapshot.EntitySnapshotManager;
import io.jmix.core.validation.group.UiComponentChecks;
import io.jmix.core.validation.group.UiCrossFieldChecks;
import io.jmix.ui.Notifications;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.TextArea;
import io.jmix.ui.component.ValidationErrors;
import io.jmix.ui.screen.*;
import com.company.jmixpm.entity.Project;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.Collection;
import java.util.Set;

@UiController("Project.edit")
@UiDescriptor("project-edit.xml")
@EditedEntityContainer("projectDc")
public class ProjectEdit extends StandardEditor<Project> {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private Notifications notifications;

    @Subscribe("commitWithBeanValidationBtn")
    public void onCommitWithBeanValidationBtnClick(Button.ClickEvent event) {
        try {
            projectService.saveProject(getEditedEntity());
            closeWithDiscard();
        } catch (ConstraintViolationException e) {
            StringBuilder sb = new StringBuilder();

            for (ConstraintViolation<?> constraintViolation : e.getConstraintViolations()) {
                sb.append(constraintViolation.getMessage()).append("\n");
            }

            notifications.create(Notifications.NotificationType.TRAY)
                    .withCaption(sb.toString())
                    .show();
        }

    }

    @Autowired
    private Validator validator;

    @Subscribe("performBeanValidationBtn")
    public void onPerformBeanValidationBtnClick(Button.ClickEvent event) {
        Set<ConstraintViolation<Project>> violations = validator.validate(getEditedEntity(),
                Default.class, UiCrossFieldChecks.class, UiComponentChecks.class);

        showBeanValidationExceptions(violations);
    }

    private void showBeanValidationExceptions(Set<ConstraintViolation<Project>> constraintViolations) {
        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            sb.append(constraintViolation.getMessage()).append("\n");
        }

        notifications.create(Notifications.NotificationType.TRAY)
                .withCaption(sb.toString())
                .show();
    }

    @Install(to = "usersTable.add", subject = "screenConfigurer")
    private void usersTableAddScreenConfigurer(Screen screen) {
        ((UserBrowse) screen).setFilterProject(getEditedEntity());
    }

    @Autowired
    private EntitySnapshotManager entitySnapshotManager;

    @Subscribe
    public void onAfterCommitChanges(AfterCommitChangesEvent event) {
        entitySnapshotManager.createSnapshot(getEditedEntity(), getEditedEntityContainer().getFetchPlan());
    }


}