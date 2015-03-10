package UseCaseTests;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import TaskManager.Project;
import TaskManager.ProjectController;
import TaskManager.TaskManClock;

public class UseCase2CreateProjectTester {

	private ProjectController controller;
	@Before
	public void setUp(){
		TaskManClock clock = new TaskManClock(LocalDateTime.of(2015, 03, 07,01,00));
		controller = new ProjectController(clock);;
	}
	@Test
	public void testCreateProject() {
		controller.createProject("Project 1", "Description 1", LocalDateTime.of(2015, 03, 01,00,00), LocalDateTime.of(2015, 03, 10,00,00));
		assertEquals(1, controller.getAllProjects().size());

		assertEquals(controller.getAllProjects().size(),1);
		List<Project> projects = controller.getAllProjects();
		assertEquals("Project 1", projects.get(0).getName());
		assertEquals("Description 1", projects.get(0).getDescription());
		assertEquals( LocalDate.of(2015, 03, 01), projects.get(0).getCreationTime());
		assertEquals( LocalDate.of(2015, 03, 10), projects.get(0).getDueTime());
	}


	@Test
	public void testCreateProjects(){
		controller.createProject("name1", "descr1",  LocalDateTime.of(2015, 03, 05,00,00), LocalDateTime.of(2015, 03, 06,00,00));
		controller.createProject("name2", "descr2",  LocalDateTime.of(2015, 03, 06,00,00), LocalDateTime.of(2015, 03, 07,00,00));


		assertEquals(controller.getAllProjects().size(),2);
		List<Project> projects = controller.getAllProjects();
		assertEquals("name1", projects.get(0).getName());
		assertEquals("descr1", projects.get(0).getDescription());
		assertEquals( LocalDate.of(2015, 03, 05), projects.get(0).getCreationTime());
		assertEquals( LocalDate.of(2015, 03, 06), projects.get(0).getDueTime());
		
		assertEquals("name2", projects.get(1).getName());
		assertEquals("descr2", projects.get(1).getDescription());
		assertEquals( LocalDate.of(2015, 03, 06), projects.get(1).getCreationTime());
		assertEquals( LocalDate.of(2015, 03, 07), projects.get(1).getDueTime());
	}
}