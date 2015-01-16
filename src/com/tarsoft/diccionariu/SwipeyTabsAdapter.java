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

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import com.astuetz.viewpager.extensions.SwipeyTabButton;
import com.astuetz.viewpager.extensions.TabsAdapter;


public class SwipeyTabsAdapter implements TabsAdapter {
	
	private Activity mContext;
	
	private String[] mTitles = {
	    "Diccionariu", "Conxugar", "Traducir"
	};
	
	public SwipeyTabsAdapter(Activity ctx) {
		this.mContext = ctx;
	}
	
	@Override
	public View getView(int position) {
		SwipeyTabButton tab;
		
		LayoutInflater inflater = mContext.getLayoutInflater();
		tab = (SwipeyTabButton) inflater.inflate(R.layout.tab_swipey, null);
		
		if (position < mTitles.length) tab.setText(mTitles[position]);
				
		return tab;
	}	
}