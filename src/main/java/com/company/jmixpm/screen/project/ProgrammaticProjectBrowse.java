package com.company.jmixpm.screen.project;

import com.company.jmixpm.entity.Project;
import io.jmix.core.*;
import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.core.metamodel.model.MetaPropertyPath;
import io.jmix.ui.Actions;
import io.jmix.ui.Facets;
import io.jmix.ui.UiComponents;
import io.jmix.ui.UiScreenProperties;
import io.jmix.ui.action.Action;
import io.jmix.ui.action.BaseAction;
import io.jmix.ui.action.list.CreateAction;
import io.jmix.ui.action.list.EditAction;
import io.jmix.ui.action.list.RemoveAction;
import io.jmix.ui.component.*;
import io.jmix.ui.component.LookupComponent;
import io.jmix.ui.component.data.table.ContainerGroupTableItems;
import io.jmix.ui.icon.JmixIcon;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.model.DataComponents;
import io.jmix.ui.model.DataContext;
import io.jmix.ui.model.impl.NoopDataContext;
import io.jmix.ui.screen.*;
import io.jmix.ui.settings.facet.ScreenSettingsFacet;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

@DialogMode(width = "800px", height = "600px")
@UiController("ProgrammaticProjectBrowse")
public class ProgrammaticProjectBrowse extends StandardLookup<Project> {

    @Autowired
    private Actions actions;
    @Autowired
    private Messages messages;
    @Autowired
    private MessageTools messageTools;
    @Autowired
    private MessageBundle messageBundle;
    @Autowired
    private Facets facets;
    @Autowired
    private FetchPlans fetchPlans;
    @Autowired
    private UiComponents uiComponents;
    @Autowired
    private DataComponents dataComponents;
    @Autowired
    private MetadataTools metadataTools;
    @Autowired
    private UiScreenProperties uiScreenProperties;

    private CollectionContainer<Project> projectsDc;
    private CollectionLoader<Project> projectsDl;
    private GroupTable<Project> projectsTable;

    @Subscribe
    public void onInit(InitEvent event) {
        getWindow().setCaption(messageBundle.getMessage("programmaticProjectBrowse.caption"));

        initData();
        initFacets();
        initActions();
        initLayout();

        getWindow().setFocusComponent(projectsTable.getId());
    }

    private void initData() {
//        DataContext dataContext = dataComponents.createDataContext();
        DataContext dataContext = new NoopDataContext(getApplicationContext());
        getScreenData().setDataContext(dataContext);

        projectsDc = dataComponents.createCollectionContainer(Project.class);
        getScreenData().registerContainer("projectsDc", projectsDc);

        projectsDl = dataComponents.createCollectionLoader();
        projectsDl.setContainer(projectsDc);
        projectsDl.setDataContext(dataContext);
        projectsDl.setQuery("select e from Project e");
        projectsDl.setFetchPlan(createProjectsFetchPlan());
        getScreenData().registerLoader("projectsDl", projectsDl);
    }

    private FetchPlan createProjectsFetchPlan() {
        return fetchPlans.builder(Project.class)
                .addFetchPlan(FetchPlan.BASE)
                .add("manager", fetchPlanBuilder ->
                        fetchPlanBuilder.addFetchPlan(FetchPlan.BASE))
                .add("owner", fetchPlanBuilder ->
                        fetchPlanBuilder.addFetchPlan(FetchPlan.BASE))
                .build();
    }

    private void initFacets() {
        ScreenSettingsFacet screenSettingsFacet = facets.create(ScreenSettingsFacet.class);
        getWindow().addFacet(screenSettingsFacet);
        screenSettingsFacet.setAuto(true);

        DataLoadCoordinator dataLoadCoordinator = facets.create(DataLoadCoordinator.class);
        getWindow().addFacet(dataLoadCoordinator);
        // Only after add
        dataLoadCoordinator.configureAutomatically();
    }

    private void initActions() {
        BaseAction lookupSelectAction = new BaseAction("lookupSelectAction")
                .withCaption(messages.getMessage("actions.Select"))
                .withIcon(JmixIcon.LOOKUP_OK.iconName())
                .withPrimary(true)
                .withShortcut(uiScreenProperties.getCommitShortcut());
        getWindow().addAction(lookupSelectAction);

        BaseAction lookupCancelAction = new BaseAction("lookupCancelAction")
                .withCaption(messages.getMessage("actions.Cancel"))
                .withIcon(JmixIcon.LOOKUP_CANCEL.iconName());
        getWindow().addAction(lookupCancelAction);
    }

    private void initLayout() {
        initFilter();
        initTable();
        initWindowButtonsPanel();

        getWindow().setSpacing(true);
        getWindow().expand(projectsTable);
    }

    private void initFilter() {
        Filter filter = uiComponents.create(Filter.class);
        filter.setId("filter");
        filter.setDataLoader(projectsDl);
        filter.loadConfigurationsAndApplyDefault();

        getWindow().add(filter);
    }

    private void initTable() {
        projectsTable = uiComponents.create(GroupTable.of(Project.class));
        projectsTable.setId("projectsTable");
        projectsTable.setWidthFull();

        initTableActions();
        initTableColumns();
        initTableButtonsPanel();
        initTablePagination();

        // after add columns
        projectsTable.setItems(new ContainerGroupTableItems<>(projectsDc));

        getWindow().add(projectsTable);
    }

    @SuppressWarnings("unchecked")
    private void initTableActions() {
        CreateAction<Project> createAction = actions.create(CreateAction.class);
        projectsTable.addAction(createAction);

        EditAction<Project> editAction = actions.create(EditAction.class);
        projectsTable.addAction(editAction);

        RemoveAction<Project> removeAction = actions.create(RemoveAction.class);
        projectsTable.addAction(removeAction);
    }

    private void initTableColumns() {
        addColumn("name");
        addColumn("startDate");
        addColumn("endDate");
        addColumn("status");
        addColumn("labels");
        addColumn("totalEstimatedEfforts");
        addColumn("owner");
    }

    private void addColumn(String id) {
        MetaClass metaClass = projectsDc.getEntityMetaClass();
        MetaPropertyPath metaPropertyPath = metadataTools.resolveMetaPropertyPath(metaClass, id);

        Table.Column<Project> column = projectsTable.addColumn(metaPropertyPath);

        column.setCaption(messageTools.getPropertyCaption(metaClass, id));
    }

    private void initTableButtonsPanel() {
        ButtonsPanel buttonsPanel = uiComponents.create(ButtonsPanel.class);

        Collection<Action> tableActions = projectsTable.getActions();
        for (Action action : tableActions) {
            Button button = uiComponents.create(Button.class);
            button.setId(action.getId() + "Btn");
            button.setAction(action);

            buttonsPanel.add(button);
        }

        projectsTable.setButtonsPanel(buttonsPanel);
    }

    private void initTablePagination() {
        SimplePagination simplePagination = uiComponents.create(SimplePagination.class);
        projectsTable.setPagination(simplePagination);
    }

    private void initWindowButtonsPanel() {
        HBoxLayout buttonsPanel = uiComponents.create(HBoxLayout.class);
        buttonsPanel.setId("lookupActions");

        Button selectButton = uiComponents.create(Button.class);
        selectButton.setAction(getWindow().getActionNN("lookupSelectAction"));
        buttonsPanel.add(selectButton);

        Button cancelButton = uiComponents.create(Button.class);
        cancelButton.setAction(getWindow().getActionNN("lookupCancelAction"));
        buttonsPanel.add(cancelButton);
    }

    @Override
    protected LookupComponent<Project> getLookupComponent() {
        return projectsTable;
    }
}