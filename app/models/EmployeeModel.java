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
	
	public EmployeeModel(String employee_userName, String employeePassword, String employee_firstName, String employee_lastName) {
		this.employee_userName = employee_userName;
		this.employeePassword = employeePassword;
		this.employee_firstName = employee_firstName;
		this.employee_lastName = employee_lastName;
	}
	
	
	public static Finder<Long, EmployeeModel> find = new Finder<Long, EmployeeModel>(Long.class, EmployeeModel.class);

	public static EmployeeModel findByUserName(String userName) {
		return find.where().eq("employee_userName", userName).findUnique();
	}
	
	// Authenticate a user based on email and password
	public static EmployeeModel authenticateEmployee(String userName, String password) {
		EmployeeModel emp = EmployeeModel.findByUserName(userName);
		return emp;
//		if(app != null && BCrypt.checkpw(password, app.applicant_password)) {
//			return app;
//		}else return null;
	}
	
}
