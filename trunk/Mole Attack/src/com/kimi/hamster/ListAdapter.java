package com.kimi.hamster;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.kimi.hamster.ListItemView;

public class ListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<ListItemView.Data> datas;

	public ListAdapter(Context context) {
		this.context = context;
	}

	public ListAdapter(Context context, ArrayList<ListItemView.Data> datas) {
		this.context = context;
		this.datas = datas;
	}

	public void setDatas(ArrayList<ListItemView.Data> datas) {
		this.datas = datas;
	}

	public int getCount() {
		return datas.size();
	}

	public Object getItem(int location) {
		return datas.get(location);
	}

	public long getItemId(int location) {
		return location;
	}

	public View getView(int location, View view, ViewGroup parent) {
		if(datas == null) {
			return null;
		}
		if(view == null) {
			ListItemView itemView = new ListItemView(context);
			itemView.updateView(datas.get(location));
			view = itemView;
		} else {
			((ListItemView) view).updateView(datas.get(location));
		}
		return view;
	}

}