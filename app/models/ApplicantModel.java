package models;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import play.db.ebean.Model;
import play.data.format.Formats;
import play.data.validation.Constraints;

import org.mindrot.jbcrypt.BCrypt;

@Entity
public class ApplicantModel extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	public Long applicant_id;

	@Constraints.Required
	@Constraints.Email
	public String applicant_email;
	@Constraints.Required
	public String applicant_title;
	@Constraints.Required
	public String applicant_firstName, applicant_lastName;
	@Constraints.Required
	public String applicant_city;
	@Constraints.Required
	@Constraints.MinLength(6)
	public String applicant_password;
	
	@OneToMany
	public List<JobApplicationModel> applicationList;

	public String cvFilePath;
	public String cvFileName;
	
	public String profileImage;
	public String introVideoPath;

	@Formats.DateTime(pattern="dd/MM/yyyy")
	public Date dateOfSignup = new Date();

	public static ApplicantModel create(String email, String title, String firstName, String lastName, String city, String password) {
		ApplicantModel app = new ApplicantModel();
		app.applicant_email = email;
		app.applicant_title = title;
		app.applicant_firstName = firstName;
		app.applicant_lastName = lastName;
		app.applicant_city = city;
		app.applicant_password = BCrypt.hashpw(password, BCrypt.gensalt());

		app.dateOfSignup = Calendar.getInstance().getTime();
		app.save();
		return app;
	}

	public String toString() {
		return String.format("%s", applicant_email);
	}
	
	public static Finder<Long, ApplicantModel> find = new Finder<Long, ApplicantModel>(Long.class, ApplicantModel.class);

	public static List<ApplicantModel> findAll() {
		return ApplicantModel.find.all();
	}

	public static ApplicantModel findByEmail(String email) {
		return find.where().eq("applicant_email", email).findUnique();
	}
	
	// Authenticate a user based on email and password
	public static ApplicantModel authenticateApplicant(String email, String password) {
		ApplicantModel app = ApplicantModel.findByEmail(email);
		if(app != null && BCrypt.checkpw(password, app.applicant_password)) {
			return app;
		}else return null;
	}
	
}