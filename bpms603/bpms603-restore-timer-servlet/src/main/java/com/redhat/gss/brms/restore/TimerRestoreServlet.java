package com.redhat.gss.brms.restore;

import java.io.IOException;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.TransactionManager;

import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.node.TimerNodeInstance;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.EmptyContext;

@WebServlet("/rest/restore")
public class TimerRestoreServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	Logger logger = Logger.getLogger(getClass().getName());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		long piid = Long.parseLong(req.getParameter("piid"));
		String deploymentId = req.getParameter("deploymentId");
		String response;
		try {
			response = restoreTimer(piid, deploymentId);
		} catch (Exception e) {
			e.printStackTrace();
			response  = "Something went wrong. See the server logs. Error message: " +e.getMessage();
		}
		resp.getOutputStream().write(response.getBytes());
		resp.getOutputStream().close();	
	}
	
	public String restoreTimer(long piid, String deploymentId) throws Exception {
			TransactionManager tm = (TransactionManager) new InitialContext()
					.lookup("java:jboss/TransactionManager");
		
		KieSession kSession = getKieSession(deploymentId);
		TimerNodeInstance oldTimerInstance = null;
		tm.begin();

		WorkflowProcessInstance pi = (WorkflowProcessInstance) kSession
				.getProcessInstance(piid);
		for (NodeInstance n : pi.getNodeInstances()) {
			if (n instanceof TimerNodeInstance)
				oldTimerInstance = (TimerNodeInstance) n;
		}
		if (oldTimerInstance == null) {
			throw new ServletException(
					"Process NOT stopped on a TimerNodeInstance");
		}
		logger.info("Setting timer as triggered for process instance " + piid);
		oldTimerInstance.triggerCompleted(true);
		tm.commit();
		return "Timer set as triggered for instance " + piid;
	}

	private KieSession getKieSession(String deploymentId) {
		// TODO: Use Deployment service
		RuntimeManager runtimeManager = RuntimeManagerRegistry.get()
				.getManager(deploymentId);
		// Only for SINGLETON
		RuntimeEngine runtimeEngine = runtimeManager
				.getRuntimeEngine(EmptyContext.get());
		KieSession ksession = runtimeEngine.getKieSession();
		return ksession;
	}
}
