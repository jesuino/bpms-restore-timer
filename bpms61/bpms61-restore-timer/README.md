# bpms603-restore-timer

A BPM Suite 6.0.3 app to restore processes stalled in timer nodes (or modify the timer values)


Build it using `mvn clean package` and add deploy it to JBoss EAP running BPM Suite 6.0.3, business-central.war.

It highly depends on Business central running on JBoss EAP.

To restore a process stalled on a timer make a request providing the deployment ID and the process instance ID: 

`$ curl -X POST 'http://localhost:8080/bpms603-restore-timer/rest/restore?deploymentId=example:proj:1.0&piid=10'`


