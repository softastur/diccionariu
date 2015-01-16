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

import br.com.dina.ui.widget.UITableView;
import br.com.dina.ui.widget.UITableView.ClickListener;

import com.Leadbolt.AdController;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

/** 
 * Clase (Activity) que contiene las pestañas que gestionan las opciones
 * del diccionariu
 *
 * @author itaravika 2012
 * 
 */

public class main extends Activity {
	
	UITableView tableViewOpciones;
	UITableView tableViewInfo;
	
	@Override 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.main);
		
		//Action bar
        final ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
        actionBar.setHomeAction(new IntentAction(this, main.createIntent(this), R.drawable.iniciu)); 
        actionBar.setTitle(getApplicationContext().getString(R.string.iniciu));
        
        
        //Table views
        tableViewOpciones = (UITableView) findViewById(R.id.tableViewOpciones); 
        tableViewInfo = (UITableView) findViewById(R.id.tableViewInfo);
        
        createTables();	 
        
        tableViewOpciones.commit();        
        tableViewInfo.commit();
	}
	
    private void createTables() {
    	
    	CustomClickListenerOpciones listenerOpciones = new CustomClickListenerOpciones();
    	tableViewOpciones.setClickListener(listenerOpciones);
    	tableViewOpciones.addBasicItem(R.drawable.diccionariu, getApplicationContext().getString(R.string.app_name), getApplicationContext().getString(R.string.guetarPallabra));
    	tableViewOpciones.addBasicItem(R.drawable.conxugar, getApplicationContext().getString(R.string.conxugar), getApplicationContext().getString(R.string.conxugarVerbu));
    	tableViewOpciones.addBasicItem(R.drawable.traduccion, getApplicationContext().getString(R.string.traducir), getApplicationContext().getString(R.string.traducirTestu));
    	
    	CustomClickListenerInfo listenerInfo = new CustomClickListenerInfo();
    	tableViewInfo.setClickListener(listenerInfo);
    	tableViewInfo.addBasicItem(R.drawable.licencia, getApplicationContext().getString(R.string.licencia), getApplicationContext().getString(R.string.verLicencia));
    	tableViewInfo.addBasicItem(R.drawable.about, getApplicationContext().getString(R.string.acercaDe), "");
    }
	
  //Click Listener for option links
    private class CustomClickListenerOpciones implements ClickListener {

		@Override
		public void onClick(int index) {			
			Intent i;
			
			switch (index){
			case 0:
				i = new Intent(main.this, opciones.class);
				i.putExtra("opcion", 0);
				startActivity(i);
				break;
			case 1:
				i = new Intent(main.this, opciones.class);
				i.putExtra("opcion", 1);
				startActivity(i);
				break;
			case 2:
				i = new Intent(main.this, opciones.class);
				i.putExtra("opcion", 2);
				startActivity(i);
				break;
			}
		}    	
    }
    
    //Click Listener for info links
    private class CustomClickListenerInfo implements ClickListener {

		@Override
		public void onClick(int index) {
			Intent i;
				
			switch (index){
			case 0:
				i = new Intent(main.this, licencia.class);
				startActivity(i);
				break;
			case 1:
				i = new Intent(main.this, acercaDe.class);
				startActivity(i);
				break;
			}
		}
    }
    
    public static Intent createIntent(Context context) {
        Intent i = new Intent(context, main.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }
}