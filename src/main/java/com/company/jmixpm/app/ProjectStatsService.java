package com.company.jmixpm.app;

import com.company.jmixpm.entity.Project;
import com.company.jmixpm.entity.ProjectStats;
import com.company.jmixpm.entity.Task;
import io.jmix.core.DataManager;
import io.jmix.core.FetchPlan;
import io.jmix.core.FetchPlans;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectStatsService {

    @Autowired
    private DataManager dataManager;

    @Autowired
    private FetchPlans fetchPlans;

    private FetchPlan createFetchPlanWithTasks() {
        return fetchPlans.builder(Project.class)
                .addFetchPlan(FetchPlan.INSTANCE_NAME)
                .add("tasks", fetchPlanBuilder -> fetchPlanBuilder.add("estimatedEfforts"))
                .build();
    }

    public List<ProjectStats> fetchProjectStatistics() {
        List<Project> projects = dataManager.load(Project.class)
                .all()
//                .fetchPlan(createFetchPlanWithTasks())
                .fetchPlan("project-with-tasks")
                .list();

        return projects.stream().map(project -> {
            ProjectStats stat = dataManager.create(ProjectStats.class);

            stat.setProjectId(project.getId());
            stat.setProjectName(project.getName());
            stat.setTaskCount(project.getTasks().size());
            Integer estimatedEfforts = project.getTasks().stream()
                    .map(Task::getEstimatedEfforts)
                    .reduce(0, Integer::sum);
            stat.setPlannedEfforts(estimatedEfforts);

            stat.setActualEfforts(getActualEfforts(project.getId()));

            return stat;
        }).collect(Collectors.toList());
    }

    public Integer getActualEfforts(UUID projectId) {
        return dataManager.loadValue("select sum(t.timeSpent) from TimeEntry t " +
                "where t.task.project.id = :projectId", Integer.class)
                .parameter("projectId", projectId).one();
    }
}