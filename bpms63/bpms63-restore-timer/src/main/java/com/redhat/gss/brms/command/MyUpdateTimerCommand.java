package com.redhat.gss.brms.command;

import java.util.List;

import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.timer.TimerInstance;
import org.jbpm.process.instance.timer.TimerManager;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.workflow.instance.node.StateBasedNodeInstance;
import org.jbpm.workflow.instance.node.TimerNodeInstance;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.internal.command.Context;
import org.kie.internal.command.ProcessInstanceIdCommand;


public class MyUpdateTimerCommand implements GenericCommand<Void>, ProcessInstanceIdCommand {

    private static final long serialVersionUID = -8252686458877022330L;

    private long processInstanceId;


    private long delay;

    private long period;

    private int repeatLimit;

	private TimerNodeInstance timerNodeInstance;
    
    public MyUpdateTimerCommand(long processInstanceId, TimerNodeInstance timerNodeInstance, long delay, long period, int repeatLimit) {
        this.processInstanceId = processInstanceId;
        this.timerNodeInstance = timerNodeInstance;
        this.delay = delay;
        this.period = period;
        this.repeatLimit = repeatLimit;
    }

    @Override
    public Void execute(Context context) {
        KieSession kieSession = ((KnowledgeCommandContext) context).getKieSession();
        TimerManager tm = getTimerManager(kieSession);

        RuleFlowProcessInstance wfp = (RuleFlowProcessInstance) kieSession.getProcessInstance(processInstanceId);

        for (NodeInstance nodeInstance : wfp.getNodeInstances()) {
            if (nodeInstance instanceof TimerNodeInstance) {
                TimerNodeInstance tni = (TimerNodeInstance) nodeInstance;
                if (tni.getNodeName().equals(timerNodeInstance.getNodeName())) {
                   // TimerInstance timer = tm.getTimerMap().get(tni.getTimerId());
                	TimerInstance timer = timerNodeInstance.getTimerInstance();	
                    tm.cancelTimer(timer.getTimerId());
                    TimerInstance newTimer = new TimerInstance();

                    if (delay != 0) {
                        long diff = System.currentTimeMillis() - timer.getActivated().getTime();
                        newTimer.setDelay(delay * 1000 - diff);
                    }
                    newTimer.setPeriod(period);
                    newTimer.setRepeatLimit(repeatLimit);
                    newTimer.setTimerId(timer.getTimerId());
                    tm.registerTimer(newTimer, wfp);

                    tni.internalSetTimerId(newTimer.getId());

                    break;
                }
            } else if (nodeInstance instanceof StateBasedNodeInstance) {
                StateBasedNodeInstance sbni = (StateBasedNodeInstance) nodeInstance;
                
                if (sbni.getNodeName().equals(timerNodeInstance.getNodeName())) {
                    List<Long> timerList = sbni.getTimerInstances();
                    if (timerList != null && timerList.size() == 1) {
                        TimerInstance timer = tm.getTimerMap().get(timerList.get(0));
    
                        tm.cancelTimer(timer.getTimerId());
                        TimerInstance newTimer = new TimerInstance();
    
                        if (delay != 0) {
                            long diff = System.currentTimeMillis() - timer.getActivated().getTime();
                            newTimer.setDelay(delay * 1000 - diff);
                        }
                        newTimer.setPeriod(period);
                        newTimer.setRepeatLimit(repeatLimit);
                        newTimer.setTimerId(timer.getTimerId());
                        tm.registerTimer(newTimer, wfp);
                        
                        timerList.clear();
                        timerList.add(newTimer.getId());
    
                        sbni.internalSetTimerInstances(timerList);
                    
                    }
                    break;
                }
            }
        }
        return null;
    }

    @Override
    public void setProcessInstanceId(Long procInstId) {
        this.processInstanceId = procInstId;
    }

    @Override
    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    private TimerManager getTimerManager(KieSession ksession) {
        KieSession internal = ksession;
        if (ksession instanceof CommandBasedStatefulKnowledgeSession) {
            internal = ((KnowledgeCommandContext) ((CommandBasedStatefulKnowledgeSession) ksession).getCommandService().getContext()).getKieSession();
        }

        return ((InternalProcessRuntime) ((StatefulKnowledgeSessionImpl) internal).getProcessRuntime()).getTimerManager();
    }

    public String toString() {
        return "processInstance.updateTimer(" + timerNodeInstance.getNodeName() + ", " + delay + ", " + period + ", " + repeatLimit + ");";
    }

}
