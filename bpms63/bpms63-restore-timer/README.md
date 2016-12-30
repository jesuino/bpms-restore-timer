# BPM Suite 6.3 Restore Timer

A BPM Suite 6.3 app to restore processes stalled in timer nodes (or modify the timer values)


Build it using `mvn clean package` and add deploy it to JBoss EAP running BPM Suite 6.3, business-central.war.

It highly depends on Business central running on JBoss EAP.

To restore a process stalled on a timer make a request providing the deployment ID and the process instance ID: 

`curl -X POST --data  'deploymentId=example:test-cancel-timer:1.0&piid=66031' 'http://localhost:8080/bpms63-restore-timer/rest/timer/mark-as-triggered?strategy=PER_PROCESS_INSTANCE'`

You can also cancel a timer:

`curl -X POST --data  'deploymentId=example:test-cancel-timer:1.0&piid=66031' 'http://localhost:8080/bpms63-restore-timer/rest/timer/cancel?strategy=PER_PROCESS_INSTANCE'`

