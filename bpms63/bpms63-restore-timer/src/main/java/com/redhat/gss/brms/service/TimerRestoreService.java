package com.redhat.gss.brms.service;

import javax.ejb.Stateless;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jbpm.process.instance.command.UpdateTimerCommand;
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

@Stateless
public class TimerRestoreService {

	private RuntimeStrategy strategy;

	public void setAsTriggered(String deploymentId, long piid) {
		RuntimeEngine runtimeEngine = getRuntimeEngine(deploymentId, piid);
		KieSession kSession = runtimeEngine.getKieSession();
		WorkflowProcessInstance pi = (WorkflowProcessInstance) kSession
				.getProcessInstance(piid);
		TimerNodeInstance oldTimerInstance = getTimerInstance(pi);
		oldTimerInstance.triggerCompleted(true);
		dispose(deploymentId, runtimeEngine);
	}

	public void updateTimerNode(Long piid, String deploymentId,
			String timerName, long delay, long period, int repeatLimit) {
		RuntimeEngine runtimeEngine = getRuntimeEngine(deploymentId, piid);
		KieSession kSession = runtimeEngine.getKieSession();
		UpdateTimerCommand cmd = new UpdateTimerCommand(piid, timerName, delay,
				period, repeatLimit);
		kSession.execute(cmd);
		dispose(deploymentId, runtimeEngine);
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
		RuntimeManager runtimeManager = RuntimeManagerRegistry.get()
				.getManager(deploymentId);
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
		RuntimeManager runtimeManager = RuntimeManagerRegistry.get()
				.getManager(deploymentId);
		if (strategy != null && strategy == RuntimeStrategy.SINGLETON) {
			runtimeManager.disposeRuntimeEngine(runtimeEngine);
		}
	}

}
