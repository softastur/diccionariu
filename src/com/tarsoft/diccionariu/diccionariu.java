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
 * Clase desde donde se van a consultar las palabras del diccionariu DALLA
 * 
 *
 * @author itaravika (c) 2012
 * 
 */

public class diccionariu {
	
	private final static String TAG = "Diccionariu";
	
	//entrada
	//private String palabra;
	//private int tipo; // 0 - palabra / 1 - codigo
	
	//resultados	
	public int RC; //0 - Palabra encontrada / 1 - Palabras propuestas / 2 - Definici—n dentro de una palabra / 9 - Palabra no encontrada
	public String definicion;
	public List<tablePalabras> listaPalabras;
	

	public diccionariu (String palabra, int tipo) {
		/*
		this.palabra = palabra;
		this.tipo = tipo;
		*/

		//consultar DALLA
		String result_DALLA = consulta_DALLA(palabra, tipo);
				
		//parsear resultado
		if (result_DALLA.length() > 0)
		{
			if (tipo == 0)
			{
				if ((result_DALLA.length() > 21) & (result_DALLA.substring(4, 21).equals("Alcontr‡ronse les")))
				{
					listaPalabras = palabrasPropuestas (result_DALLA);
					RC = 1;
				}
				else if((result_DALLA.length() > 26) & (result_DALLA.indexOf("index.php?pallabra=") > 0))
				{
					listaPalabras = palabrasDefinidas (result_DALLA);
					definicion = result_DALLA;
					RC = 2;
				}
				else 
				{
					//ver si el resultado es que no se encontró ninguna palabra
					int encontrado = result_DALLA.indexOf("nenguna pallabra");					
					if (encontrado==19)
					{
						RC = 9;
					} 
					else 
					{
						RC = 0;
						definicion = result_DALLA;
					}
		        }
			}
			else if (tipo == 1)
			{
				if ((result_DALLA.length() > 13) & (result_DALLA.substring(4, 13).equals("Pallabra:")))
				{
		    		RC = 9;
		        } 
				else if((result_DALLA.length() > 26) & (result_DALLA.indexOf("index.php?pallabra=") > 0))
				{
					listaPalabras = palabrasDefinidas (result_DALLA);
					definicion = result_DALLA;
					RC = 2;
				}
				else 
				{
						RC = 0;
						definicion = result_DALLA;
		        }
			
			}
		}
		else
		{
			RC = 9;
		}
	}
    
