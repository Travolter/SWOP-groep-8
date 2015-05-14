package taskmanager;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A company consists of multiple branch offices.
 * 
 * @author menno
 *
 */
public class Company {

	private Set<BranchOffice> branchOffices;
	private ImmutableClock clock;

	/**
	 * Default constructor of Company class. Initializes a new set of branch
	 * offices
	 */
	Company(ImmutableClock clock){
		this.clock = clock;
		this.branchOffices = new LinkedHashSet<BranchOffice>();
	}

	/**
	 * Creates a new branch office with the given name. and adds the new branch
	 * office to the set of all branch offices
	 * 
	 * @param name
	 *            : given name
	 */
	BranchOffice createBranchOffice( String location) {
		BranchOffice branchOffice = new BranchOffice(location, clock);
		this.addBranchOffice(branchOffice);
		return branchOffice;
	}

	/**
	 * Creates a new branch office with the given name. and adds the new branch
	 * office to the set of all branch offices
	 * 
	 * @param name
	 *            : given name
	 */
	BranchOffice createBranchOffice(String location, TaskManClock clock) {
		BranchOffice branchOffice = new BranchOffice(location, clock);
		this.addBranchOffice(branchOffice);
		return branchOffice;
	}

	/**
	 * Adds the branch office to the set of branch offices if and only if the
	 * given branch office is valid. This means the given branch office is not
	 * null and not already in the set.
	 * 
	 * @param branchOffice
	 *            : given branchOffice
	 * 
	 * @throws IllegalArgumentException
	 *             : if the branch office is a null object or the branch office
	 *             already exists
	 */
	@NonNull
	private void addBranchOffice(BranchOffice branchOffice) {
		if (getAllBranchOffices().contains(branchOffice)) {
			throw new IllegalArgumentException(
					"The given branch office already exists");
		}
		this.branchOffices.add(branchOffice);
	}

	/**
	 * Returns the unmodifiable set of all branch offices of the company
	 * 
	 * @return branchOffices : set of all branchOffices
	 */
	Set<BranchOffice> getAllBranchOffices() {
		return Collections.unmodifiableSet(branchOffices);
	}

}
