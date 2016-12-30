Skip a process stopped on a timer in BPM Suite 6.1
--
This project has two commands to skip a timer on BPM Suite 6.1 only, when the executor API was public.

To use it you must:    
* Build this project using `mvn clean package`* Get the generated JAR in target directory and paste on `business-central.war/WEB-INF/lib`* Restart BPM Suite, got to Business Central and go to Deploy -> Jobs* Then click in Actions and chose New Job* You must provide the parameters to run the commands:     - Name: any name you want to give to the job;    - Due On: It is when you want this job to run;    
    - Type: **example.executor_update_timer.commands.SetTimerAsTriggeredCommand** to mark a timer as triggered and make the process continue the execution after the timer or **example.executor_update_timer.commands.CancelTimerCommand** to simply cancel a timer node;    - Retries: If there's any error during the command execution, the executor service can run the command again and you choose how many times it will re-run your command    - Finally you should provider the parameters to run the jobs, which are: The identifier (deployment ID) and the processInstanceId - the process instance ID that is stopped on a timer.    
Then the command should run and cancel/trigger the timer. Make sure the provided process instance ID is stopped on a timer or 
