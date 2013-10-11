package atmosphere.android.activity.listener.handler;

public interface OnFlickHandler {
	public void controlMoving(int limit, float difference);

	public void compleated();

	public void notCompleated();
}
