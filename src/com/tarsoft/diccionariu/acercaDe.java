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


import br.com.dina.ui.model.ViewItem;
import br.com.dina.ui.widget.UITableView;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

/** 
 * Clase (Activity) que presenta los datos generales de la aplicacion
 *
 * @author itaravika (c) 2012
 * 
 */

public class acercaDe extends Activity {

	UITableView tableViewAcercaDe;

	@Override
	/** Called when the activity is first created. */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.contenedor);
		
		//Action bar
        final ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
        actionBar.setHomeAction(new IntentAction(this, main.createIntent(this), R.drawable.ic_title_home_default));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getApplicationContext().getString(R.string.about));
		
        tableViewAcercaDe = (UITableView) findViewById(R.id.tableViewTexto);
        
        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout v = (RelativeLayout) mInflater.inflate(R.layout.acercade, null);
        
        //add links for emails and web pages
		TextView emailLink = (TextView) v.findViewById(R.id.detallegeneralemail);		
        Linkify.addLinks(emailLink, Linkify.ALL);
        
        TextView detallefinal = (TextView) v.findViewById(R.id.tvdetallefinal);		
        Linkify.addLinks(detallefinal, Linkify.ALL); 
        
		ViewItem v2 = new ViewItem(v);
		v2.setClickable(false);
		tableViewAcercaDe.addViewItem(v2);
		
		tableViewAcercaDe.commit();
	} 
}
