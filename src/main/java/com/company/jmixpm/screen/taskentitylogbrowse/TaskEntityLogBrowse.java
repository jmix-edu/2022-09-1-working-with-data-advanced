package com.company.jmixpm.screen.taskentitylogbrowse;

import com.company.jmixpm.entity.Task;
import io.jmix.ui.component.EntityComboBox;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@UiController("TaskEntityLogBrowse")
@UiDescriptor("task-entity-log-browse.xml")
public class TaskEntityLogBrowse extends Screen {

    @Autowired
    private EntityComboBox<UUID> tasksField;

    @Subscribe(id = "projectTasksDc", target = Target.DATA_CONTAINER)
    public void onProjectTasksDcCollectionChange(CollectionContainer.CollectionChangeEvent<Task> event) {
        List<Task> items = event.getSource().getItems();
        tasksField.setOptionsMap(items.stream()
                .collect(Collectors.toMap(Task::getName, Task::getId)));
    }
}