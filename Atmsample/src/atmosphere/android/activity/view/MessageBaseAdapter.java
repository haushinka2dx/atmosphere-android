package atmosphere.android.activity.view;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import atmosphere.android.constant.AtmosUrl;
import atmosphere.android.dto.MessageDto;

public abstract class MessageBaseAdapter extends BaseAdapter implements AtmosUrl {
	protected List<MessageDto> list;

	public MessageBaseAdapter(List<MessageDto> list) {
		this.list = list;
	}

	public void setItems(List<MessageDto> list) {
		this.list = list;
	}

	public void addItem(MessageDto item) {
		this.list.add(item);
	}

	public void addItems(List<MessageDto> list) {
		this.list.addAll(list);
	}

	public void addBeforeItem(MessageDto item) {
		List<MessageDto> newList = new ArrayList<MessageDto>();
		newList.add(item);
		newList.addAll(this.list);
		this.list = newList;
	}

	public void addBeforeItems(List<MessageDto> list) {
		list.addAll(this.list);
		this.list = list;
	}

	public void removeItem(int position) {
		list.remove(position);
	}

	@Override
	public int getCount() {
		return this.list.size();
	}

	@Override
	public Object getItem(int position) {
		return this.list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}
}
