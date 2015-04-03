package controllers;

import java.util.ArrayList;
import java.util.List;

import models.*;
import play.data.Form;
import play.mvc.*;
import play.mvc.Security;

public class Application extends Controller {

	// Create a form to be passed to main website for applicant sign up
	private static final Form<ApplicantModel> appForm = Form.form(ApplicantModel.class);
	private static ApplicantModel appMod;

	// Render the homepage for the main website
	public static Result index() {
		return ok(views.html.MainWebsite.Homepage.render(appForm, Form.form(Login.class)));
	}
	
	// Render the job listings view for the main website
	public static Result jobListings() {
		List<JobListingModel> jobList = JobListingModel.findAll();
		return ok(views.html.MainWebsite.WebsiteJobList.render(jobList, Form.form(Login.class)));
	}
	
	// Render the applicant profile view for the main website
	@Security.Authenticated(Secured.class)
	public static Result applicantProfile() {
		appMod = new ApplicantModel().findByEmail(request().username());
		return ok(views.html.Applicant.ApplicantProfile.render(appMod, Form.form(Login.class)));
	}
	
	// Render the dashboard view for the recruiters page
	public static Result dashboard() {
		// Create a list of unprocessed applicants to pass to dashboard view
		List<ApplicantModel> newApplicants = ApplicantModel.findUnprocessed();
		// Create a list of all job listings to pass to dashboard view
		List<JobListingModel> jobList = JobListingModel.findAll();
		return ok(views.html.Recruiter.Dashboard.render(newApplicants.size(), jobList.size(), newApplicants, jobList));
	}

	// Method for submitting sign-up form from main website
	public static Result submitForm() {
		Form<ApplicantModel> boundForm = appForm.bindFromRequest();
		if (boundForm.hasErrors()) {
			flash("error", "Please correct the form below.");
			return badRequest(views.html.MainWebsite.Homepage.render(boundForm, Form.form(Login.class)));
		}

		ApplicantModel app = boundForm.get();

		if (app.applicant_id == null) {
			if (ApplicantModel.findByEmail(app.applicant_email) != null) {
				flash("error",
						String.format("User %s is already registered!", app));
				return redirect(routes.Application.index());
			} else {
				app.save();
				flash("success", "Sign up successful");
				return redirect(routes.Application.index());
			}
		} else
			return redirect(routes.Application.index());
	}

	// Creates a list of title options for the sign up form
	public static final List<String> formTitleOptions() {
		List<String> list = new ArrayList<String>();
		list.add("");
		list.add("Mr");
		list.add("Ms");
		return list;
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
		
		if(ApplicantModel.authenticateApplicant(email, password) == null) {
			// Clear the existing session
			session().clear();
			flash("error", "Invalid Login Credentials!");
			return badRequest(views.html.MainWebsite.Homepage.render(appForm, Form.form(Login.class)));
		}
		
		// Clear the existing session
		session().clear();
		// Add users email to the session
		session("email", email);
		
		// Redirect to homepage
		flash("success", "Login Successful!");
		return redirect(routes.Application.index());
	}
}


