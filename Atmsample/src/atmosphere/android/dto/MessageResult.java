package atmosphere.android.dto;

import java.util.List;

public class MessageResult {
	public String status;
	public String count;
	public String latest_created_at;
	public String oldest_created_at;
	public List<MessageDto> results;
}
