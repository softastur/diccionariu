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

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;
import br.com.dina.ui.model.BasicItem;
import br.com.dina.ui.model.ViewItem;
import br.com.dina.ui.widget.UITableView;
import br.com.dina.ui.widget.UITableView.ClickListener;

import com.astuetz.viewpager.extensions.SwipeyTabsView;
import com.astuetz.viewpager.extensions.TabsAdapter;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ActionBar.IntentAction;

/** 
 * Clase (Activity) principal de la aplicacion donde se realizarán las busquedas
 * y se mostrarán los datos
 *
 * @author itaravika (c) 2012
 * 
 */

public class opciones extends Activity {
    /** Called when the activity is first created. */
	
	private final static String TAG = "Diccionariu";
	
	private boolean checkConexion;
	
	private Bundle extras;

	UITableView tableViewResultDiccionariu;
	UITableView tableViewResultConxugar;
	UITableView tableViewResultTraducir;
	UITableView tableViewIdioma;
	
	private EditText etPalabra;
	private EditText etVerbo;
	private EditText etTexto;
	private ActionBar actionBar;
	
	private ViewPager mPager;
	private SwipeyTabsView mSwipeyTabs;
	private DiccionariuPagerAdapter mPagerAdapter;
	private TabsAdapter mSwipeyTabsAdapter;
	private int focusedPage = 0; // 0 - diccionariu / 1 - conxugar / 2 - traducir
	private List<View> data;
	
	private int traducir_direccion = 1; // 0 - Ast - Cast / 1 - Cast - Ast
	
	//variables globales para accesos al diccionario
	private int RC;
	private List<tablePalabras> listaPalabras;
	
	//variables globales con el resultado de las consultas
	private String result_diccionario;
	private String result_conxugar;
	private String result_traducir;	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //obtener parametros
        extras = getIntent().getExtras();
        //actualizar página actual
        focusedPage = extras.getInt("opcion");        
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	
    	//mostrarMensaje("onResume focusedPage: " + focusedPage);
    	
    	//inicializar vistas
        data = new ArrayList<View>();
        initViews();   
        //Paginación horizontal de pantallas
		setContentView(R.layout.activity_swipey_tabs);
		initViewPager(3);
		mSwipeyTabs = (SwipeyTabsView) findViewById(R.id.swipey_tabs);
		mSwipeyTabsAdapter = new SwipeyTabsAdapter(this);
		mSwipeyTabs.setAdapter(mSwipeyTabsAdapter);
		mSwipeyTabs.setChangePageListener(new MyChangePageListener());
		mSwipeyTabs.setViewPager(mPager);
    	
    	//evaluar si hay internet
    	evaluarConexion();
    	
		//inicializar variables globales
		result_diccionario = "";
		result_conxugar = "";
		result_traducir = "";
		
		RC = -1;
    	
		//inicializar action bar
		initActionBar();		
		
	    etPalabra.setOnKeyListener(new View.OnKeyListener() {
	        @Override
	        public boolean onKey(View v, int keyCode, KeyEvent event) {
	                if(keyCode == KeyEvent.KEYCODE_ENTER){
	                	if (focusedPage == 0)
	                	{
		                	tableViewResultDiccionariu = (UITableView) mPagerAdapter.findViewById(0,R.id.tableViewResult);
		                	etPalabra = (EditText) mPagerAdapter.findViewById(0,R.id.inputpalabra);
		        			//limpiar antes la ultima busqueda
		                    tableViewResultDiccionariu.clear();
		                    tableViewResultDiccionariu.commit();
		            		//si la palabra a buscar esta vacia, error
		            		if (etPalabra.length() == 0){
		            			mostrarMensaje(getApplicationContext().getString(R.string.palabraObligatoria));
		            		} else {
		            			actionBar.setProgressBarVisibility(View.VISIBLE);
		            			new consultaPalabra(etPalabra.getText().toString(), 0).execute();        			
		            		}
		            		
		        			//quitar el teclado
		        			InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		        		    mgr.hideSoftInputFromWindow(actionBar.getWindowToken(), 0);
	                	}
	                    return true;
	                }
	                return false;
	        }
	    });
	    
