package example.executor_update_timer.commands;

import org.kie.internal.executor.api.Command;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;

/**
 * 
 * If the given process instance Id is stopped at a timer node, this will cancel it
 * 
 * @author wsiqueir
 *
 */
public class CancelTimerCommand extends AbstractTimerActionCommand implements Command {

	public ExecutionResults execute(CommandContext ctx) throws Exception {
		return super.execute(ctx, TimerAction.CANCEL);
	}

}