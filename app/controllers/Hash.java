package controllers;

import play.data.Form;
import play.mvc.*;
import models.ApplicantModel;

import org.mindrot.jbcrypt.BCrypt;

public class Hash extends Controller{
	
	public static class HashForm {
		public String string_value;
	}
	
	public static Result hashing() {
		return ok(views.html.Recruiter.hashing.render(Form.form(HashForm.class)));
	}
	
	public static Result hashValue() {
		Form<HashForm> loginForm = Form.form(HashForm.class).bindFromRequest();
		String formValue = loginForm.get().string_value;
		System.out.println(formValue);
		String result = BCrypt.hashpw(loginForm.get().string_value, BCrypt.gensalt());
		flash("result", result);
		return redirect(routes.Hash.hashing());
	}
	
}
