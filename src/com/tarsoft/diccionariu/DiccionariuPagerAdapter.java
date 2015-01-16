package com.tarsoft.diccionariu;

/*Diccionariu - http://code.google.com/p/diccionariu/
Copyright (C) 2012 itaravika itaravika@gmail.com

Diccionariu is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

Diccionariu is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Diccionariu; if not, see http://www.gnu.org/licenses for more
information.
*/

import java.util.List;

import android.app.Activity;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;


public class DiccionariuPagerAdapter extends PagerAdapter {
	
	protected transient Activity mContext;
	
	private List<View> data;
	
	public DiccionariuPagerAdapter(Activity context, List<View> data) {
		mContext = context;
		this.data = data;
	}
	
	@Override
	public int getCount() {
		return data.size();
	}
	
	@Override
	public Object instantiateItem(View container, int position) {
		
		RelativeLayout v = new RelativeLayout(mContext);		
		v = (RelativeLayout) data.get(position);
				
		((ViewPager) container).addView(v, 0);
				
		return v;
	}
	
	
	@Override
	public void destroyItem(View container, int position, Object view) {
		((ViewPager) container).removeView((View) view);
	}
	
	
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((View) object);
	}
	
	
	@Override
	public void finishUpdate(View container) {}
	
	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {}
	
	@Override
	public Parcelable saveState() {
		return null;
	}
	
	@Override
	public void startUpdate(View container) {}
	
	public View findViewById(int position, int id) {	
		return data.get(position).findViewById(id);
	}
	
}