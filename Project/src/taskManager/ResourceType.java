package taskManager;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import utility.TimeInterval;
import utility.WorkDay;

/**
 * Each resource has a specific type, and for each type of resource, numerous
 * instances can exist. Whenever a task is planned, the project manager can
 * choose to assign any resource of a specific type, or can choose a specific
 * instance of this type. When a task is planned, specific resources will be
 * reserved for use during the task’s planned timespan. Resources of a specific
 * resource type can conflict with or depend on resources of another type.
 * 
 * @author Groep 8
 * 
 */
public class ResourceType {

	private String name;
	private Set<ResourceType> requiredResourceTypes = new LinkedHashSet<ResourceType>();
	private Set<ResourceType> conflictedResourceTypes = new LinkedHashSet<ResourceType>();;
	private Set<Resource> resources = new LinkedHashSet<Resource>();
	private TimeInterval dailyAvailability;
	
	private Memento memento;

	/**
	 * The ResourceTypeBuilder is an inner class builder for constructing
	 * resource types. The name of a resourceType is a required parameter. The
	 * optional parameters for a resource type are the required and conflicted
	 * resource types
	 * 
	 * @author Groep 8
	 */
	public static class ResourceTypeBuilder {

		private final String name;
		private Set<ResourceType> requiredResourceTypes = new LinkedHashSet<ResourceType>();
		private Set<ResourceType> conflictedResourceTypes = new LinkedHashSet<ResourceType>();;
		private TimeInterval dailyAvailability;

		/**
		 * Constructor creates the resourceTypeBuilder with the required
		 * parameters: name and resourceExpert.
		 * 
		 * @param name
		 *            : required name of a resource type
		 * @param resourceExpert
		 *            : resource expert
		 */
		public ResourceTypeBuilder(String name) {
			this.name = name;
			dailyAvailability = new TimeInterval(WorkDay.getStartTime(), WorkDay.getEndTime());
		}

		/**
		 * If the resource type being build has other required resource types,
		 * then add them one at a time.
		 * 
		 */
		public ResourceTypeBuilder addRequiredResourceTypes(
				ResourceType requiredResourceType) {
			this.requiredResourceTypes.add(requiredResourceType);
			return this;
		}

		/**
		 * If the resource type being build has other conflicted resource types,
		 * then add them one at a time.
		 */
		public ResourceTypeBuilder addConflictedResourceTypes(
				ResourceType conflictedResourceType) {
			this.conflictedResourceTypes.add(conflictedResourceType);
			return this;
		}

		public ResourceTypeBuilder addDailyAvailability(
				TimeInterval dailyAvailability) {
			this.dailyAvailability = dailyAvailability;
			return this;
		}

		/**
		 * Builds a resource type after all the optional values have been set.
		 */
		public ResourceType build(ResourceExpert resourceExpert) {
			ResourceType resourceType = new ResourceType(this);
			resourceExpert.addResourceType(resourceType);
			return resourceType;
		}
	}

	/**
	 * Returns a new resource type builder to add extra parameters such as
	 * other required resource types and other conflicted resource types
	 * 
	 * @param name : required name of a resource type
	 * @return resourceTypeBuilder : new builder for creating resource types
	 */
	public static ResourceTypeBuilder builder(String name){
		return new ResourceTypeBuilder(name);
	}
	
	/**
	 * 
	 * @param builder
	 */
	public ResourceType(ResourceTypeBuilder builder) {
		setName(builder.name);
		setDailyAvailability(builder.dailyAvailability);
		addMultipleRequiredResourceTypes(builder.requiredResourceTypes);
		addMultipleConflictedResourceTypes(builder.conflictedResourceTypes);
	}

	/**
	 * Creates a new resource for the resource types and adds the new resource
	 * to the set of a resources
	 * 
	 * @param name
	 *            : name of resource
	 */
	public void createResource(String name) {
		Resource resource = new Resource(name);
		this.addResource(resource);
	}

	/**
	 * Adds the resource to the set of resources if and only if the given
	 * resources is valid. This means the given resource is not null and not
	 * already in the set.
	 * 
	 * @param resource
	 *            : given resource
	 * @throws IllegalArgumentException
	 *             : if the given resource is already in the resource set or
	 *             null
	 */
	void addResource(Resource resource) {
		if (!canHaveResource(resource)) {
			throw new IllegalArgumentException(
					"The resource type has already the given resource.");
		}
		this.resources.add(resource);
	}

	/**
	 * Checks whether the given resource is valid
	 * 
	 * @param resource
	 *            : given resource
	 * @return true if and only if the resource is not already in the set and
	 *         not null
	 */
	boolean canHaveResource(Resource resource) {
		return (!getAllResources().contains(resource) && resource != null);
	}

	/**
	 * For each element in the given set of resource types, add to the set of
	 * required resource types
	 * 
	 * @param requiredResourceTypes
	 *            : set of required resource types
	 */
	private void addMultipleRequiredResourceTypes(
			Set<ResourceType> requiredResourceTypes) {
		for (ResourceType requiredResourceType : requiredResourceTypes) {
			this.addRequiredResourceType(requiredResourceType);
		}
	}

	/**
	 * Adds an resource type to the required resource type set of the resource
	 * type
	 * 
	 * @param requiredResourceType
	 *            : resource type
	 * @throws IllegalArgumentExcption
	 *             : if the given resource type creates a loop
	 * @throws IllegalArgumentException
	 *             : if the given resource type is already in the list
	 */
	public void addRequiredResourceType(ResourceType requiredResourceType) {
		if (requiredResourceType.hasRequiredResourceType(this))
			throw new IllegalArgumentException(
					"Tried to create a required resource type loop.");
		if (!isValidRequiredResourceType(requiredResourceType)) {
			throw new IllegalArgumentException(
					"The given resource type is already required or conflicted on this resource type");
		} else {
			requiredResourceTypes.add(requiredResourceType);
		}
	}

