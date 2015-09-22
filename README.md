# bpms603-restore-timer

## SERVLET EDITION (NO DEPENDENCY ON RESTEASY TO AVOID CLASSLOADING ISSUES)

A BPM Suite 6.0.3 app to restore processes stalled in timer nodes (or modify the timer values) 

Build it using `mvn clean package` and add deploy it to JBoss EAP running BPM Suite 6.0.3, business-central.war.

It highly depends on Business central running on JBoss EAP.

`$ curl -X POST 'http://localhost:8080/bpms603-restore-timer/rest/restore?deploymentId=example:proj:1.0&piid=10'`

It is also possible to update a running timer using the following call:

`$ curl -X POST 'http://localhost:8080/bpms603-restore-timer/rest/update?deploymentId=example:proj:1.0&piid=5&timerName=TimerName&delay=1'`

Other accepted parameters for the update endpoint are `period` and `repeatLimit`. These will be the parameters of the new timer instance.
