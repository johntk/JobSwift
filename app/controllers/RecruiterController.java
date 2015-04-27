package controllers;

import java.util.List;

import controllers.Application.Login;
import models.*;
import play.data.Form;
import play.mvc.*;


public class RecruiterController extends Controller{
	
	public static Result recruiterLogin() {
		return ok(views.html.Recruiter.RecruitersLogin.render(Form.form(EmpLogin.class)));
	}
	
	// Inner class to store user credentials for logging to recruiter's dashboard
	public static class EmpLogin {
		public String employee_userName;
		public String employeePassword;
	}
	
	public static Result authenticateEmployee() {
		Form<EmpLogin> loginForm = Form.form(EmpLogin.class).bindFromRequest();
		String userName = loginForm.get().employee_userName;
		String password = loginForm.get().employeePassword;
		
		// Check if the user exists in the database
		if(EmployeeModel.authenticateEmployee(userName, password) == null) {
			// Clear any existing user from the session
			if(session().containsKey("username")) {
				session().remove("username");
			}
			flash("error", "Invalid Login!");
			return redirect(routes.RecruiterController.recruiterLogin());
		}
		
		// Clear any existing user from the session
		if(session().containsKey("username")) {
			session().remove("username");
		}
		// Add users email to the session
		session("username", userName);
		// Redirect to homepage
		flash("success", "Login Successful!");
		return redirect(routes.RecruiterController.dashboard());
	}
	
	// Render the dashboard view for the recruiters page
	@Security.Authenticated(RecruiterSecured.class)
	public static Result dashboard() {
		// Create a list of all job listings to pass to dashboard view
		List<JobListingModel> jobList = JobListingModel.findAll();
		List<JobApplicationModel> applicationList = JobApplicationModel.findAllUnprocessedApplications();
		List<JobApplicationModel> interviewList = JobApplicationModel.findAllCompleteInterviews();
		return ok(views.html.Recruiter.Dashboard.render(jobList, applicationList, interviewList));
	}
	
	public static Result recruiterLogout() {
		if(session().containsKey("username")) {
			session().remove("username");
		}
		return redirect(routes.RecruiterController.recruiterLogin());
	}

}
