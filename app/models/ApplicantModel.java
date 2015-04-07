package models;

import java.util.Date;
import java.util.List;

import controllers.DateTime;

import javax.persistence.*;

import play.db.ebean.Model;
import play.data.validation.Constraints;

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
	@Constraints.Required
	@Constraints.MinLength(6)
	public String applicant_password_confirmation;
	
	@OneToMany
	public List<JobApplicationModel> applicationList;

	public String cvFilePath;
	public String cvFileName;
	
	public String profileImage;
	public String introVideoPath;

	public DateTime dateTime;
	public Date dateOfSignup = new Date();

	// Default Constructor
	public ApplicantModel() {
	}

	// Overloaded Constructor
	public ApplicantModel(String email, String title, String firstName, String lastName, String city, String password, String passwordConfirm) {
		applicant_email = email;
		applicant_title = title;
		applicant_firstName = firstName;
		applicant_lastName = lastName;
		applicant_city = city;
		applicant_password = password;
		applicant_password_confirmation = passwordConfirm;

		dateOfSignup = dateTime.getDateTime();
	}

	public String toString() {
		return String.format("%s %s - %s", applicant_firstName,
				applicant_lastName, applicant_email);
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
		return find.where().eq("applicant_email", email).eq("applicant_password", password).findUnique();
	}
	
}