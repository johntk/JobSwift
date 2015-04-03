package controllers;

import models.*;
import play.mvc.*;
import java.util.*;
import play.data.Form;
import play.libs.Json;
import play.libs.Json.*;                        
import static play.libs.Json.toJson;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ApplicantController extends Controller {
	
	private static final Form<ApplicantModel> appForm = Form.form(ApplicantModel.class);
	
	// Method to retrieve all applicants in the database
	public static Result listApplicants() {
    	List<ApplicantModel> applicants = ApplicantModel.findAll();
    	return ok(views.html.Applicant.ApplicantList.render(applicants));
    }
	
	// Overloaded method to retrieve applicants by email
	public static Result listNewApplicants(int processed) {
    	List<ApplicantModel> applicants = ApplicantModel.findAll();
    	return ok(views.html.Applicant.ApplicantList.render(applicants));
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
    	return ok(views.html.Applicant.ApplicantDetails.render(filledForm));
    }
    
    // Form to update applicant details from recruiters screen *UNUSED*
    public static Result updateApplicant() {
        Form<ApplicantModel> boundForm = appForm.bindFromRequest();
        if(boundForm.hasErrors())
        {
          flash("error", "Please correct the form below.");
          return badRequest(views.html.Applicant.ApplicantDetails.render(boundForm));
        }

        ApplicantModel app = boundForm.get();
        
        if(app.applicant_id == null)
        {
        	app.update();
            flash("success", String.format("Successfully updated user %s", app));
            return redirect(routes.ApplicantController.listApplicants());
        } else return redirect(routes.Application.dashboard());
    }
    
    public static Result delete(String email) {
    	final ApplicantModel app = ApplicantModel.findByEmail(email);
    	if(app == null) {
    		return notFound(String.format("Applicant %s does not exist.", email));
    	}

    	app.delete();
    	return redirect(routes.ApplicantController.listApplicants());
    }
	
}