    //Metodo que accede al diccionario DALLA y devuelve el resultado de la palabra buscada
    //se puede buscar por palabra o por codigo
	//tipo (0 - palabra / 1 - codigo)
    public String consulta_DALLA(String palabra, int tipo) {    	
        try {        	
	        	HttpClient client = new DefaultHttpClient();  
	            String postURL = "http://www.academiadelallingua.com/diccionariu/index.php";
	            HttpPost post = new HttpPost(postURL);
	            List<NameValuePair> params = new ArrayList<NameValuePair>();
	            if (tipo == 0){
	            	params.add(new BasicNameValuePair("pallabra", palabra));
	            } else {
	            	params.add(new BasicNameValuePair("cod", palabra));
	            }

	            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.ISO_8859_1);
	            post.setEntity(ent);
	
	            HttpResponse responsePOST = client.execute(post);
	            HttpEntity resEntity = responsePOST.getEntity();
	            //si se encuentra, devolverlo
	            if (resEntity != null) {
	            	String _response=EntityUtils.toString(resEntity);
	            	return parsea_DALLA(_response);
	            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG,"Error en el acceso a diccionariu: " + e.getMessage());
        }
        //si llega aqui no se ha encontrado la palabra
        return "";
    }
	
	//Metodo que parsea el resultado devuelto por DALLA
	//(la definicion comienza en el primer <p> y finaliza en el primer </p>)
    public String parsea_DALLA(String result_DALLA) {    	
        try {        	
			int posIni = result_DALLA.indexOf("<P>") - 1;
			int posFin = result_DALLA.indexOf("</P>") + 4;
			
			if (posIni >= 0 && posFin > 0){
				return result_DALLA.substring(posIni, posFin);
			}

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG,"Error en el acceso a diccionariu: " + e.getMessage());
        }
        //si llega aqui ha habido algún problema
        return "";
    }
	
	//metodo para parsear las palabras propuestas
    public List<tablePalabras> palabrasPropuestas (String html) {
    	
    	List<tablePalabras> palabras = new ArrayList<tablePalabras>();
    	
    	try{
	    	
	    	boolean salir = false;
	    	
	    	while (!salir){
	    		//detectar siguiente "<A HREF="
	    		int posRef = html.indexOf("<A HREF=");

	    		//detectar ultima palabra "<A HREF="
	    		int posFinal = html.lastIndexOf("<A HREF=");
	    		
	    		//si coinciden, esta es la ultima palabra
	    		if (posRef == posFinal){
	    			salir = true;
	    		}
	    		
	    		//obtener string desde la posicion encontrada
	    		String htmlTemp = html.substring(posRef);
	    		//Obtener Codigo
	    		//inicio a partir del = de "A HREF"
	    		//y fin antes del ">"
	    		int posIniCod = 23;
	    		int posFinCod = htmlTemp.indexOf(">") - 1;

	    		//Obtener Palabra
	    		//detectar siguiente ">" (lo anterior a la palabra propuesta)
	    		//y siguiente "</A><BR>" (lo siguiente a la palabra propuesta)
	    		int posIniPalabra = htmlTemp.indexOf(">") +1;
	    		int posFinPalabra = htmlTemp.indexOf("</A><BR>");
	    		
	    		//a?adir palabra a la lista
	    		tablePalabras tp = new tablePalabras(htmlTemp.substring(posIniPalabra, posFinPalabra), htmlTemp.substring(posIniCod, posFinCod));
	    		palabras.add(tp);
	    		
	    		//recortar string para la siguiente pasada
	    		html = htmlTemp.substring(posFinPalabra); 	
	    	}
    	
		} catch (Exception e) { 
			e.printStackTrace(); 
			Log.d(TAG, "Error al convertir resultado lista de palabras: "+ e.toString()); 
		}
    	return palabras;
    }
    
	//metodo para parsear las palabras propuestas
    public List<tablePalabras> palabrasDefinidas (String html) {
    	
    	List<tablePalabras> palabras = new ArrayList<tablePalabras>();
    	
    	try{
	    	
	    	boolean salir = false;
	    	
	    	while (!salir){
	    		//detectar siguiente "<A HREF="
	    		int posRef = html.indexOf("HREF=");

	    		//detectar ultima palabra "<A HREF="
	    		int posFinal = html.lastIndexOf("HREF=");
	    		
	    		//si coinciden, esta es la ultima palabra
	    		if (posRef == posFinal){
	    			salir = true;
	    		}
	    		
	    		//obtener string desde la posicion encontrada
	    		String htmlTemp = html.substring(posRef);

	    		//Obtener Palabra
	    		//detectar siguiente ">" (lo anterior a la palabra propuesta)
	    		//y siguiente "</A><BR>" (lo siguiente a la palabra propuesta)
	    		int posIniPalabra = htmlTemp.indexOf(">") +1;
	    		int posFinPalabra = htmlTemp.indexOf("</A>") -3;
	    		
	    		//a?adir palabra a la lista
	    		tablePalabras tp = new tablePalabras(htmlTemp.substring(posIniPalabra, posFinPalabra), null);
	    		palabras.add(tp);
	    		
	    		//recortar string para la siguiente pasada
	    		html = htmlTemp.substring(posFinPalabra); 	
	    	}
    	
		} catch (Exception e) { 
			e.printStackTrace(); 
			Log.d(TAG, "Error al convertir resultado lista de palabras definidas: "+ e.toString()); 
		}
    	return palabras;
    }
}