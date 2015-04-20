package taskManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.activity.InvalidActivityException;

import org.junit.Before;
import org.junit.Test;

public class TaskTester {

	private LocalDateTime now;

	private Task baseTask;
	private Task dependentTask;
	private Task finishedTask;
	private Task failedTask;
	private Task level2DependentTask;
	private Project project;

	@Before
	public void setUp() throws Exception {
		now = LocalDateTime.of(2015, 03, 03, 8, 0);
		project = new Project("proj", "descr", LocalDateTime.of(2015, 03, 03,
				8, 0), LocalDateTime.of(2016, 03, 03, 8, 0));

		project.taskBuilder("a task", Duration.ofHours(8), 0.2).build();
		baseTask = project.getAllTasks().get(0);

		project.taskBuilder("a dependent task", Duration.ofHours(8), 0.2)
				.addDependencies(baseTask).build();
		dependentTask = project.getAllTasks().get(1);

		project.taskBuilder("a finished task", Duration.ofHours(8), 0.2).build();
		finishedTask = project.getAllTasks().get(2);
		finishedTask.updateStatus(now, now.plusHours(2), false);

		project.taskBuilder("a failed task", Duration.ofHours(8), 0.2).build();
		failedTask = project.getAllTasks().get(3);
		failedTask.updateStatus(now, now.plusHours(2), true);

		project.taskBuilder("a task dependent on all kind of tasks",
				Duration.ofHours(8), 0.2).addDependencies(finishedTask)
				.addDependencies(failedTask).addDependencies(dependentTask)
				.build();
		level2DependentTask = project.getAllTasks().get(4);
	}

	@Test
	public void getStatusAvailableUndependentTask() {
		assertEquals(TaskStatus.AVAILABLE, baseTask.getCalculatedStatus());
	}

	@Test
	public void getStatusAvailableDependentTask() {
		baseTask.updateStatus(now.minusDays(1), now, false);
		assertEquals(TaskStatus.AVAILABLE, dependentTask.getCalculatedStatus());
	}

	@Test
	public void getStatusUnavailableTask() {
		assertEquals(TaskStatus.UNAVAILABLE, dependentTask.getCalculatedStatus());
	}

	@Test
	public void getStatusFinishedTask() {
		assertEquals(TaskStatus.FINISHED, finishedTask.getCalculatedStatus());
	}

	@Test
	public void getStatusFailedTask() {
		assertEquals(TaskStatus.FAILED, failedTask.getCalculatedStatus());
	}

	@Test
	public void getStatusLevel2DependentTask() {
		assertEquals(TaskStatus.UNAVAILABLE, level2DependentTask.getCalculatedStatus());
	}

	@Test
	public void getEstimatedFinishTimeAvaillableTask() {
		assertEquals(LocalDateTime.of(2015, 03, 03, 16, 0),
				baseTask.getEstimatedFinishTime());
	}

	@Test
	public void getEstimatedFinishTimeUvaillableTask() {
		assertEquals(LocalDateTime.of(2015, 03, 04, 16, 0),
				dependentTask.getEstimatedFinishTime());
	}

	@Test
	public void getEstimatedFinishTimeLevel2Task() {
		assertEquals(LocalDateTime.of(2015, 03, 05, 16, 0),
				level2DependentTask.getEstimatedFinishTime());
	}

	@Test
	public void testGetEstimatedFinishTime() {
		Project project = new Project("proj", "descr", LocalDateTime.of(2015,
				03, 03, 8, 0), LocalDateTime.of(2016, 03, 03, 8, 0));
		project.taskBuilder("bla", Duration.ofHours(5 * 8), 0.5).build();
		assertEquals(now.plusDays(6).plusHours(8), project.getAllTasks().get(0)
				.getEstimatedFinishTime());
	}

	@Test
	public void getId() {
		Project project = new Project("proj", "descr", LocalDateTime.of(2014,
				03, 03, 8, 0), LocalDateTime.of(2016, 03, 03, 8, 0));

		project.taskBuilder("new task 1", Duration.ofHours(8), 0.2).build();
		project.taskBuilder("new task 2", Duration.ofHours(8), 0.2).build();

		assertEquals(project.getAllTasks().get(0).getId() + 1, project
				.getAllTasks().get(1).getId());
	}

