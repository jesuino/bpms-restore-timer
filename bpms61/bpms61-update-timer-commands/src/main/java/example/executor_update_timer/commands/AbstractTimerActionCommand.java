package example.executor_update_timer.commands;

import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;

import example.executor_update_timer.service.UpdateTimerService;

public abstract class AbstractTimerActionCommand {

	protected UpdateTimerService updateTimerService;

	Logger logger = Logger.getLogger(this.getClass().getName());

	public AbstractTimerActionCommand() {
		updateTimerService = UpdateTimerService.Factory.get();
	}

	public ExecutionResults execute(CommandContext ctx, TimerAction timerAction) throws Exception {
		UserTransaction ut = (UserTransaction) InitialContext
				.doLookup("java:jboss/UserTransaction");
		try {
			ut.begin();
			String identifier = (String) ctx.getData().get("identifier");
			// avoid errors if users send processInstanceId as text
			String piidStr = String.valueOf(ctx.getData().get(
					"processInstanceId"));
			long piid = Long.parseLong(piidStr);
			logger.warning("Running action " + timerAction + " on identifier "
					+ identifier + " and process instance " + piid);
			switch (timerAction) {
			case CANCEL:
				updateTimerService.cancelTimer(identifier, piid);
				break;
			case SET_AS_TRIGERRED:
				updateTimerService.setAsTriggered(identifier, piid);
				break;
			default:
				throw new IllegalArgumentException("Action not recognized: " + timerAction);	
			}
			ut.commit();
		} catch (Exception e) {
			e.printStackTrace();
			ut.rollback();
		}
		return null;
	}

}