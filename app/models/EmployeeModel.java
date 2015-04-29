package models;

import java.util.List;

import javax.persistence.*;

import org.mindrot.jbcrypt.BCrypt;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;
import play.data.validation.Constraints;

@Entity
public class EmployeeModel extends Model {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	public Long employee_id;
	
	@Constraints.Required
	public String employee_userName;
	@Constraints.Required
	public String employeePassword;
	@Constraints.Required
	public String employee_firstName;
	@Constraints.Required
	public String employee_lastName;
	
	@OneToMany
	public List<AssignedTaskModel> assignedTasks;
	
	public static EmployeeModel create(String employee_userName, String employeePassword, String employee_firstName, String employee_lastName) {
		EmployeeModel emp = new EmployeeModel();
		emp.employee_userName = employee_userName;
		emp.employeePassword = BCrypt.hashpw(employeePassword, BCrypt.gensalt());
		emp.employee_firstName = employee_firstName;
		emp.employee_lastName = employee_lastName;
		
		emp.save();
		return emp;
	}
	
	
	public static Finder<Long, EmployeeModel> find = new Finder<Long, EmployeeModel>(Long.class, EmployeeModel.class);

	public static EmployeeModel findByUserName(String userName) {
		return find.where().eq("employee_userName", userName).findUnique();
	}
	
	// Authenticate a user based on email and password
	public static EmployeeModel authenticateEmployee(String userName, String empPassword) {
		EmployeeModel emp = EmployeeModel.findByUserName(userName);
		if(emp != null && BCrypt.checkpw(empPassword, emp.employeePassword)) {
			return emp;
		}else return null;
	}
	
}
