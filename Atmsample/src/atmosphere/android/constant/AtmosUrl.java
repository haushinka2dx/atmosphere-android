package atmosphere.android.constant;

public final class AtmosUrl {
	private AtmosUrl() {
	}

	public static final String BASE_URL = "https://atmos.interprism.co.jp/atmos-stg/";

	public static final String LOGIN_METHOD = "auth/login";
	public static final String USER_WHO_AM_I_METHOD = "auth/whoami";

	public static final String GLOBAL_TIMELINE_METHOD = "messages/global_timeline";
	public static final String TALK_TIMELINE_METHOD = "messages/talk_timeline";
	public static final String PRIVATE_TIMELINE_METHOD = "private/timeline";
	public static final String MESSAGE_SEARCH_METHOD = "messages/search";

	public static final String SEND_MESSAGE_METHOD = "messages/send";
	public static final String SEND_PRIVATE_MESSAGE_METHOD = "private/send";
	public static final String SEND_RESPONSE_METHOD = "messages/response";
	public static final String SEND_DESTORY_METHOD = "messages/destroy";

	public static final String USER_LIST_METHOD = "user/list";
	public static final String USER_AVATAR_METHOD = "user/avator";
}
