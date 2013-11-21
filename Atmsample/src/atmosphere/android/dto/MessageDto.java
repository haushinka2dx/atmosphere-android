package atmosphere.android.dto;

import java.util.List;

public class MessageDto {
	public String _id;
	public String message;
	public String message_type;
	public AddressesDto addresses;
	public List<String> to_user_id;
	public List<String> hashtags;
	public String reply_to;
	public String created_by;
	public ResponsesDto responses;
	public String created_at;
}
