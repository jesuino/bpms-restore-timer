package com.redhat.gss.brms.restore;

import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.transaction.TransactionManager;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.node.TimerNodeInstance;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.EmptyContext;

@Path("restore")
public class TimerRestoreResource {
	
	Logger logger = Logger.getLogger(getClass().getName());

	@POST
	public void restore(@QueryParam("deploymentId") String deploymentId,
						@QueryParam("piid") long piid) throws Exception {
	    TransactionManager tm = (TransactionManager) new InitialContext().lookup("java:jboss/TransactionManager");
		KieSession kSession = getKieSession(deploymentId);
		TimerNodeInstance oldTimerInstance = null;
		tm.begin();
		WorkflowProcessInstance pi = (WorkflowProcessInstance)kSession.getProcessInstance(piid);
		for(NodeInstance n: pi.getNodeInstances()) {
			if(n instanceof TimerNodeInstance)
				oldTimerInstance = (TimerNodeInstance) n;
		}
		if(oldTimerInstance == null) {
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity("Process NOT stopped on a TimerNodeInstance").build());
		}
		logger.info("Setting timer as triggered for process instance " + piid);
		tm.commit();
	}

	private KieSession getKieSession(String deploymentId) {
		// TODO: Use Deployment service
		RuntimeManager runtimeManager = RuntimeManagerRegistry.get().getManager(deploymentId);
		// Only for SINGLETON
		RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(EmptyContext.get());
		KieSession ksession = runtimeEngine.getKieSession();
		return ksession;
	}
}
