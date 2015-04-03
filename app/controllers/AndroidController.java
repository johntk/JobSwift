package controllers;

import play.mvc.*;

import java.util.Map;

import play.mvc.Http.RequestBody;

import java.io.File;

import play.libs.Json;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class AndroidController extends Controller {


	public static Result json(){
	
				ObjectNode result = Json.newObject();
				result.put("content", "Hello John");
				return ok(result);
			}
			

	public static Result jsonPost(){
			
		RequestBody body = request().body();
		Http.MultipartFormData multippartBody = body.asMultipartFormData();
		Http.MultipartFormData.FilePart imageFile = multippartBody.getFile("fileKey");
		Map<String, String[]> myMap  = multippartBody.asFormUrlEncoded();
		 System.out.println(myMap.toString());
		
			if(imageFile.getFile() != null) {
				File file = imageFile.getFile(); 
				String fileName = imageFile.getFilename();
				File newDir = new File("/public/images/","RecruitSwift");
		        if(!newDir.isDirectory()){
		            newDir.mkdirs();
		            System.out.println("Created dir");
		        }
		        if(newDir.canWrite()){
		        	file.renameTo(new File(newDir, fileName));
		        }
		        return ok("Got: " + "it");
			} 
			else {
				return badRequest("Expecting text/plain request body");
		}
	}
}