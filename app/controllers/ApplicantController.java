package controllers;

import models.*;
import play.mvc.*;

import java.util.*;

import controllers.Application.Login;
import play.data.Form;

public class ApplicantController extends Controller {
	
	private static final Form<ApplicantModel> appForm = Form.form(ApplicantModel.class);
	
	// Method to retrieve all applicants in the database
	public static Result listApplicants() {
    	List<ApplicantModel> applicants = ApplicantModel.findAll();
    	return ok(views.html.Applicant.ApplicantList.render(applicants));
    }
	
	// Displays all job applciations in the recruiters dashboard
	public static Result listAllJobApplications() {
    	List<JobApplicationModel> jobApps = JobApplicationModel.findAll();
    	return ok(views.html.Recruiter.JobApplicationList.render(jobApps));
    }
    
    public static Result newApplicant() {
    	return ok(views.html.Applicant.ApplicantDetails.render(appForm));
    }
    
    public static Result details(String email) {
    	final ApplicantModel app = ApplicantModel.findByEmail(email);
    	if (app == null) {
    		return notFound(String.format("Applicant %s does not exist.", email));
    	}
    	
    	Form<ApplicantModel> filledForm = appForm.fill(app);
    	return ok(views.html.Applicant.WebsiteApplicantDetails.render(filledForm, Form.form(Login.class)));
    }
    
    // Form to update applicant details
    public static Result updateApplicant() {
        Form<ApplicantModel> boundForm = appForm.bindFromRequest();
        if(boundForm.hasErrors())
        {
          flash("error", "Please correct the form below.");
          return badRequest(views.html.Applicant.WebsiteApplicantDetails.render(boundForm, Form.form(Login.class) ));
        }

        ApplicantModel app = boundForm.get();
        
        if(!app.applicant_password.equals(app.applicant_password_confirmation)) {
			flash("error", "Passwords do not match!");
			return redirect(routes.ApplicantController.details(app.applicant_email));
		}
        
        if(app.applicant_id != null)
        {
        	app.update();
            flash("success", String.format("Successfully updated user %s", app));
            return redirect(routes.Application.applicantProfile());
        } else return redirect(routes.Application.applicantProfile());
    }
    
 // Delete method to delete user from recruiters dashboard
    public static Result deleteUser(String email) {
    	final ApplicantModel app = ApplicantModel.findByEmail(email);
    	if(app == null) {
    		return notFound(String.format("Applicant %s does not exist.", email));
    	}
    	
    	List<JobApplicationModel> jam = JobApplicationModel.findAllApplicationsByUser(app);
    	if(jam != null) {
	    	for(int i=0; i<jam.size(); i++) {
	    		jam.get(i).delete();
	    	}
    	}
    	app.delete();
    	return redirect(routes.ApplicantController.listApplicants());
    }
    
    // Method to delete user from user profile
    @Security.Authenticated(Secured.class)
    public static Result deleteCurrentUser() {
    	ApplicantModel app = Application.getCurrentUser();
    	Application.logOut();
    	List<JobApplicationModel> jam = JobApplicationModel.findAllApplicationsByUser(app);
    	if(jam != null) {
	    	for(int i=0; i<jam.size(); i++) {
	    		jam.get(i).delete();
	    	}
    	}
    	app.delete();
    	
    	flash("success","Account Deleted");
    	return redirect(routes.Application.index());
    }

}
