package hk.ust.comp4521.instant_messaging.server_utils;

public interface OnHandleResultWithHostListener {
	public void onResponse(long id, String response);
	public void onError(long id);
	public void onStop(long id);
	public void onWaitingNetwork(long id);
}
