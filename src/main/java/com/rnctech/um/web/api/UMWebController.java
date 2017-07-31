package com.rnctech.um.web.api;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.rnctech.um.web.utils.UMWebUtils;

/**
 * @contributor zilin
 * 
 */

@RestController
public class UMWebController {

	@Autowired
	private Environment env;

	@RequestMapping("/ping")
	public String ping() {
		return "Ping UMWeb successed!";
	}

	@RequestMapping("/role")	
	public String getLoginNameAndRole() {
		
		Authentication authentication = UMWebUtils.getAuthentication();
		if (authentication != null) {
			List<String> roles = UMWebUtils.getRolesOfUser();
			return authentication.getName()+ ":"
					+ roles.toString();
		}else{
			return "No user or role found!";
		}

	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> uploadFile(
			@RequestParam("uploadfile") MultipartFile uploadfile) {

		try {
			String filename = uploadfile.getOriginalFilename();
			String directory = env.getProperty("um.upload.folder");
			String filepath = Paths.get(directory, filename).toString();
			File basefolder = new File(directory);
			if(!basefolder.exists())
				basefolder.mkdirs();

			// Save the file locally
			BufferedOutputStream stream = new BufferedOutputStream(
					new FileOutputStream(new File(filepath)));
			stream.write(uploadfile.getBytes());
			stream.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}
	


}
