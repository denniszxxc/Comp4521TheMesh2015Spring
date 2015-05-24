package hk.ust.comp4521.instant_messaging.server_utils;

public class RequestId {
	// id > 0 is occupied by singel send task
	// id < 0 for adding friend
	public static final int BULK_SEND = 0;
	public static final int ADD_FRIEND = -1;
	
}