	@Test(expected = IllegalArgumentException.class)
	public void createTaskWithDoubleDependency() {
		project.taskBuilder("new task 2", Duration.ofHours(8), 0.2)
				.addDependencies(baseTask).addDependencies(baseTask).build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void addAlreadyPresentDependency() {
		dependentTask.addDependency(baseTask);
	}

	@Test(expected = IllegalArgumentException.class)
	public void addLoopingDependency() {
		baseTask.addDependency(dependentTask);
	}

	@Test
	public void hasDirectDependency() {
		assertTrue(dependentTask.hasDependency(baseTask));
	}

	@Test
	public void hasIndirectDependency() {
		assertTrue(level2DependentTask.hasDependency(baseTask));
	}

	@Test
	public void finishedEarly() throws InvalidActivityException {
		baseTask.updateStatus(now, now.plusHours(2), false);
		assertEquals(baseTask.getFinishStatus(), TaskFinishedStatus.EARLY);
	}

	@Test
	public void finishedWithADelay() throws InvalidActivityException {
		baseTask.updateStatus(now, now.plusDays(3), false);
		assertEquals(baseTask.getFinishStatus(),
				TaskFinishedStatus.WITH_A_DELAY);
	}

	@Test
	public void finishedOnTimeEarly() throws InvalidActivityException {
		baseTask.updateStatus(now, now.plusHours(7), false);
		assertEquals(baseTask.getFinishStatus(), TaskFinishedStatus.ON_TIME);
	}

	@Test
	public void finishedOnTimeExact() throws InvalidActivityException {
		baseTask.updateStatus(now, now.plusHours(8), false);
		assertEquals(baseTask.getFinishStatus(), TaskFinishedStatus.ON_TIME);
	}

	@Test
	public void finishedOnTimeLate() throws InvalidActivityException {
		baseTask.updateStatus(now, now.plusHours(8), false);
		assertEquals(baseTask.getFinishStatus(), TaskFinishedStatus.ON_TIME);
	}

	@Test(expected = IllegalArgumentException.class)
	public void taskIsNotFinishedYet() throws InvalidActivityException {
		baseTask.getFinishStatus();
	}

	@Test(expected = IllegalArgumentException.class)
	public void negativeDuration() {
		Project project = new Project("proj", "descr", LocalDateTime.of(2014,
				03, 03, 8, 0), LocalDateTime.of(2016, 03, 03, 8, 0));
		project.taskBuilder("bla", Duration.ofHours(-1), 0.5).build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void zeroDuration() {
		Project project = new Project("proj", "descr", LocalDateTime.of(2014,
				03, 03, 8, 0), LocalDateTime.of(2016, 03, 03, 8, 0));
		project.taskBuilder("bla", Duration.ofHours(0), 0.5).build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void negativeDeviation() {
		Project project = new Project("proj", "descr", LocalDateTime.of(2014,
				03, 03, 8, 0), LocalDateTime.of(2016, 03, 03, 8, 0));
		project.taskBuilder("bla", Duration.ofHours(5 * 8), -2).build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void setEndTimeBeforeStartTime() {
		baseTask.updateStatus(now, now.minusDays(2), false);
	}

	@Test
	public void createAlternativeTask() {
		project.taskBuilder("desc2", Duration.ofHours(3), 2)
				.setOriginalTask(failedTask).build();
	}

	@Test
	public void createAlternativeTaskWithDep() {
		project.taskBuilder("desc2", Duration.ofHours(3), 2)
				.addDependencies(baseTask).setOriginalTask(failedTask).build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void createAlternativeTaskWithAutoDep() {
		project.taskBuilder("desc2", Duration.ofHours(3), 2)
				.addDependencies(failedTask).setOriginalTask(failedTask)
				.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void createAlternativeTaskWithIndirectAutoDep() {
		baseTask.updateStatus(now, now.plusDays(2), true);
		project.taskBuilder("desc2", Duration.ofHours(3), 2)
				.addDependencies(dependentTask).setOriginalTask(baseTask)
				.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void setAlternativeTaskInvalidTaskNotFailed() {
		Project project = new Project("proj", "descr", LocalDateTime.of(2014,
				03, 03, 8, 0), LocalDateTime.of(2016, 03, 03, 8, 0));
		project.taskBuilder("bla", Duration.ofHours(5 * 8), 3).build();
		;
		project.taskBuilder("desc2", Duration.ofHours(3), 2)
				.setOriginalTask(project.getAllTasks().get(0)).build();

	}

	@Test
	public void update() {
		baseTask.handleTimeChange(now.plusDays(5));
		assertEquals(now.plusDays(5), baseTask.getLastUpdateTime());
	}

	@Test
	public void addResourceType() {
		Project project = new Project("proj", "descr", LocalDateTime.of(2014,
				03, 03, 8, 0), LocalDateTime.of(2016, 03, 03, 8, 0));
		ResourceExpert resourceExpert = new ResourceExpert();
		resourceExpert.resourceTypeBuilder("resourcetype").build();

		List<ResourceType> list = new ArrayList<ResourceType>(
				resourceExpert.getAllResourceTypes());
		list.get(0).createResource("res1");

		project.taskBuilder("desc", Duration.ofHours(2), 2)
				.addRequiredResourceType(list.get(0), 1).build();
		assertEquals(1, project.getAllTasks().get(0).getRequiredResourceTypes()
				.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void addAlreadyPresentResourceType() {
		ResourceExpert resourceExpert = new ResourceExpert();
		resourceExpert.resourceTypeBuilder("resourcetype").build();
		List<ResourceType> list = new ArrayList<ResourceType>(
				resourceExpert.getAllResourceTypes());
		list.get(0).createResource("res1");
		baseTask.addResourceType(list.get(0), 1);
		baseTask.addResourceType(list.get(0), 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void addResourceTypeWithInvalidQuantity() {
		Project project = new Project("proj", "descr", LocalDateTime.of(2014,
				03, 03, 8, 0), LocalDateTime.of(2016, 03, 03, 8, 0));
		ResourceExpert resourceExpert = new ResourceExpert();
		resourceExpert.resourceTypeBuilder("resourcetype").build();

		List<ResourceType> list = new ArrayList<ResourceType>(
				resourceExpert.getAllResourceTypes());
		list.get(0).createResource("res1");

		project.taskBuilder("desc", Duration.ofHours(2), 2)
				.addRequiredResourceType(list.get(0), -1).build();
		assertEquals(1, project.getAllTasks().get(0).getRequiredResourceTypes()
				.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void addResourceTypeWithNotEnoughResources() {
		Project project = new Project("proj", "descr", LocalDateTime.of(2014,
				03, 03, 8, 0), LocalDateTime.of(2016, 03, 03, 8, 0));
		ResourceExpert resourceExpert = new ResourceExpert();
		resourceExpert.resourceTypeBuilder("resourcetype").build();

		List<ResourceType> list = new ArrayList<ResourceType>(
				resourceExpert.getAllResourceTypes());
		list.get(0).createResource("res1");

		project.taskBuilder("desc", Duration.ofHours(2), 2)
				.addRequiredResourceType(list.get(0), 2).build();
		assertEquals(1, project.getAllTasks().get(0).getRequiredResourceTypes()
				.size());
	}

}