	    etVerbo.setOnKeyListener(new View.OnKeyListener() {
	        @Override
	        public boolean onKey(View v, int keyCode, KeyEvent event) {
	                if(keyCode == KeyEvent.KEYCODE_ENTER){
	                	if (focusedPage == 1)
	                	{
	                    	tableViewResultConxugar = (UITableView) mPagerAdapter.findViewById(1,R.id.tableViewResult);
	                    	etVerbo = (EditText) mPagerAdapter.findViewById(1,R.id.inputpalabra);
	            			//limpiar antes la ultima busqueda
	                        tableViewResultConxugar.clear();
	                        tableViewResultConxugar.commit();
	                		//si la palabra a buscar esta vacia, error
	                		if (etVerbo.length() == 0){
	                			mostrarMensaje(getApplicationContext().getString(R.string.verboObligatorio));
	                		} else {
	                			actionBar.setProgressBarVisibility(View.VISIBLE);
	                			new consultaVerbo().execute();        			
	                		}
	                		
	            			//quitar el teclado
	            			InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	            		    mgr.hideSoftInputFromWindow(actionBar.getWindowToken(), 0);
	                	}
	                    return true;
	                }
	                return false;
	        }
	    });
	    
	    etTexto.setOnKeyListener(new View.OnKeyListener() {
	        @Override
	        public boolean onKey(View v, int keyCode, KeyEvent event) {
	                if(keyCode == KeyEvent.KEYCODE_ENTER){
	                	if (focusedPage == 2)
	                	{
	                    	tableViewResultTraducir = (UITableView) mPagerAdapter.findViewById(2,R.id.tableViewResult);
	                    	etTexto = (EditText) mPagerAdapter.findViewById(2,R.id.inputtexto);
	            			//limpiar antes la ultima busqueda
	                        tableViewResultTraducir.clear();
	                        tableViewResultTraducir.commit();
	                		//si la palabra a buscar esta vacia, error
	                		if (etTexto.length() == 0){
	                			mostrarMensaje(getApplicationContext().getString(R.string.textoObligatorio));
	                		} else {
	                			actionBar.setProgressBarVisibility(View.VISIBLE);
	                			new traducirTexto().execute();        			
	                		}
	                		
	            			//quitar el teclado
	            			InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	            		    mgr.hideSoftInputFromWindow(actionBar.getWindowToken(), 0);
	                	}
	                    return true;
	                }
	                return false;
	        }
	    });
	    
