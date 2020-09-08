package com.rnctech.umweb.api;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.rnctech.umweb.job.UMJobData;
import com.rnctech.umweb.service.UMJOBService;
import com.rnctech.umweb.utils.UMWebUtils;

/**
 * @contributor zilin
 * 
 */

@RestController
@RequestMapping(value = "/api/v1")
public class UMJobController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UMJobController.class);

	@Autowired
	private UMJOBService jobservice;
	
	@Autowired
	private Environment env;

	@RequestMapping(value = "/run/{name}", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> extract(@PathVariable("name") String name,
			@RequestBody UMJobData jobdata , HttpServletRequest request,
			HttpServletResponse response) {
		
		String runIdentifier = UMWebUtils.getRunID(name);
		jobdata.setJobkey(runIdentifier);
		jobservice.scheduleUMJob(jobdata);			
		return new ResponseEntity<String>(runIdentifier, HttpStatus.OK);
	}


	@RequestMapping(value = "/run/status", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getJobStatus(HttpServletRequest request, HttpServletResponse response) {		
		String execjobs = jobservice.getExecutingJobs();		
		return new ResponseEntity<String>(execjobs, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/run/status/{name}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getJobHistory(@PathVariable("name") String name, HttpServletRequest request, HttpServletResponse response) {		
		String execjobs = jobservice.getJobHistory(name);		
		return new ResponseEntity<String>(execjobs, HttpStatus.OK);
	}
	
	@RequestMapping(value="cancel/{name}")
	@ResponseBody
	public ResponseEntity<?> cancel(@PathVariable("name") String name, HttpServletRequest request,
			HttpServletResponse response) {
		String ret = "Try to cancel job "+name;
		ret = jobservice.cancelumJob(name);

		return new ResponseEntity<String>(ret, HttpStatus.OK);
	}


}
