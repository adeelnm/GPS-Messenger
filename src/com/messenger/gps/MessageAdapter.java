package com.messenger.gps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public final class MessageAdapter extends ArrayAdapter<Posts> {

	private Context context;
	private List<Posts> messageSearchResult;
	

	public MessageAdapter(final Context c, List<Posts> list) {
		super(c, R.layout.message_list);
		messageSearchResult = new ArrayList<Posts>();
		messageSearchResult = list;
		context = c;
	}

	@Override
	public void clear() {
		messageSearchResult.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return messageSearchResult.size();
	}

	@Override
	public void addAll(Collection<? extends Posts> collection) {
		messageSearchResult.addAll(collection);
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		if (row == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = vi.inflate(R.layout.message_list, null);
		}
		Posts item = messageSearchResult.get(position);
		if (item != null) {
			TextView t1 = (TextView) row.findViewById(R.id.view_nick);
			TextView t2 = (TextView) row.findViewById(R.id.view_message);
			t1.setText(item.getName());
			t2.setText(item.getMessage());
		}

		/*
		 * Button b = (Button) convertView.findViewById(R.id.btn_showMore);
		 * 
		 * b.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub new TwitterSearchActivity().TaskStarter();
		 * 
		 * } });
		 */
		return row;

	}

}
