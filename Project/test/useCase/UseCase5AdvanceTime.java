package useCase;

import static org.junit.Assert.assertEquals;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

import taskmanager.Developer;
import taskmanager.Planning;
import taskmanager.Project;
import taskmanager.ProjectFinishingStatus;
import taskmanager.ProjectStatus;
import taskmanager.Task;
import taskmanager.BranchOffice;

public class UseCase5AdvanceTime {

	private BranchOffice tmc;
	private Project project1;
	private Project project2;

	private Task task1;

	private LocalDateTime now;

	@Before
	public void setUp() {
		// create a controller, 3 projects and 3 tasks:
		// project0 has 0 tasks
		// project1 has 1 task (finished)
		// project2 has 2 tasks (1 task is dependent on the other)

		now = LocalDateTime.of(2015, 03, 10, 11, 00);

		tmc = new BranchOffice(now);

		tmc.createProject("Project 1", "Description 1",
				LocalDateTime.of(2015, 03, 10, 17, 00));

		tmc.createProject("Project 2", "Description 2",
				LocalDateTime.of(2015, 03, 10, 13, 00));

		project1 = tmc.getAllProjects().get(0);
		project2 = tmc.getAllProjects().get(1);

		Task.builder("Task 1", Duration.ofHours(5), 0.4).build(project1);

		task1 = project1.getAllTasks().get(0);
		Developer dev1 = tmc.createDeveloper("dev1");
		tmc.getPlanner().createPlanning(now, task1, dev1).build();
		tmc.setExecuting(task1, now.minusDays(6));
		tmc.setFinished(task1, now.minusDays(5));
	}

	@Test
	public void advanceTime() {

		// advance time with 5 hours
		tmc.advanceTime(now.plusHours(5));

		// check if the last update time has changed in every project and every
		// task
		assertEquals(now.plusHours(5), tmc.getTime());
		assertEquals(now.plusHours(5), project1.getLastUpdateTime());
		assertEquals(now.plusHours(5), project2.getLastUpdateTime());

		// check if the project finishing status is on time: current time:
		// 2015-03-11 16:00
		// due time of project 1 is 2015-03-11 17:00 so on time
		// due time of project 2 is 2015-03-11 13:00 so over time
		assertEquals(ProjectFinishingStatus.ON_TIME, project1.finishedOnTime());
		assertEquals(ProjectStatus.FINISHED, project1.getStatus());
		assertEquals(ProjectFinishingStatus.OVER_TIME,
				project2.finishedOnTime());
		assertEquals(ProjectStatus.ONGOING, project2.getStatus());

		// advance time with 2 hours, the project is still on time
		tmc.advanceTime(now.plusHours(7));
		assertEquals(now.plusHours(7), tmc.getTime());
		assertEquals(now.plusHours(7), project1.getLastUpdateTime());
		assertEquals(now.plusHours(7), project2.getLastUpdateTime());

		assertEquals(ProjectFinishingStatus.ON_TIME, project1.finishedOnTime());
		assertEquals(ProjectStatus.FINISHED, project1.getStatus());
		assertEquals(ProjectFinishingStatus.OVER_TIME,
				project2.finishedOnTime());
		assertEquals(ProjectStatus.ONGOING, project2.getStatus());
	}
}
