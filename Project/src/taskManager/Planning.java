package taskManager;

import java.time.LocalDateTime;


import java.util.ArrayList;
import java.util.HashMap;


import java.util.Collections;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import utility.TimeSpan;


public class Planning {


	private Memento memento;
	private TimeSpan timeSpan;
	private Set<Developer> developers = new LinkedHashSet<Developer>();
	private Map<ResourceType, Set<Resource>> resources = new LinkedHashMap<ResourceType, Set<Resource>>();
	/**
	 * The TaskBuilder is an inner class builder for constructing new tasks. The
	 * description, estimated duration and acceptable deviation of a task are
	 * required parameters. The optional parameters for a task are the original
	 * task, dependencies and required resource types.
	 */
	public static class PlanningBuilder {

		private TimeSpan timespan;
		private Task task;
		private Set<Developer> developers;
		private Map<ResourceType, Set<Resource>> resources;
		/**
		 * Creates a PlanningBuilder with the required information for the creation
		 * of a Planning
		 * 
		 * 
		 * @param startTime
		 *            : planned start time
		 * @param task
		 *            : task that is being planned
		 * @param developers
		 * 		      : assigned developers 
		 */
		public PlanningBuilder(LocalDateTime startTime,
				Task task, Developer developer) {
			this.timespan = new TimeSpan(startTime, startTime.plus(task.getDuration()));	
			this.task = task;
			this.developers = new LinkedHashSet<Developer>();
			this.developers.add(developer);
			this.resources = new LinkedHashMap<ResourceType, Set<Resource>>();
		}

		/**
		 * a planning may require resources 
		 */
		public PlanningBuilder addResources (ResourceType resourcetypes, Set<Resource> resource){
			this.resources.put(resourcetypes, resource);
			return this;
		}

		/**
		 * a planning may require more developers
		 */
		public PlanningBuilder addDeveloper (Developer developer) { 
			this.developers.add(developer);
			return this;
		}
		/**
		 * Build a Planning after all the optional values have been set.
		 */
		public Planning build(Planner planner) {
			Planning planning = new Planning(this);
			planner.addPlanning(planning);
			task.setPlanning(planning);
			planner.updateStatus(task);
			return planning;
		}
	}

	/**
	 * 
	 * Returns a new planning builder to add extra parameters such as resources
	 * 
	 * @param startTime
	 *            : planned start time
	 * @param endTime
	 *            : planned end time
	 * @param task
	 *            : task that is being planned
	 * @param developers
	 *            : assigned developers
	 * 
	 * @return planningBuilder : new builder for creating planning
	 */
	public static PlanningBuilder builder(LocalDateTime startTime, Task task,
			Developer developer) {
		return new PlanningBuilder(startTime, task, developer);
	}



	/**
	 * The constructor of planning has a planning builder as argument. The planning builder
	 * contains all the required parameters and possible optional parameters
	 * 
	 * @param planningBuilder
	 *            : planning builder with parameters
	 */
	public Planning(PlanningBuilder planningBuilder){
		setDevelopers(planningBuilder.developers);
		setTimeSpan(planningBuilder.timespan);
		setResources(planningBuilder.resources);
	}

	public Set<Developer> getDevelopers() {
		return Collections.unmodifiableSet(developers);
	}

	void setDevelopers(Set<Developer> developers) {
		this.developers = developers;
	}

	public Map<ResourceType, Set<Resource>> getResources() {
		return Collections.unmodifiableMap(resources);
	}

	void setResources(Map<ResourceType, Set<Resource>> resources) {
		this.resources = resources;
	}

	public TimeSpan getTimeSpan() {
		return timeSpan;
	}

	/**
	 * sets the timespan of the planning
	 * 
	 * @param timeSpan the new timespan of the planning
	 */
	public void setTimeSpan(TimeSpan timeSpan) {
		this.timeSpan = timeSpan;
	}

	/**
	 * allow to edit the end time of the planning
	 * 
	 * @param endTime the new end time of the planning 
	 */
	public void setEndTime(LocalDateTime endTime){
		if(!endTime.isAfter(getTimeSpan().getBegin())){
			throw new IllegalStateException("given end time is before the start time");
		}
		if(endTime.isBefore(this.getTimeSpan().getEnd())){
			this.getTimeSpan().setEnd(endTime);
		}

	}
	
	void save() {
		this.memento = new Memento(this);
	}
	
	boolean load() {
		if(this.memento == null) {
			return false;
		}
		else {
			this.memento.load(this);
			return true;
		}
	}
	
	private class Memento {
		private TimeSpan timeSpan;
		private Task task;
		private Set<Developer> developers;
		private Map<ResourceType, Set<Resource>> resources;
		
		public Memento(Planning planning) {
			this.timeSpan = planning.timeSpan;
			this.developers = new LinkedHashSet<Developer>(planning.developers);
			//TODO: check of deze juist is
			this.resources = new HashMap<ResourceType, Set<Resource>>(planning.resources);
		}
		
		public void load(Planning planning) {
			planning.timeSpan = this.timeSpan;
			planning.developers = this.developers;
			planning.resources = this.resources;
		}
	}
}

