package com.redhat.gss.brms.restore;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.EmptyContext;

@WebServlet("/rest/update")
public class TimerUpdateServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	Logger logger = Logger.getLogger(getClass().getName());
	
	private RuntimeEngine runtimeEngine;
	private RuntimeManager runtimeManager;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String val;
		long piid = Long.parseLong(req.getParameter("piid"));
		String deploymentId = req.getParameter("deploymentId");
		String timerName = req.getParameter("timerName");
		long delay =  Long.parseLong(((val = req.getParameter("delay")) != null ? val : "0"));
		long period  = Long.parseLong(((val = req.getParameter("period")) != null ? val : "0"));
		int repeatLimit = Integer.parseInt(((val = req.getParameter("repeatLimit")) != null ? val : "0"));
		String response;
		try {
			String msg = String.format("Attempt to update timer %s from process instance id %d with parameters: (delay = %d, period= %d, repeatLimit = %d)", timerName, piid, delay, period, repeatLimit);
			logger.info(msg);
			UpdateTimerCommand cmd = new UpdateTimerCommand(piid, timerName, delay, period, repeatLimit);
			KieSession kSession = getKieSession(deploymentId);
			kSession.execute(cmd);
			dispose();
			response = "Timer updated with success.";
		} catch (Exception e) {
			e.printStackTrace();
			response  = "Something went wrong. See the server logs. Error message: " + e.getMessage();
		}
		logger.info(response);
		resp.getOutputStream().write(response.getBytes());
		resp.getOutputStream().close();	
	}

	private void dispose() {
		runtimeManager.disposeRuntimeEngine(runtimeEngine);
	}

	private KieSession getKieSession(String deploymentId) {
		runtimeManager = RuntimeManagerRegistry.get()
				.getManager(deploymentId);
		runtimeEngine = runtimeManager
				.getRuntimeEngine(EmptyContext.get());
		KieSession ksession = runtimeEngine.getKieSession();
		return ksession;
	}
}
