package atmosphere.android.util;

public interface ProgressObserver {
	public enum ProgressStyle {
		Spin, Progress,
	}

	void update(ProgressStyle style, int max, int progress, String message, String subMesage);

	void updateStyle(ProgressStyle style);

	void updateProgress(int progress);

	void updateProgress(int progress, int max);

	void setMessage(String message);

	void setSubMessage(String message);

	void showToast(String message);

	boolean isCancelled();

	void cancel();

	void cancel(int cancelCode);

}
