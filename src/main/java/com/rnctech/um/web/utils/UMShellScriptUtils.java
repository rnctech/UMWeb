package com.rnctech.um.web.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rnctech.um.web.job.UMJobData;

public class UMShellScriptUtils {

	public static final String UMWEB_JOB_CMD = "command";
    
	public static String[] buildArgs(UMJobData config, Logger logger) {
		List<String> arglist = buildArgsList(config,logger);
		String[] args = new String[arglist.size()];
		arglist.toArray(args);
		return args;

	}
	
	public static List<String> buildArgsList(UMJobData config, Logger logger){
		ObjectMapper oMapper = new ObjectMapper();
		Map<String, Object> amap = oMapper.convertValue(config, Map.class);
		try {
			amap.remove(UMWEB_JOB_CMD);
			amap.put(UMWEB_JOB_CMD,
					oMapper.writer().writeValueAsString(config.getCommand())
							.replaceAll("\"", "\\\""));
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage());
		}
		List<String> arglist = new ArrayList<>();
		for (Map.Entry<String, Object> entry : amap.entrySet()) {
			Object o = entry.getValue();
			if (null != o) {
				arglist.add("--" + entry.getKey().trim().toLowerCase());
				arglist.add(entry.getValue().toString());
			}
		}
		return arglist;
	}


	public static Object executeShellCommand(String command, boolean waitForResponse,
			Logger logger) {
		ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
		return executeCommand(pb, waitForResponse, logger);
	}
	
	public static Object executeJava(UMJobData config,  boolean waitForResponse,
			Logger logger){
	    ArrayList<String> cmd = new ArrayList<String>();
	    cmd.add("java");
	    cmd.add("-cp");
	    cmd.add("httpcore-4.4.4.jar:httpclient-4.4.1.jar:guava-20.0.jar:log4j-1.2.17.jar:org.apache.commons.codec-1.3.0.jar:cxf-bundle-2.7.5.jar:javax.ws.rs-api-2.0.1.jar:javax.ws.rs-api-2.0-m10.jar:neethi-3.0.2.jar:httpcore-nio-4.2.2.jar:httpasyncclient-4.0-beta3.jar:wsdl4j-1.6.3.jar:commons-io-2.4.jar");
	    cmd.add("-Dlog4j.configuration=file:log4j.properties");
	    cmd.add("com.rnctech.platform.TestMain");
	    cmd.addAll(buildArgsList(config, logger));
	    logger.info("build java command to run as:\n"+UMWebUtils.aTos(cmd.toArray(new String[cmd.size()])));
	    ProcessBuilder pb = new ProcessBuilder(cmd.toArray(new String[cmd.size()]));	
        Map<String, String> env = pb.environment();
        env.put("JOB_NAME", config.getJobkey());
		return executeCommand(pb, waitForResponse, logger);
	}
	
	public static Object executeCommand(ProcessBuilder pb, boolean waitForResponse,
			Logger logger) {
		String response = "";
		pb.directory(new File(System.getenv("user.home")));        
		pb.redirectErrorStream(true);
		logger.info("Run command: " + pb.command());
		try {
			Process aprocess = pb.start();
			if (waitForResponse) {
				InputStream shellIn = aprocess.getInputStream();
				int shellExitStatus = aprocess.waitFor();
				logger.info("Exit status" + shellExitStatus);
				response = printStream(shellIn);
				shellIn.close();
			}
			else{
				return aprocess;
			}
		}catch (Exception e) {
			logger.error("Error occured while executing command " + pb.command()
					+ ".\n" + e.getMessage());
		}
		return response;
	}

	public static String printStream(InputStream is) throws IOException {
		if (is != null) {
			Writer writer = new StringWriter();
			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(
						new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}
	
    public static void printFile(File file, Logger logger) throws IOException {
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;
        while ((line = br.readLine()) != null) {
        	if(null != logger)
        		logger.info(line+"\n");
        	else
        		System.out.println(line);
        }
        br.close();
        fr.close();
    }

/*    public static void main(String [] args) throws IOException {        
        String[] command = {"CMD", "/C", "java -cp httpcore-4.4.4.jar:httpclient-4.4.1.jar:guava-20.0.jar:log4j-1.2.17.jar:org.apache.commons.codec-1.3.0.jar:cxf-bundle-2.7.5.jar:javax.ws.rs-api-2.0.1.jar:javax.ws.rs-api-2.0-m10.jar:neethi-3.0.2.jar:httpcore-nio-4.2.2.jar:httpasyncclient-4.0-beta3.jar:wsdl4j-1.6.3.jar:commons-io-2.4.jar -Dlog4j.configuration=file:log4j.properties com.rnctech.platform.TestMain --url http://192.168.211.1:8080/UMWeb/ --user useradmin@rnctech.com --password Admin123 --command \"{\"tenantName\":\"test\",\"sourceSystemName\":null,\"sourceSystemInstanceName\":null,\"entityCategory\":\"cs_servicenow_tenant_ec\",\"jobName\":\"myjob\",\"externalParentJobId\":0,\"jobType\":\"ETL\",\"loadType\":\"FULL\",\"jobProperties\":{}}\""};
        ProcessBuilder probuilder = new ProcessBuilder( command );
        probuilder.directory(new File("c:\\tmp"));        
        Process process = probuilder.start();
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        System.out.printf("Output of running %s is:\n",
                Arrays.toString(command));
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
        try {
            int exitValue = process.waitFor();
            System.out.println("\n\nExit Value is " + exitValue);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }*/
}
