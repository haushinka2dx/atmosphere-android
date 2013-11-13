package atmosphere.android.activity.view;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import atmosphere.android.dto.MessageDto;

public abstract class MessageBaseAdapter extends BaseAdapter {
	protected List<MessageDto> list;

	public MessageBaseAdapter(List<MessageDto> list) {
		this.list = list;
	}

	public void setItems(List<MessageDto> list) {
		this.list = list;
	}

	public void addItem(MessageDto item) {
		if (item != null) {
			this.list.add(item);
		}
	}

	public void addItems(List<MessageDto> list) {
		if (list != null & !list.isEmpty()) {
			this.list.addAll(list);
		}
	}

	public void addBeforeItem(MessageDto item) {
		if (item != null) {
			this.list.add(0, item);
		}
	}

	public void addBeforeItems(List<MessageDto> list) {
		if (list != null && !list.isEmpty()) {
			list.addAll(this.list);
			this.list = list;
		}
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
