package example.executor_update_timer.commands;

import org.kie.internal.executor.api.Command;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;

/**
 * 
 * If a process instance is stopped on a timer we can set it as triggered and
 * make the process skip it.
 * 
 * @author wsiqueir
 *
 */
public class SetTimerAsTriggeredCommand extends AbstractTimerActionCommand
		implements Command {

	public ExecutionResults execute(CommandContext ctx) throws Exception {
		logger.warning("Running action SetTimerAsTriggered");
		return super.execute(ctx, TimerAction.SET_AS_TRIGERRED);
	}

}