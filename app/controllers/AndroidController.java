package controllers;

import play.Play;
import play.mvc.*;

import java.util.Map;

import play.mvc.Http.RequestBody;

import java.io.File;

import play.libs.Json;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class AndroidController extends Controller {
	
	public static String getGlobalUploadFolderAbolutePath() {
		String root = Play.application().path().toString();
		String globalFolderPath = root + "/public/globalUploadFolder/";
		File globalFolder = new File(globalFolderPath);
		
		// Check if the main upload folder exists first, if not create it
		if(!globalFolder.exists()) {
			try{
				globalFolder.mkdir();
			}catch(SecurityException se){
				System.out.println("cannot create global directory");
		    }
		}
		return globalFolderPath;
	}

	// JSON get request 
	public static Result json(){
	
				ObjectNode result = Json.newObject();
				result.put("content", "Hello John");
				return ok(result);
			}
			

	// Multipart Post request
	public static Result jsonPost(){
			
		//get the body of the request
		RequestBody body = request().body();
		//Assign the type as MultipartFormData
		Http.MultipartFormData multippartBody = body.asMultipartFormData();
		// Pull the video from the MultipartFormData
		Http.MultipartFormData.FilePart videoFile = multippartBody.getFile("fileKey");
		// Pull the hashMap from the MultipartFormData
		Map<String, String[]> myMap  = multippartBody.asFormUrlEncoded();
		
		// Loop to print the contents of the hashMap
		for(int i =0; i < myMap.size(); i++){
			String param = "param" + (i + 1);
			System.out.println(myMap.get(param)[i]);	
		}
		

		// Create a directory for the Video and add the video to it
			if(videoFile != null) {
				File file = videoFile.getFile(); 
				String fileName = videoFile.getFilename();
				File newDir = new File(getGlobalUploadFolderAbolutePath(),"videos");
		        if(!newDir.isDirectory()){
		            newDir.mkdirs();
		        }
		        if(newDir.canWrite()){
		        	file.renameTo(new File(newDir, fileName));
		        }
		        return ok();
			} 
			else {
				return badRequest();
		}
	}
}