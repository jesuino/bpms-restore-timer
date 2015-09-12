# bpms603-restore-timer

A BPM Suite 6.0.3 app to restore processes stalled in timer nodes (or modify the timer values)


Build it using `mvn clean package` and add deploy it to JBoss EAP running BPM Suite 6.0.3, business-central.war.

It highly depends on Business central running on JBoss EAP.

To use it simply make a HTTP POST the the API endpoint passing as parameters the deployment id of the project that contains the old timer and values for the new, for example: `curl -X POST 'http://localhost:8080/bpms603-restore-timer/rest/restore?deploymentId=example:proj:1.0&cron=10s&piid=9'`

Other parameters are also accepted. If you pass triggered=true, it will resume the process execution. This is useful in case the process is stucked at a timer instance, situation that might happen for many reasons:
`$ curl -X POST 'http://localhost:8080/bpms603-restore-timer/rest/restore?deploymentId=example:proj:1.0&triggered=true&piid=10'`


