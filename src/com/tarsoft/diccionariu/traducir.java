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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

/** 
 * Clase Desde donde se van a consultar las traducciones de un texto de APERTIUM 
 * y donde se van a mostrar
 * 
 *
 * @author itaravika (c) 2012
 * 
 */

public class traducir {
    /** Called when the activity is first created. */
	
	private final static String TAG = "Diccionariu";
	
	//entrada
	//private String texto;
	//private int direccion; 0 - Ast - Cast / 1 - Cast - Ast
	
	//resultados	
	public int RC; //0 - texto traducido / 9 - texto no traducido
	public String resultado;
	
	public traducir (String texto, int direccion) {
		/*
		this.texto = texto;
		this.direccion = direccion;
		*/

		//consultar APERTIUM
		String result_APERTIUM = consulta_APERTIUM(texto, direccion);
		
		//parsear resultado
		if (result_APERTIUM.length() > 0)
		{
			int posIni = result_APERTIUM.indexOf("transresult") + 13;
			int posFin = result_APERTIUM.indexOf("funding");
			
			if (posIni >= 0 && posFin > 0){
				RC = 0;
				resultado = result_APERTIUM.substring(posIni, posFin);
				int posFin2 = resultado.indexOf("</p>");
				if (posFin2 >= 0){
					resultado = resultado.substring(0,posFin2);
				}
			}
			else
			{
				RC = 9;
			}
		}
		else
		{
			RC = 9;
		}
	}
  	
    //Metodo que accede al diccionario APERTIUM y devuelve el resultado del verbo buscado
    //o el string de no encontrado
	//direccion = 0 - Ast - Cast / 1 - Cast - Ast
    public String consulta_APERTIUM(String texto, int direccion) {    	
        try {        	
	        	HttpClient client = new DefaultHttpClient();  
	            String postURL = "http://www.apertium.org/index.php?id=translatetext";
	            HttpPost post = new HttpPost(postURL);
	            List<NameValuePair> params = new ArrayList<NameValuePair>();
	            params.add(new BasicNameValuePair("textbox",texto));
	            
	            if (direccion == 1)
				{
	            	params.add(new BasicNameValuePair("direction","es-ast"));
	            } else  if (direccion == 0)
				{
	            	params.add(new BasicNameValuePair("direction","ast-es"));
	            }
	            
	            //marcar las palabras incorrectas
	            params.add(new BasicNameValuePair("mark","1"));
	            
	            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);	            
	            post.setEntity(ent);
	
	            HttpResponse responsePOST = client.execute(post);
	            HttpEntity resEntity = responsePOST.getEntity();
	            //si se encuentra, devolverlo
	            if (resEntity != null) {
	            	String _response=EntityUtils.toString(resEntity, HTTP.UTF_8);
	            	return _response;
	            }	           
        	
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG,"Error en el acceso a APERTIUM: " + e.getMessage());
        }
        //si llega aqui no se ha encontrado la palabra
        return "";
    }
}