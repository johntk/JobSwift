package controllers;

import play.Play;
import play.mvc.*;

import java.util.Iterator;
import java.util.Map;

import play.mvc.Http.RequestBody;

import java.io.File;

import play.libs.Json;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class AndroidController extends Controller {

	// JSON get request 
	public static Result json(){
	
		ObjectNode result = Json.newObject();
		result.put("content", "Hello John");
		return ok(result);
	}
			

	// MultipartRequest Post update
	public static Result update(){
			
		//get the body of the request
		RequestBody body = request().body();
		//Assign the type as MultipartFormData
		Http.MultipartFormData multippartBody = body.asMultipartFormData();
		// Pull the video from the MultipartFormData
		Http.MultipartFormData.FilePart videoFile = multippartBody.getFile("fileKey");
		// Pull the hashMap from the MultipartFormData
		Map<String, String[]> myMap  = multippartBody.asFormUrlEncoded();
		
		
		Iterator<String> myVeryOwnIterator = myMap.keySet().iterator();
	    while(myVeryOwnIterator.hasNext()) {
	        String key=(String)myVeryOwnIterator.next();
	        String[] value= myMap.get(key);
	        System.out.println(key + " " + value[0]);
	    }
		

		// Create a directory for the Video and add the video to it
			if(videoFile != null) {
				File file = videoFile.getFile(); 
				String fileName = videoFile.getFilename();
				File newDir = new File(FileUploadController.getGlobalUploadFolderAbolutePath(),"videos");
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