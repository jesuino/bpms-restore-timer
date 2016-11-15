package com.redhat.gss.brms.restore;

import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.kie.internal.runtime.conf.RuntimeStrategy;

import com.redhat.gss.brms.service.TimerRestoreService;

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
@Stateless
public class TimerRestoreResource {

	@EJB
	TimerRestoreService timerRestoreService;

	Logger logger = Logger.getLogger(getClass().getName());

	// OPTIONAL
	@QueryParam("strategy")
	private RuntimeStrategy strategy;

	@POST
	@Path("mark-as-triggered")
	// tested and working
	public Response setAsTriggered(
			@FormParam("deploymentId") String deploymentId,
			@FormParam("piid") long piid) {
		String responseMsg;
		try {
			logger.info("Setting timer as triggered for process instance "
					+ piid);
			timerRestoreService.setAsTriggered(deploymentId, piid, strategy);
			responseMsg = "Timer from piid " + piid + " of deployment "
					+ deploymentId + " succesfull marked as triggered";
			logger.info(responseMsg);
			return Response.ok(responseMsg).build();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	@POST
	@Path("cancel")
	// tested and working
	public Response cancel(@FormParam("deploymentId") String deploymentId,
			@FormParam("piid") long piid) {
		String responseMsg;
		try {
			logger.info("Cancelling timer for process instance " + piid);
			timerRestoreService.cancelTimer(deploymentId, piid, strategy);
			responseMsg = "Timer from piid " + piid + " of deployment "
					+ deploymentId + " succesfull cancelled";
			logger.info(responseMsg);
			return Response.ok(responseMsg).build();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	@POST
	@Path("update")
	public Response update(@FormParam("piid") Long piid,
			@FormParam("deploymentId") String deploymentId,
			@FormParam("delay") long delay, @FormParam("period") long period,
			@FormParam("repeatLimit") int repeatLimit) {
		try {
			String msg = String
					.format("Attempt to update timer from process instance id %d with parameters: (delay = %d, period= %d, repeatLimit = %d)",
							piid, delay, period, repeatLimit);
			logger.info(msg);
			timerRestoreService.updateTimerNode(piid, deploymentId, delay,
					period, repeatLimit);
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
		// TODO: Implement
		return Response.ok("Timer successfully restored").build();
	}

}