	    //mostrarMensaje("fin onResume focusedPage: " + focusedPage);
    } 
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
      // Save UI state changes to the savedInstanceState.
      // This bundle will be passed to onCreate if the process is
      // killed and restarted.
      savedInstanceState.putInt("focusedPage", focusedPage);
      
      //mostrarMensaje("guardar focusedPage: " + focusedPage);
      
      super.onSaveInstanceState(savedInstanceState);
    }
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
      super.onRestoreInstanceState(savedInstanceState);
      // Restore UI state from the savedInstanceState.
      // This bundle has also been passed to onCreate.
      focusedPage = savedInstanceState.getInt("focusedPage");
      
      //mostrarMensaje("recuperar focusedPage: " + focusedPage);
    }
	
	//metodo parar añadir la opción de buscar
    private class searchAction extends AbstractAction {

        public searchAction() {
            super(R.drawable.buscar);
        }

        @Override
        public void performAction(View view) {
        	
        	//evaluar si hay conexión a la red
        	evaluarConexion();
        	
        	//dependiendo de la pagina que tenga el foco
        	switch (focusedPage) {
			//diccionariu
        	case 0:
            	tableViewResultDiccionariu = (UITableView) mPagerAdapter.findViewById(0,R.id.tableViewResult);
            	etPalabra = (EditText) mPagerAdapter.findViewById(0,R.id.inputpalabra);
    			//limpiar antes la ultima busqueda
                tableViewResultDiccionariu.clear();
                tableViewResultDiccionariu.commit();
        		//si la palabra a buscar esta vacia, error
        		if (etPalabra.length() == 0){
        			mostrarMensaje(getApplicationContext().getString(R.string.palabraObligatoria));
        		} else {
        			actionBar.setProgressBarVisibility(View.VISIBLE);
        			new consultaPalabra(etPalabra.getText().toString(), 0).execute();        			
        		}
				break;
			//conxugar
        	case 1:		
            	tableViewResultConxugar = (UITableView) mPagerAdapter.findViewById(1,R.id.tableViewResult);
            	etVerbo = (EditText) mPagerAdapter.findViewById(1,R.id.inputpalabra);
    			//limpiar antes la ultima busqueda
                tableViewResultConxugar.clear();
                tableViewResultConxugar.commit();
        		//si la palabra a buscar esta vacia, error
        		if (etVerbo.length() == 0){
        			mostrarMensaje(getApplicationContext().getString(R.string.verboObligatorio));
        		} else {
        			actionBar.setProgressBarVisibility(View.VISIBLE);
        			new consultaVerbo().execute();        			
        		}
				break;
			//traducir
        	case 2:	
            	tableViewResultTraducir = (UITableView) mPagerAdapter.findViewById(2,R.id.tableViewResult);
            	etTexto = (EditText) mPagerAdapter.findViewById(2,R.id.inputtexto);
    			//limpiar antes la ultima busqueda
                tableViewResultTraducir.clear();
                tableViewResultTraducir.commit();
        		//si la palabra a buscar esta vacia, error
        		if (etTexto.length() == 0){
        			mostrarMensaje(getApplicationContext().getString(R.string.textoObligatorio));
        		} else {
        			actionBar.setProgressBarVisibility(View.VISIBLE);
        			new traducirTexto().execute();        			
        		}
				break;
			}
        	
			//quitar el teclado
			InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		    mgr.hideSoftInputFromWindow(actionBar.getWindowToken(), 0);
        }
    }
    
	//metodo parar añadir la opción de buscar
    private class clearAction extends AbstractAction {

        public clearAction() {
            super(R.drawable.clear_document);
        }

        @Override
        public void performAction(View view) {
        	
            switch (focusedPage) {
    		case 0:	
    			result_diccionario = "";
    			etPalabra.setText("");
            	tableViewResultDiccionariu = (UITableView) mPagerAdapter.findViewById(0,R.id.tableViewResult);
                tableViewResultDiccionariu.clear();
                tableViewResultDiccionariu.commit();
    	        break;
    		case 1:
    			result_conxugar = "";
    			etVerbo.setText("");
            	tableViewResultConxugar = (UITableView) mPagerAdapter.findViewById(1,R.id.tableViewResult);
                tableViewResultConxugar.clear();
                tableViewResultConxugar.commit();
    	        break;
    		case 2:
    			result_traducir = "";
    			etTexto.setText("");
            	tableViewResultTraducir = (UITableView) mPagerAdapter.findViewById(2,R.id.tableViewResult);
                tableViewResultTraducir.clear();
                tableViewResultTraducir.commit();
    	        break;
    		} 
            initActionBar();
        }
    }
    
	//metodo parar añadir la opción de copiar resultado
    private class copyAction extends AbstractAction {

        public copyAction() {
            super(R.drawable.copy);
        }

        @Override
        public void performAction(View view) {

        	String textoCopiar = "";
            switch (focusedPage) {
    		case 0:	
    	        if (result_diccionario.length() > 0)
    	        {
    	        	textoCopiar = result_diccionario;
    	        }
    	        else
    	        {
    	        	mostrarMensaje(getApplicationContext().getString(R.string.textoNoCopiado));
    	        }
    	        break;
    		case 1:
    	        if (result_conxugar.length() > 0)
    	        {
    	        	textoCopiar = result_conxugar;
    	        }
    	        else
    	        {
    	        	mostrarMensaje(getApplicationContext().getString(R.string.textoNoCopiado));
    	        }
    	        break;
    		case 2:
    	        if (result_traducir.length() > 0)
    	        {
    	        	textoCopiar = result_traducir;
    	        }
    	        else
    	        {
    	        	mostrarMensaje(getApplicationContext().getString(R.string.textoNoCopiado));
    	        }
    	        break;
    		} 
			//copiar a portapapeles
			ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE); 
			clipboard.setText(textoCopiar);
			
			//Mensaje de texto copiado
			mostrarMensaje(getApplicationContext().getString(R.string.textoCopiado));
        }
    }
    
	//metodo que mostrara los distintos errores de la pantalla
	public void mostrarMensaje (String mensaje){
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, mensaje, duration);
		toast.show();
	}
	
	//inicializar gestor de paginación horizontal
	private void initViewPager(int pageCount) {
		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new DiccionariuPagerAdapter(this, data);		
		mPager.setAdapter(mPagerAdapter);
		mPager.setCurrentItem(focusedPage); 
		mPager.setPageMargin(1);
	}
	
	//acción a realizar cuando el usuario cambia la pantalla
	private class MyChangePageListener implements com.astuetz.viewpager.extensions.ChangePageListener{

		@Override
		public void onHandle(int position) {
	    	focusedPage = position;
	        
	    	initActionBar();
	    	
	    	actionBar.setProgressBarVisibility(View.GONE);
		}
	}
	
	//inicializar action bar
	private void initActionBar() {
        actionBar = (ActionBar) findViewById(R.id.actionbar);
        actionBar.setHomeAction(new IntentAction(this, main.createIntent(this), R.drawable.ic_title_home_default));
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        actionBar.removeAllActions();
        
        actionBar.addAction(new clearAction());
        
        switch (focusedPage) {
		case 0:
	        actionBar.setTitle(getApplicationContext().getString(R.string.guetarPallabra));			
	        if (result_diccionario.length() > 0)
	        {
	        	actionBar.addAction(new copyAction());
	        }	        
	        break;
		case 1:
	        actionBar.setTitle(getApplicationContext().getString(R.string.conxugarVerbu));			
	        if (result_conxugar.length() > 0)
	        {
	        	actionBar.addAction(new copyAction());
	        }
	        break;
		case 2:
	        actionBar.setTitle(getApplicationContext().getString(R.string.traducirTestu));			
	        if (result_traducir.length() > 0)
	        {
	        	actionBar.addAction(new copyAction());
	        }
	        break;
		}      
        
        actionBar.addAction(new searchAction());   
	}	

	
	private void initViews()
	{		
        data.clear();
		
		View vi; // Creating an instance for View Object
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		RelativeLayout contenedor;

		vi = inflater.inflate(R.layout.diccionariu, null);
		contenedor = (RelativeLayout) vi.findViewById(R.id.contenedor);		
		etPalabra = (EditText) contenedor.findViewById(R.id.inputpalabra);		
		data.add(contenedor);

		vi = inflater.inflate(R.layout.conxugar, null);
		contenedor = (RelativeLayout) vi.findViewById(R.id.contenedor);
		etVerbo = (EditText) contenedor.findViewById(R.id.inputpalabra);
		data.add(contenedor);

		vi = inflater.inflate(R.layout.traducir, null);
		contenedor = (RelativeLayout) vi.findViewById(R.id.contenedor);
		etTexto = (EditText) contenedor.findViewById(R.id.inputtexto);
		tableViewIdioma = (UITableView) contenedor.findViewById(R.id.tableViewIdioma);
		
		if (traducir_direccion == 0)
		{
			tableViewIdioma.addBasicItem(getApplicationContext().getString(R.string.rbAstEs));
		}
		else
		{
			tableViewIdioma.addBasicItem(getApplicationContext().getString(R.string.rbEsAst));
		}		
		
		IdiomaTableViewClickListener listener = new IdiomaTableViewClickListener();
    	tableViewIdioma.setClickListener(listener);
        tableViewIdioma.commit();		
		data.add(contenedor);			
	}
	
	//tarea asincrona para mostrar el dialogprogress mientras buscamos la palabra
    private class consultaPalabra extends AsyncTask<String, Void, String> {
    	
    	private String palabra;
    	private int tipo;
    	
    	public consultaPalabra (String palabra, int tipo) {
    		this.palabra = palabra;
    		this.tipo = tipo;
    	}
    	
        protected void onPreExecute() {
        	//lanzar dialer de busqueda de la palabra
        	actionBar.setProgressBarVisibility(View.VISIBLE);
        	
        	//sacar mensaje de busqueda
        	tableViewResultDiccionariu.clear();
	        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			RelativeLayout v = (RelativeLayout) mInflater.inflate(R.layout.result, null);
        	
			TextView tv = (TextView) v.findViewById(R.id.result);
			tv.setText(getApplicationContext().getString(R.string.dialerBuscar));      			

			ViewItem v2 = new ViewItem(v);
			v2.setClickable(false);
			tableViewResultDiccionariu.addViewItem(v2);        			

	        tableViewResultDiccionariu.commit();
			
	        //inicializar variables globales
			RC = -1;
			result_diccionario = ""; 
			
			//inicializar action bar
			initActionBar();        	
        }

        @Override
        protected String doInBackground(String... params) {
           	         	
   			diccionariu dicc = new diccionariu(palabra, tipo);			
	        
   			RC = dicc.RC;
   			listaPalabras = dicc.listaPalabras;
   			
   			return dicc.definicion;	
        }

        @Override
        protected void onPostExecute(String result) {
        	try {
        		if (RC == 0)
        		{
        			
	        		if (result.length() > 0)
	        		{	        			
	        			tableViewResultDiccionariu.clear();
	        			
		                //mostrar el resultado con formato html
	        	        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        			RelativeLayout v = (RelativeLayout) mInflater.inflate(R.layout.result, null);
	        			        	        
	        			tableViewResultDiccionariu.addBasicItem(new BasicItem(etPalabra.getText().toString(),"", false));
	        			
	        			TextView tv = (TextView) v.findViewById(R.id.result);
	        			tv.setText(Html.fromHtml(result), BufferType.SPANNABLE); 
	        			result_diccionario = Html.fromHtml(result).toString(); 
	
	        			ViewItem v2 = new ViewItem(v);
	        			v2.setClickable(false);
	        			tableViewResultDiccionariu.addViewItem(v2);        			
	
	        	        tableViewResultDiccionariu.commit();
	        		}
        		}
        		else if (RC == 1)
        		{
        			
        			tableViewResultDiccionariu.clear();
        			
        			tableViewResultDiccionariu.addBasicItem(new BasicItem(getApplicationContext().getString(R.string.listaPalabras),"", false));
        			
        			for (tablePalabras palabra : listaPalabras) 
        			{
        				tableViewResultDiccionariu.addBasicItem(palabra.getcolPalabra());						
					}
        			
        			ListaPalabrasTableViewClickListener listener = new ListaPalabrasTableViewClickListener();
        			tableViewResultDiccionariu.setClickListener(listener);
        			
        			tableViewResultDiccionariu.commit();        			
        		}
        		else if (RC == 2)
        		{
        			
        			tableViewResultDiccionariu.clear();
        			
	                //mostrar el resultado con formato html
        	        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        			RelativeLayout v = (RelativeLayout) mInflater.inflate(R.layout.result, null);
        			        	        
        			tableViewResultDiccionariu.addBasicItem(new BasicItem(etPalabra.getText().toString(),"", false));
        			
        			TextView tv = (TextView) v.findViewById(R.id.result);
        			tv.setText(Html.fromHtml(result), BufferType.SPANNABLE); 
        			result_diccionario = Html.fromHtml(result).toString(); 

        			ViewItem v2 = new ViewItem(v);
        			v2.setClickable(false);
        			tableViewResultDiccionariu.addViewItem(v2);
        			
        			for (tablePalabras palabra : listaPalabras) 
        			{
        				tableViewResultDiccionariu.addBasicItem(getApplicationContext().getString(R.string.verSignificau) + ": " + palabra.getcolPalabra());						
					}
        			
        			ListaPalabrasDefTableViewClickListener listener = new ListaPalabrasDefTableViewClickListener();
        			tableViewResultDiccionariu.setClickListener(listener);
        			
        			tableViewResultDiccionariu.commit();        			
        		}
        		

			} catch (Exception e) { 
				e.printStackTrace(); 
				Log.d(TAG, "Error al presentar resultado: "+ e.toString()); 
			}
        	
    		actionBar.setProgressBarVisibility(View.GONE);
			//inicializar action bar
			initActionBar(); 
			
			if (result_diccionario.length() == 0 && !(RC == 1))
			{
    			tableViewResultDiccionariu.clear();   			
    			tableViewResultDiccionariu.commit(); 
    			
    			mostrarMensaje(getApplicationContext().getString(R.string.noEncontrada));
			}
        }
    }
    
	//tarea asincrona para mostrar el dialogprogress mientras buscamos el verbo
    private class consultaVerbo extends AsyncTask<String, Void, String> {
    	
        protected void onPreExecute() {
        	//lanzar dialer de busqueda de la palabra
        	actionBar.setProgressBarVisibility(View.VISIBLE);
        	
        	//sacar mensaje de busqueda
        	tableViewResultConxugar.clear();
	        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			RelativeLayout v = (RelativeLayout) mInflater.inflate(R.layout.result, null);
        	
			TextView tv = (TextView) v.findViewById(R.id.result);
			tv.setText(getApplicationContext().getString(R.string.dialerConxugar));      			

			ViewItem v2 = new ViewItem(v);
			v2.setClickable(false);
			tableViewResultConxugar.addViewItem(v2);        			

	        tableViewResultConxugar.commit();
			
	        //inicializar variables globales
			result_conxugar = ""; 
			
			//inicializar action bar
			initActionBar();
        }

        @Override
        protected String doInBackground(String... params) {
           	         	
   			conxugar conx = new conxugar(etVerbo.getText().toString());		
	        return conx.resultado;		        	
        }

        @Override
        protected void onPostExecute(String result) {
        	try {
        		if (result.length() > 0){
        			
        			tableViewResultConxugar.clear();
        			
	                //mostrar el resultado con formato html        	        
        	        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        			RelativeLayout v = (RelativeLayout) mInflater.inflate(R.layout.result, null);
        			        	        
        			tableViewResultConxugar.addBasicItem(new BasicItem(etVerbo.getText().toString(),"", false));
        			
        			TextView tv = (TextView) v.findViewById(R.id.result);
        			tv.setText(Html.fromHtml(result), BufferType.SPANNABLE);
        			result_conxugar = Html.fromHtml(result).toString(); 

        			ViewItem v2 = new ViewItem(v);
        			v2.setClickable(false);
        			tableViewResultConxugar.addViewItem(v2);        			

        			tableViewResultConxugar.commit();        			
        		}
        		
			} catch (Exception e) { 
				e.printStackTrace(); 
				Log.d(TAG, "Error al presentar resultado: "+ e.toString()); 
			}
        	
    		actionBar.setProgressBarVisibility(View.GONE);
			//inicializar action bar
			initActionBar(); 
			
			if (result_conxugar.length() == 0)
			{
    			tableViewResultConxugar.clear();   			
    			tableViewResultConxugar.commit();  
    			
    			mostrarMensaje(getApplicationContext().getString(R.string.verboNoExiste));
			}
        }
    }
    
	//tarea asincrona para mostrar el dialogprogress mientras buscamos la traduccion
    private class traducirTexto extends AsyncTask<String, Void, String> {
    	
        protected void onPreExecute() {
        	//lanzar dialer de busqueda de la palabra
        	actionBar.setProgressBarVisibility(View.VISIBLE);
        	
        	//sacar mensaje de busqueda
        	tableViewResultTraducir.clear();
	        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			RelativeLayout v = (RelativeLayout) mInflater.inflate(R.layout.result, null);
        	
			TextView tv = (TextView) v.findViewById(R.id.result);
			tv.setText(getApplicationContext().getString(R.string.dialerTraducir));      			

			ViewItem v2 = new ViewItem(v);
			v2.setClickable(false);
			tableViewResultTraducir.addViewItem(v2);        			

	        tableViewResultTraducir.commit();
			
	        //inicializar variables globales
			result_traducir = ""; 
			
			//inicializar action bar
			initActionBar();
        }

        @Override
        protected String doInBackground(String... params) {
           	         	
   			traducir trad = new traducir(etTexto.getText().toString(),traducir_direccion);		
	        return trad.resultado;			        	
        }

        @Override
        protected void onPostExecute(String result) {
        	try {
        		if (result.length() > 0){
        			
        			tableViewResultTraducir.clear();
        			tableViewResultTraducir.commit();
        			
	                //mostrar el resultado con formato html      	        
        	        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        			RelativeLayout v = (RelativeLayout) mInflater.inflate(R.layout.result, null);
        			
        			TextView tv = (TextView) v.findViewById(R.id.result);
        			tv.setText(Html.fromHtml(result));
        			result_traducir = Html.fromHtml(result).toString(); 

        			ViewItem v2 = new ViewItem(v);
        			v2.setClickable(false);
        			tableViewResultTraducir.addViewItem(v2);        			

        			tableViewResultTraducir.commit();        			
        		}
			} catch (Exception e) { 
				e.printStackTrace(); 
				Log.d(TAG, "Error al presentar resultado: "+ e.toString()); 
			}
        	
    		actionBar.setProgressBarVisibility(View.GONE);
			//inicializar action bar
			initActionBar(); 
			
			if (result_traducir.length() == 0)
			{
    			tableViewResultTraducir.clear();   			
    			tableViewResultTraducir.commit();  
			}
        }
    }
    
    //Listener para cambiar idioma
    private class IdiomaTableViewClickListener implements ClickListener {

		@Override
		public void onClick(int index) {
			
			if (traducir_direccion == 0)
			{
				traducir_direccion = 1;
			}
			else
			{
				traducir_direccion = 0;
			}
			
			tableViewIdioma.clear();
			
			if (traducir_direccion == 0)
			{
				tableViewIdioma.addBasicItem(getApplicationContext().getString(R.string.rbAstEs));
			}
			else
			{
				tableViewIdioma.addBasicItem(getApplicationContext().getString(R.string.rbEsAst));
			}    			
			
	        tableViewIdioma.commit();
		}    	
    }
    
    //Listener para palabras parecidas
    private class ListaPalabrasTableViewClickListener implements ClickListener {

		@Override
		public void onClick(int index) {
			
			if (RC == 1 && index > 0)
			{
				int indice = index - 1;
	
				actionBar.setProgressBarVisibility(View.VISIBLE);
				etPalabra.setText(listaPalabras.get(indice).getcolPalabra());
				new consultaPalabra(listaPalabras.get(indice).getcolCod(),1).execute();
			}
		}    	
    }
    
    //Listener para palabras definidas
    private class ListaPalabrasDefTableViewClickListener implements ClickListener {

		@Override
		public void onClick(int index) {
			
			if (RC == 2 && index > 1)
			{
				int indice = index - 2;
	
				actionBar.setProgressBarVisibility(View.VISIBLE);
				etPalabra.setText(listaPalabras.get(indice).getcolPalabra());
				new consultaPalabra(listaPalabras.get(indice).getcolPalabra(),0).execute();
			}
		}    	
    }
    
	//evaluar conexión a internet (si no hay, mostrar mensaje y volver al menú)
	private void evaluarConexion() {    	
		try {
	    	ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo comprueboInternet = cm.getActiveNetworkInfo();
	        if (comprueboInternet != null && comprueboInternet.isConnectedOrConnecting()) {
	        	checkConexion = true;
	        } else {
	        	checkConexion = false;
	        }    	
		} catch (Exception e) { 
			e.printStackTrace(); 
			Log.d(TAG, "Error al consultar si hay internet: "+ e.toString()); 
		}
		
    	//si no hay conexion volver a la pantalla principal
    	if (!checkConexion){
    		mostrarMensaje(getApplicationContext().getString(R.string.sinConexion));
    		this.finish();
    	}
	}
}