package example.executor_update_timer.service;

public interface UpdateTimerService {

	void setAsTriggered(String identifier, long piid);

	void cancelTimer(String identifier, long piid);

	public static class Factory {
		private static UpdateTimerService updateTimerService;

		static {
			updateTimerService = new UpdateTimerServiceImpl();
		}

		public static UpdateTimerService get() {
			return updateTimerService;
		}
	}
}
