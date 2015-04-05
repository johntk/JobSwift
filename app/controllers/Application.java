package controllers;

import java.util.List;

import models.*;
import play.data.Form;
import play.mvc.*;

public class Application extends Controller {

	// Create a form to be passed to main website for applicant sign up
	private static final Form<ApplicantModel> appForm = Form.form(ApplicantModel.class);
	private static ApplicantModel appMod = new ApplicantModel();

	// Render the homepage for the main website
	public static Result index() {
		return ok(views.html.MainWebsite.Homepage.render(appForm, Form.form(Login.class) ));
	}
	
	
	
	// Render the applicant profile view for the main website
	@Security.Authenticated(Secured.class)
	public static Result applicantProfile() {
		appMod = getCurrentUser();
		List<JobApplicationModel> jobAppList = JobApplicationModel.findAllApplicationsByUser(ApplicantModel.findByEmail(request().username()));
		return ok(views.html.Applicant.ApplicantProfile.render(appMod, jobAppList, Form.form(Login.class) ));
	}
	
	// Render the dashboard view for the recruiters page
	public static Result dashboard() {
		// Create a list of all job listings to pass to dashboard view
		List<JobListingModel> jobList = JobListingModel.findAll();
		return ok(views.html.Recruiter.Dashboard.render(jobList.size(), jobList));
	}

	// Method for submitting sign-up form from main website
	public static Result submitForm() {
		Form<ApplicantModel> boundForm = appForm.bindFromRequest();
		if (boundForm.hasErrors()) {
			flash("error", "Please correct the form below.");
			return badRequest(views.html.MainWebsite.Homepage.render(boundForm, Form.form(Login.class) ));
		}

		ApplicantModel app = boundForm.get();
		
		if(!app.applicant_password.equals(app.applicant_password_confirmation)) {
			flash("error", "Passwords do not match!");
			return redirect(routes.Application.index());
		}
		
		if (app.applicant_id == null) {
			if (ApplicantModel.findByEmail(app.applicant_email) != null) {
				flash("error", String.format("User %s is already registered!", app));
				return redirect(routes.Application.index());
			} else {
				app.save();
				FileUploadController.createUserFolder(app.applicant_email);
				flash("success", "Sign up successful");
				return redirect(routes.Application.index());
			}
		} else
			return redirect(routes.Application.index());
	}
	
	// Inner class to store user credentials for logging in
	public static class Login {
		public String applicant_email;
		public String applicant_password;
	}
	
	// Process the applicant login form from the homepage
	public static Result authenticateApplicant() {
		Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
		String email = loginForm.get().applicant_email;
		String password = loginForm.get().applicant_password;
		
		// Check if the user exists in the database
		if(ApplicantModel.authenticateApplicant(email, password) == null) {
			// Clear the existing session
			session().clear();
			flash("error", "Invalid Login!");
			return badRequest(views.html.MainWebsite.Homepage.render(appForm, Form.form(Login.class) ));
		}
		
		// Clear the existing session
		session().clear();
		// Add users email to the session
		session("email", email);
		// Redirect to homepage
		flash("success", "Login Successful!");
		return redirect(routes.Application.index());
	}
	
	@Security.Authenticated(Secured.class)
	public static ApplicantModel getCurrentUser() {
		appMod = ApplicantModel.findByEmail(request().username());
		return appMod;
	}

	public static Result logOut() {
		session().clear();
		return redirect(routes.Application.index());
	}
}


