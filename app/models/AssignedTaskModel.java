package models;


import java.util.List;

import javax.persistence.*;

import play.db.ebean.Model;

@Entity
public class AssignedTaskModel extends Model {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	public Long assignedTaskId;
	
	@ManyToOne
	public EmployeeModel emp;
	
	@OneToOne
	public JobApplicationModel jobApp;
	
	// Default Constructor
	public AssignedTaskModel() {
		
	}
	
	public static Finder<Long, AssignedTaskModel> find = new Finder<Long, AssignedTaskModel>(Long.class, AssignedTaskModel.class);
	
	// Return task assigned to a particular job listing
	public static AssignedTaskModel findTaskByJobApplication(JobApplicationModel jam){
		AssignedTaskModel temp = find.where().eq("jobApp.job_application_id", jam.job_application_id).findUnique();
		return temp;
	}
	
	// Find all assigned tasks for an employee
	public static List<AssignedTaskModel> findAllTasksByEmployee(EmployeeModel emp){
		List<AssignedTaskModel> temp = find.where().eq("emp.employee_id", emp.employee_id).findList();
		return temp;
	}
}
