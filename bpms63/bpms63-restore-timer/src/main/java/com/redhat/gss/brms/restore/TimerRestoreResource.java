package com.redhat.gss.brms.restore;

import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.transaction.TransactionManager;
import javax.ws.rs.FormParam;
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
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

/**
 * 
 * This is an EJB to perform timer operations over a process stopped on a timer
 * node instance
 * 
 * TODO: must refactor
 * 
 * @author wsiqueir
 *
 */
@Path("timer")
public class TimerRestoreResource {

	Logger logger = Logger.getLogger(getClass().getName());

	// OPTIONAL
	@QueryParam("strategy")
	private RuntimeStrategy strategy;

	@POST
	@Path("mark-as-triggered")
	// tested and working
	public Response setAsTriggered(
			@FormParam("deploymentId") String deploymentId,
			@FormParam("piid") long piid) throws Exception {
		TransactionManager tm = (TransactionManager) InitialContext.doLookup("java:jboss/TransactionManager");
		tm.begin();
		RuntimeEngine runtimeEngine = getRuntimeEngine(deploymentId, piid);
		KieSession kSession = runtimeEngine.getKieSession();
		WorkflowProcessInstance pi = (WorkflowProcessInstance) kSession
				.getProcessInstance(piid);
		TimerNodeInstance oldTimerInstance = getTimerInstance(pi);
		oldTimerInstance.triggerCompleted(true);
		dispose(deploymentId, runtimeEngine);
		logger.info("Setting timer as triggered for process instance " + piid);
		tm.commit();
		return Response.ok(
				"Timer from piid " + piid + " of deployment " + deploymentId
						+ " succesfull marked as triggered").build();

	}

	@POST
	@Path("update")
	public Response update(@FormParam("piid") Long piid,
			@FormParam("deploymentId") String deploymentId,
			@FormParam("timerName") String timerName,
			@FormParam("delay") long delay, @FormParam("period") long period,
			@FormParam("repeatLimit") int repeatLimit) {
		try {
			String msg = String
					.format("Attempt to update timer %s from process instance id %d with parameters: (delay = %d, period= %d, repeatLimit = %d)",
							timerName, piid, delay, period, repeatLimit);
			logger.info(msg);
			org.jbpm.process.instance.command.UpdateTimerCommand cmd = new org.jbpm.process.instance.command.UpdateTimerCommand(
					piid, timerName, delay, period, repeatLimit);
			RuntimeEngine runtimeEngine = getRuntimeEngine(deploymentId, piid);
			KieSession kSession = runtimeEngine.getKieSession();
			kSession.execute(cmd);
			dispose(deploymentId, runtimeEngine);
			return Response.ok("Timer successfully updated").build();
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(Response
					.status(Status.BAD_REQUEST)
					.entity("Problem updating timer").build());
		}
	}

	@POST
	@Path("restore")
	public Response update(@FormParam("piid") Long piid,
			@FormParam("deploymentId") String deploymentId) {
		return Response.ok("Timer successfully restored").build();

	}

	private TimerNodeInstance getTimerInstance(WorkflowProcessInstance pi) {
		// this does not seem to be the best way to achieve this - better modify
		// in future
		TimerNodeInstance oldTimerInstance = null;
		for (NodeInstance n : pi.getNodeInstances()) {
			if (n instanceof TimerNodeInstance)
				oldTimerInstance = (TimerNodeInstance) n;
		}
		if (oldTimerInstance == null) {
			throw new WebApplicationException(Response
					.status(Status.BAD_REQUEST)
					.entity("Process NOT stopped on a TimerNodeInstance")
					.build());
		}
		return oldTimerInstance;
	}

	public RuntimeEngine getRuntimeEngine(String deploymentId, Long piid) {
		RuntimeManager runtimeManager = RuntimeManagerRegistry.get().getManager(deploymentId);
		RuntimeEngine runtimeEngine = null;
		if (strategy == RuntimeStrategy.PER_PROCESS_INSTANCE) {
			runtimeEngine = runtimeManager
					.getRuntimeEngine(ProcessInstanceIdContext.get(piid));
		} else {
			runtimeEngine = runtimeManager.getRuntimeEngine(EmptyContext.get());
		}
		return runtimeEngine;
	}

	public void dispose(String deploymentId, RuntimeEngine runtimeEngine) {
			RuntimeManager runtimeManager = RuntimeManagerRegistry.get().getManager(deploymentId);
		if (strategy!= null && strategy == RuntimeStrategy.SINGLETON) {
			runtimeManager.disposeRuntimeEngine(runtimeEngine);
		}
	}
}