	/**
	 * Checks whether a resource type has required resource types
	 * 
	 * @param resourceType
	 *            : required resource type
	 * @return true if the resource type has the given resourceType as required
	 *         resource
	 */
	boolean hasRequiredResourceType(ResourceType resourceType) {
		if (getRequiredResourceTypes().contains(resourceType))
			return true;
		for (ResourceType requiredResourceType : getRequiredResourceTypes())
			if (requiredResourceType.hasRequiredResourceType(resourceType))
				return true;
		return false;
	}

	/**
	 * Checks whether the given resource is valid as required resource type
	 * 
	 * @param resource
	 *            : given resource
	 * @return true if and only if the resource is not already in the set of
	 *         required resource type
	 */
	private boolean isValidRequiredResourceType(ResourceType resourceType) {
		return !(this.getRequiredResourceTypes().contains(resourceType) || this
				.getConflictedResourceTypes().contains(resourceType));
	}

	/**
	 * For each element in the given set of resource types, add to the set of
	 * conflicted resource types
	 * 
	 * @param conflictedResourceTypes
	 *            : set of conflicted resource types
	 */
	private void addMultipleConflictedResourceTypes(
			Set<ResourceType> conflictedResourceTypes) {
		for (ResourceType conflictedResourceType : conflictedResourceTypes) {
			this.addConflictedResourceType(conflictedResourceType);
		}
	}

	/**
	 * Adds an resource type to the conflicted resource type set of the resource
	 * type
	 * 
	 * @param conflictedResourceType
	 *            : resource type
	 */
	public void addConflictedResourceType(ResourceType conflictedResourceType) {
		if (conflictedResourceType.hasConflictedResourceType(this))
			throw new IllegalArgumentException(
					"Tried to create a required resource type loop.");
		if (!isValidConflictedResourceType(conflictedResourceType)) {
			throw new IllegalArgumentException(
					"The given resource type is already requires on this resource type");
		} else {
			conflictedResourceTypes.add(conflictedResourceType);
		}
	}

	/**
	 * Checks whether a resource type has conflicted resource types
	 * 
	 * @param resourceType
	 *            : conflicted resource type
	 * @return true if the resource type has the given resourceType as
	 *         conflicted resource
	 */
	boolean hasConflictedResourceType(ResourceType resourceType) {
		if (getConflictedResourceTypes().contains(resourceType))
			return true;
		for (ResourceType conflictedResourceType : getConflictedResourceTypes())
			if (conflictedResourceType.hasConflictedResourceType(resourceType))
				return true;
		return false;
	}

	/**
	 * Checks whether the given resource is valid as conflicted resource type
	 * 
	 * @param resource
	 *            : given resource
	 * @return true if and only if the resource is not already in the set of
	 *         conflicted resource type
	 */
	private boolean isValidConflictedResourceType(ResourceType resourceType) {
		return !(this.getConflictedResourceTypes().contains(resourceType) || this
				.getRequiredResourceTypes().contains(resourceType));
	}

	/**
	 * Sets the resource type name
	 * 
	 * @param name
	 */
	private void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the name of the resource type
	 * 
	 * @return name : name of resource type
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the resource type daily availability
	 * 
	 * @param dailyAvailability
	 */
	private void setDailyAvailability(TimeInterval dailyAvailability) {
		this.dailyAvailability = dailyAvailability;
	}

	/**
	 * Returns the daily availability of the resource type
	 * 
	 * @return dailyAvailability : daily availability of resource type
	 */
	public TimeInterval getDailyAvailability() {
		return dailyAvailability;
	}

	/**
	 * Returns a set of all the resources of the resource types
	 * 
	 * @return resources : set with resources
	 */
	public Set<Resource> getAllResources() {
		return Collections.unmodifiableSet(resources);
	}

	/**
	 * Returns the required resource types of a resource type. If the resource
	 * type does not require other types then it returns an empty set.
	 * 
	 * @return requiredResourceTypes : set of required resource types
	 */
	public Set<ResourceType> getRequiredResourceTypes() {
		return Collections.unmodifiableSet(requiredResourceTypes);
	}

	/**
	 * Returns the conflicted resource types of a resource type. If the resource
	 * type does not conflict with other types then it returns an empty set.
	 * 
	 * @return conflictedResourceTypes : set of conflicted resource types
	 */
	public Set<ResourceType> getConflictedResourceTypes() {
		return Collections.unmodifiableSet(conflictedResourceTypes);
	}
	
	
	void save() {
		this.memento = new Memento(this);
	}

	boolean load() {
		if (this.memento == null) {
			return false;
		} else {
			this.memento.load(this);
			return true;
		}
	}

	private class Memento {
		private String name;
		private Set<ResourceType> requiredResourceTypes;
		private Set<ResourceType> conflictedResourceTypes;
		private Set<Resource> resources;
		private TimeInterval dailyAvailability;

		public Memento(ResourceType rt) {
			this.name = new String(rt.name);
			this.requiredResourceTypes = new LinkedHashSet<ResourceType>(rt.requiredResourceTypes);
			this.conflictedResourceTypes = new LinkedHashSet<ResourceType>(rt.conflictedResourceTypes);
			this.resources = new LinkedHashSet<Resource>(rt.resources);;
			this.dailyAvailability = rt.dailyAvailability;
		}

		public void load(ResourceType rt) {
			rt.name = this.name;
			rt.conflictedResourceTypes = this.conflictedResourceTypes;
			rt.requiredResourceTypes = this.requiredResourceTypes;
			rt.resources = this.resources;
			rt.dailyAvailability = this.dailyAvailability;
		}
	}
}
