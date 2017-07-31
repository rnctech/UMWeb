#!/bin/bash

cd /tmp
if [ "$JOB_NAME" != "" ]; then

    echo "using jobName " $JOB_NAME
	
	java -cp log4j-1.2.17.jar -Dlog4j.configuration=file:log4j.properties com.rnctech.cd.TestMain --user useradmin@rnctech.com --password Admin123 --command "{\"name\":\"test\",\"jobName\":\"myjob\",\"jobType\":\"MAIN\",\"jobProperties\":{}}"

else
    echo "please provide a job name as \n export JOB_NAME=myjob"
fi

bash



