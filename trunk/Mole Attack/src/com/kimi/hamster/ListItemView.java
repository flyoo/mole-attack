package com.kimi.hamster;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class ListItemView extends LinearLayout {

	private Context context;
	private ImageView iconView;
	private TextView nameTextView, addressTextView;

	public ListItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	public ListItemView(Context context) {
		super(context);
		initialize(context);
	}

	private void initialize(Context context) {
		this.context = context;
		View view = LayoutInflater.from(this.context).inflate(R.layout.device_item, null);
		iconView = (ImageView) view.findViewById(R.id.icon);
		nameTextView = (TextView) view.findViewById(R.id.devicename);
		addressTextView = (TextView) view.findViewById(R.id.deviceaddr);
		addView(view);
	}

	public void updateView(Data d) {
		
		iconView.setImageDrawable(d.icon);
		nameTextView.setText(d.nameText);
		addressTextView.setText(d.addressText);
	}

	public static final class Data {
		
		public Drawable icon;
		public String nameText;
		public String addressText;

		public Data() { }

		public Data(Data d) {
			icon = d.icon;
			nameText = d.nameText;
			addressText = d.addressText;
		}
	}
}