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

import android.text.Html;
import android.util.Log;

/** 
 * Clase Desde donde se van a consultar las conjugaciones de verbos de ESLEMA 
 * 
 *
 * @author itaravika (c) 2012
 * 
 */

public class conxugar {
    /** Called when the activity is first created. */
	
	private final static String TAG = "Diccionariu";
	
	//entrada
	//private String verbo;
	
	//resultados	
	public int RC; //0 - verbo conjugado / 9 - texto no conjugado
	public String resultado;
	
	public conxugar (String verbo) {
		/*
		this.verbo = verbo;
		*/

		//consultar DALLA
		String result_DALLA = consulta_DALLA(verbo);
		
		//parsear resultado
		if (result_DALLA.length() > 0)
		{
			//parsear el resultado de la consulta devuelta (la definicion comienza 
			//  en el primer <p> y finaliza en el primer </p>
			int posIni = result_DALLA.indexOf("conxugador2") - 10;
			int posFin = result_DALLA.indexOf("conxugador3") -10;
			
			if (posIni >= 0 && posFin > 0){
				RC = 0;
				resultado = convertirTablaHtml(result_DALLA.substring(posIni, posFin)); 
				if (resultado.length() == 0)
				{
					RC = 9;
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
    
    //Metodo que accede al diccionario ESLEMA y devuelve el resultado del verbo buscado
    //o el string de no encontrado
    public String consulta_DALLA(String verbo) {    	
        try {
        	
	        	HttpClient client = new DefaultHttpClient();  
	            String postURL = "http://di098.edv.uniovi.es/apertium/comun/conxugador.php";
	            HttpPost post = new HttpPost(postURL);
	            List<NameValuePair> params = new ArrayList<NameValuePair>();
            	params.add(new BasicNameValuePair("verbo", verbo));

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
            Log.d(TAG,"Error en el acceso a diccionariu: " + e.getMessage());
        }
        //si llega aqui no se ha encontrado la palabra
        return "";
    }
    
	//metodo que obtendra la tabla html con la conjugacion y la convertira a
    //un formato aceptable para presentar
	public String convertirTablaHtml (String tabla){
		//variable donde se va a devolver el resultado
		String resultado = "";
		
		//variable auxiliar que se va a ir recortando segun se va avanzando en el tratamiento de la tabla
		String tablaAux = "";
		
		//variables donde se van a ir dejando las distintas partes analizadas para luego unirlas
		String resFNPers = "";
		String resImperativo = "";
		String resIndicativo = "<b>[indicativu]</b><br><br>";
		String resIndicativoPresente = "";
		String resIndicativoPretImperfecto = "";
		String resIndicativoPretIndefinido = "";
		String resIndicativoPluscuamperfecto= "";
		String resSubjuntivo = "<br><b>[subxuntivu]</b><br><br>";
		String resSubjuntivoPresente = "";
		String resSubjuntivoPretImperfecto = "";
		String resPotencial = "<br><b>[potencial]</b><br><br>";
		String resPotencialFuturo = "";
		String resPotencialCondicional = "";
		
		
		//variables auxiliares de inicio y de fin
		int start = 0;
		int end = 0;
		
		try {
			//Inicio y fin de la tabla --> guardado en la variable tabla
			start = tabla.toLowerCase().indexOf("<table");
			if( start < 0 ) {
			    Log.d(TAG, "Error con las marcas de la tabla HTML <table>");
				return "";
			} else {
			    end = tabla.toLowerCase().indexOf("</table>", start) + 8;
			    if( end < 0 ) {
			        Log.d(TAG, "Error con las marcas de la tabla HTML </table>");
			    	return "";			    	
			    } else {
			        tabla = tabla.substring(start, end);
			    }
			}
			
			tablaAux = tabla;
			
			//Titulo Formes non personales --> se acumula en la variable resultado
			start = tabla.toLowerCase().indexOf("<b>");
			if( start < 0 ) {
			    Log.d(TAG, "Error con las marcas de la tabla HTML <b>");
				return "";
			} else {
			    end = tabla.toLowerCase().indexOf("</b>", start);
			    if( end < 0 ) {
			        Log.d(TAG, "Error con las marcas de la tabla HTML </b>");
			    	return "";			    	
			    } else {
			        resFNPers = "["+tabla.substring(start, end) + "]</b><br><br>";
			        end = end + 4;
			    }
			}			
			tablaAux = tablaAux.substring(end);
			
			//Titulo Imperativu --> se acumula en la variable resultado
			// se suman 33 porque viene asi: <b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;imperativu</b>
			start = tablaAux.toLowerCase().indexOf("<b>") + 33;
			if( start < 0 ) {
			    Log.d(TAG, "Error con las marcas de la tabla HTML <b>");
				return "";
			} else {
			    end = tablaAux.toLowerCase().indexOf("</b>", start);
			    if( end < 0 ) {
			        Log.d(TAG, "Error con las marcas de la tabla HTML </b>");
			    	return "";			    	
			    } else {
			        resImperativo = "<b>["+ tablaAux.substring(start, end) + "]</b><br><br>";
			        end = end + 4;
			    }
			}			
			tablaAux = tablaAux.substring(end);
			
			//Contenido Formes no personales --> se acumula en la variable resultado
			//infinitivo
			start = tablaAux.toLowerCase().indexOf("<u>") + 3;
			if( start < 0 ) {
			    Log.d(TAG, "Error con las marcas de la tabla HTML <u>");
				return "";
			} else {
			    end = tablaAux.toLowerCase().indexOf("</u>", start);
			    if( end < 0 ) {
			        Log.d(TAG, "Error con las marcas de la tabla HTML </b>");
			    	return "";			    	
			    } else {
			    	resFNPers = resFNPers + "<b>" + tablaAux.substring(start, end) + ": </b>";
			    }
			}			
			tablaAux = tablaAux.substring(end);
			
			start = 11;
		    end = tablaAux.toLowerCase().indexOf("</td>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML </b>");
		    	return "";			    	
		    } else {
		    	resFNPers = resFNPers + tablaAux.substring(start, end) +"<br>";
		    }
		
			tablaAux = tablaAux.substring(end);
			
			//gerundio
			start = tablaAux.toLowerCase().indexOf("<u>") + 3;
			if( start < 0 ) {
			    Log.d(TAG, "Error con las marcas de la tabla HTML <u>");
				return "";
			} else {
			    end = tablaAux.toLowerCase().indexOf("</u>", start);
			    if( end < 0 ) {
			        Log.d(TAG, "Error con las marcas de la tabla HTML </b>");
			    	return "";			    	
			    } else {
			    	resFNPers = resFNPers + "<b>" + tablaAux.substring(start, end) + ": </b>";
			    }
			}			
			tablaAux = tablaAux.substring(end);
			
			start = 11;

		    end = tablaAux.toLowerCase().indexOf("</td>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML </b>");
		    	return "";			    	
		    } else {
		    	resFNPers = resFNPers + tablaAux.substring(start, end) + "<br>";
		    }
		
			tablaAux = tablaAux.substring(end);
			
			//participio
			start = tablaAux.toLowerCase().indexOf("<u>") + 3;
			if( start < 0 ) {
			    Log.d(TAG, "Error con las marcas de la tabla HTML <u>");
				return "";
			} else {
			    end = tablaAux.toLowerCase().indexOf("</u>", start);
			    if( end < 0 ) {
			        Log.d(TAG, "Error con las marcas de la tabla HTML </b>");
			    	return "";			    	
			    } else {
			    	resFNPers = resFNPers + "<b>" + tablaAux.substring(start, end) + ": </b>";
			    }
			}			
			tablaAux = tablaAux.substring(end);
			
			start = 11;

		    end = tablaAux.toLowerCase().indexOf("<br/><br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML </b>");
		    	return "";			    	
		    } else {
		    	String cadenaAux = tablaAux.substring(start, end);
		    	int endTemp = cadenaAux.toLowerCase().indexOf("<br/>");
		    	int startTemp = endTemp + 5;
		    	
		    	resFNPers = resFNPers + cadenaAux.substring(0, endTemp) + " - "+cadenaAux.substring(startTemp)+"<br><br><br>";

		    }
		
			tablaAux = tablaAux.substring(end);
			
			//Contenido Imperativu --> se acumula en la variable resultado
			// viene asi: t&uacute;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; fala<br/>vosotros/es&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; fal√°i<br/><br/><br/></td>
			//tu
		
			start = tablaAux.toLowerCase().indexOf("t&uacute");
			if( start < 0 ) {
			    Log.d(TAG, "Error con las marcas de la tabla HTML <td>");
				return "";
			} else {
			    end = tablaAux.toLowerCase().indexOf("<br/>", start);
			    if( end < 0 ) {
			        Log.d(TAG, "Error con las marcas de la tabla HTML <br/>");
			    	return "";			    	
			    } else {
			    	//a√±adir dos blancos al resultado
			    	int endAux = start + 9;
			    	resImperativo = resImperativo + tablaAux.substring(start, endAux) +
			    					"&nbsp;&nbsp;&nbsp;&nbsp;"+ tablaAux.substring(endAux, end) + "<br>";
			    }
			}			
			tablaAux = tablaAux.substring(end);
			
			//vosotros/es
			start = 5;
			if( start < 0 ) {
			    Log.d(TAG, "Error con las marcas de la tabla HTML");
				return "";
			} else {
			    end = tablaAux.toLowerCase().indexOf("<br/><br/><br/>", start);
			    if( end < 0 ) {
			        Log.d(TAG, "Error con las marcas de la tabla HTML");
			    	return "";			    	
			    } else {
			    	resImperativo = resImperativo + tablaAux.substring(start, end) +"<br><br><br>";
			    }
			}			
			tablaAux = tablaAux.substring(end);
			
			//Titulo Presente Indicativu --> se acumula en la variable resultado
			start = tablaAux.toLowerCase().indexOf("<u>") + 3;
			if( start < 0 ) {
			    Log.d(TAG, "Error con las marcas de la tabla HTML");
				return "";
			} else {
			    end = tablaAux.toLowerCase().indexOf("</u>", start);
			    if( end < 0 ) {
			        Log.d(TAG, "Error con las marcas de la tabla HTML ");
			    	return "";			    	
			    } else {
			    	resIndicativoPresente = "<b>"+tablaAux.substring(start, end) + "</b><br>";
			    }
			}			
			tablaAux = tablaAux.substring(end);
			
			//Titulo Preteritu imperfectu Indicativu --> se acumula en la variable resultado
			start = tablaAux.toLowerCase().indexOf("<u>") + 3;
			if( start < 0 ) {
			    Log.d(TAG, "Error con las marcas de la tabla HTML");
				return "";
			} else {
			    end = tablaAux.toLowerCase().indexOf("</u>", start);
			    if( end < 0 ) {
			        Log.d(TAG, "Error con las marcas de la tabla HTML ");
			    	return "";			    	
			    } else {
			    	resIndicativoPretImperfecto = "<b>"+tablaAux.substring(start, end) + "</b><br>";
			    }
			}			
			tablaAux = tablaAux.substring(end);
			
			//Titulo Preteritu indefinido Indicativu --> se acumula en la variable resultado
			start = tablaAux.toLowerCase().indexOf("<u>") + 3;
			if( start < 0 ) {
			    Log.d(TAG, "Error con las marcas de la tabla HTML");
				return "";
			} else {
			    end = tablaAux.toLowerCase().indexOf("</u>", start);
			    if( end < 0 ) {
			        Log.d(TAG, "Error con las marcas de la tabla HTML ");
			    	return "";			    	
			    } else {
			    	resIndicativoPretIndefinido = "<b>"+tablaAux.substring(start, end) + "</b><br>";
			    }
			}			
			tablaAux = tablaAux.substring(end);
			
			//Titulo Preteritu pluscuamperfecto Indicativu --> se acumula en la variable resultado
			start = tablaAux.toLowerCase().indexOf("<u>") + 3;
			if( start < 0 ) {
			    Log.d(TAG, "Error con las marcas de la tabla HTML");
				return "";
			} else {
			    end = tablaAux.toLowerCase().indexOf("</u>", start);
			    if( end < 0 ) {
			        Log.d(TAG, "Error con las marcas de la tabla HTML ");
			    	return "";			    	
			    } else {
			    	resIndicativoPluscuamperfecto = "<b>"+tablaAux.substring(start, end) + "</b><br>";
			    }
			}			
			tablaAux = tablaAux.substring(end);
			
			
			
			//Contenido Presente Indicativu --> se acumula en la variable resultado
			start = tablaAux.toLowerCase().indexOf("</tr>") + 5;
			tablaAux = tablaAux.substring(start);
			start = tablaAux.toLowerCase().indexOf("<td>") + 4;
			
			if( start < 0 ) {
			    Log.d(TAG, "Error con las marcas de la tabla HTML");
				return "";
			} else {
			    end = tablaAux.toLowerCase().indexOf("<br/>", start);
			    if( end < 0 ) {
			        Log.d(TAG, "Error con las marcas de la tabla HTML ");
			    	return "";			    	
			    } else {
			    	resIndicativoPresente = resIndicativoPresente + "yo"+tablaAux.substring(start, end) + "<br>";
			    }
			}			
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	
		    	resIndicativoPresente = resIndicativoPresente + "tú&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resIndicativoPresente = resIndicativoPresente + "Èl/ella/usté&nbsp;&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resIndicativoPresente = resIndicativoPresente + "nosotros/es&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resIndicativoPresente = resIndicativoPresente + "vosotros/es&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resIndicativoPresente = resIndicativoPresente + "ellos/es&nbsp;&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br><br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			
			
			//Contenido Preterito imperfecto --> se acumula en la variable resultado
			start = tablaAux.toLowerCase().indexOf("<td>") + 4;
			
			if( start < 0 ) {
			    Log.d(TAG, "Error con las marcas de la tabla HTML");
				return "";
			} else {
			    end = tablaAux.toLowerCase().indexOf("<br/>", start);
			    if( end < 0 ) {
			        Log.d(TAG, "Error con las marcas de la tabla HTML ");
			    	return "";			    	
			    } else {
			    	resIndicativoPretImperfecto = resIndicativoPretImperfecto + "yo"+tablaAux.substring(start, end) + "<br>";
			    }
			}			
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resIndicativoPretImperfecto = resIndicativoPretImperfecto + "tú&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resIndicativoPretImperfecto = resIndicativoPretImperfecto + "Èl/ella/usté&nbsp;&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resIndicativoPretImperfecto = resIndicativoPretImperfecto + "nosotros/es&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resIndicativoPretImperfecto = resIndicativoPretImperfecto + "vosotros/es&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resIndicativoPretImperfecto = resIndicativoPretImperfecto + "ellos/es&nbsp;&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br><br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			
			
			//Contenido Preterito indefinido --> se acumula en la variable resultado
			start = tablaAux.toLowerCase().indexOf("<td>") + 4;
			
			if( start < 0 ) {
			    Log.d(TAG, "Error con las marcas de la tabla HTML");
				return "";
			} else {
			    end = tablaAux.toLowerCase().indexOf("<br/>", start);
			    if( end < 0 ) {
			        Log.d(TAG, "Error con las marcas de la tabla HTML ");
			    	return "";			    	
			    } else {
			    	resIndicativoPretIndefinido = resIndicativoPretIndefinido + "yo"+tablaAux.substring(start, end) + "<br>";
			    }
			}			
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resIndicativoPretIndefinido = resIndicativoPretIndefinido + "tú&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resIndicativoPretIndefinido = resIndicativoPretIndefinido + "Èl/ella/usté&nbsp;&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resIndicativoPretIndefinido = resIndicativoPretIndefinido + "nosotros/es&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resIndicativoPretIndefinido = resIndicativoPretIndefinido + "vosotros/es&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resIndicativoPretIndefinido = resIndicativoPretIndefinido + "ellos/es&nbsp;&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br><br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			
			
			//Contenido Preterito Pluscuamperfecto Indicativo--> se acumula en la variable resultado
			start = tablaAux.toLowerCase().indexOf("<td>") + 4;
			
			if( start < 0 ) {
			    Log.d(TAG, "Error con las marcas de la tabla HTML");
				return "";
			} else {
			    end = tablaAux.toLowerCase().indexOf("<br/>", start);
			    if( end < 0 ) {
			        Log.d(TAG, "Error con las marcas de la tabla HTML ");
			    	return "";			    	
			    } else {
			    	resIndicativoPluscuamperfecto = resIndicativoPluscuamperfecto + "yo"+tablaAux.substring(start, end) + "<br>";
			    }
			}			
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resIndicativoPluscuamperfecto = resIndicativoPluscuamperfecto + "tú&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resIndicativoPluscuamperfecto = resIndicativoPluscuamperfecto + "Èl/ella/usté&nbsp;&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resIndicativoPluscuamperfecto = resIndicativoPluscuamperfecto + "nosotros/es&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resIndicativoPluscuamperfecto = resIndicativoPluscuamperfecto + "vosotros/es&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resIndicativoPluscuamperfecto = resIndicativoPluscuamperfecto + "ellos/es&nbsp;&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br><br>";
		    }
			tablaAux = tablaAux.substring(end);

			
			
			//Titulo Presente Subjuntivo --> se acumula en la variable resultado
			start = tablaAux.toLowerCase().indexOf("<u>") + 3;
			if( start < 0 ) {
			    Log.d(TAG, "Error con las marcas de la tabla HTML");
				return "";
			} else {
			    end = tablaAux.toLowerCase().indexOf("</u>", start);
			    if( end < 0 ) {
			        Log.d(TAG, "Error con las marcas de la tabla HTML ");
			    	return "";			    	
			    } else {
			    	resSubjuntivoPresente = "<b>"+tablaAux.substring(start, end) + "</b><br>";
			    }
			}			
			tablaAux = tablaAux.substring(end);
			
			//Titulo Preteritu imperfectu subjuntivo --> se acumula en la variable resultado
			start = tablaAux.toLowerCase().indexOf("<u>") + 3;
			if( start < 0 ) {
			    Log.d(TAG, "Error con las marcas de la tabla HTML");
				return "";
			} else {
			    end = tablaAux.toLowerCase().indexOf("</u>", start);
			    if( end < 0 ) {
			        Log.d(TAG, "Error con las marcas de la tabla HTML ");
			    	return "";			    	
			    } else {
			    	resSubjuntivoPretImperfecto = "<b>"+tablaAux.substring(start, end) + "</b><br>";
			    }
			}			
			tablaAux = tablaAux.substring(end);
			
			//Titulo Potencial Futuro --> se acumula en la variable resultado
			start = tablaAux.toLowerCase().indexOf("<u>") + 3;
			if( start < 0 ) {
			    Log.d(TAG, "Error con las marcas de la tabla HTML");
				return "";
			} else {
			    end = tablaAux.toLowerCase().indexOf("</u>", start);
			    if( end < 0 ) {
			        Log.d(TAG, "Error con las marcas de la tabla HTML ");
			    	return "";			    	
			    } else {
			    	resPotencialFuturo = "<b>"+tablaAux.substring(start, end) + "</b><br>";
			    }
			}			
			tablaAux = tablaAux.substring(end);
			
			//Titulo Potencial condicional --> se acumula en la variable resultado
			start = tablaAux.toLowerCase().indexOf("<u>") + 3;
			if( start < 0 ) {
			    Log.d(TAG, "Error con las marcas de la tabla HTML");
				return "";
			} else {
			    end = tablaAux.toLowerCase().indexOf("</u>", start);
			    if( end < 0 ) {
			        Log.d(TAG, "Error con las marcas de la tabla HTML ");
			    	return "";			    	
			    } else {
			    	resPotencialCondicional = "<b>"+tablaAux.substring(start, end) + "</b><br>";
			    }
			}			
			tablaAux = tablaAux.substring(end);
			
			
			
			//Contenido Presente subjuntivo --> se acumula en la variable resultado
			start = tablaAux.toLowerCase().indexOf("<td>") + 4;
			
			if( start < 0 ) {
			    Log.d(TAG, "Error con las marcas de la tabla HTML");
				return "";
			} else {
			    end = tablaAux.toLowerCase().indexOf("<br/>", start);
			    if( end < 0 ) {
			        Log.d(TAG, "Error con las marcas de la tabla HTML ");
			    	return "";			    	
			    } else {
			    	resSubjuntivoPresente = resSubjuntivoPresente + "yo"+tablaAux.substring(start, end) + "<br>";
			    }
			}			
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resSubjuntivoPresente = resSubjuntivoPresente + "tú&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resSubjuntivoPresente = resSubjuntivoPresente + "Èl/ella/usté&nbsp;&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resSubjuntivoPresente = resSubjuntivoPresente + "nosotros/es&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resSubjuntivoPresente = resSubjuntivoPresente + "vosotros/es&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resSubjuntivoPresente = resSubjuntivoPresente + "ellos/es&nbsp;&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br><br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			
			
			//Contenido Preterito imperfecto --> se acumula en la variable resultado
			start = tablaAux.toLowerCase().indexOf("<td>") + 4;
			
			if( start < 0 ) {
			    Log.d(TAG, "Error con las marcas de la tabla HTML");
				return "";
			} else {
			    end = tablaAux.toLowerCase().indexOf("<br/>", start);
			    if( end < 0 ) {
			        Log.d(TAG, "Error con las marcas de la tabla HTML ");
			    	return "";			    	
			    } else {
			    	resSubjuntivoPretImperfecto = resSubjuntivoPretImperfecto + "yo"+tablaAux.substring(start, end) + "<br>";
			    }
			}			
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resSubjuntivoPretImperfecto = resSubjuntivoPretImperfecto + "tú&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resSubjuntivoPretImperfecto = resSubjuntivoPretImperfecto + "Èl/ella/usté&nbsp;&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resSubjuntivoPretImperfecto = resSubjuntivoPretImperfecto + "nosotros/es&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resSubjuntivoPretImperfecto = resSubjuntivoPretImperfecto + "vosotros/es&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resSubjuntivoPretImperfecto = resSubjuntivoPretImperfecto + "ellos/es&nbsp;&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br><br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			
			
			//Contenido Potencial Futuro --> se acumula en la variable resultado
			start = tablaAux.toLowerCase().indexOf("<td>") + 4;
			
			if( start < 0 ) {
			    Log.d(TAG, "Error con las marcas de la tabla HTML");
				return "";
			} else {
			    end = tablaAux.toLowerCase().indexOf("<br/>", start);
			    if( end < 0 ) {
			        Log.d(TAG, "Error con las marcas de la tabla HTML ");
			    	return "";			    	
			    } else {
			    	resPotencialFuturo = resPotencialFuturo + "yo"+tablaAux.substring(start, end) + "<br>";
			    }
			}			
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resPotencialFuturo = resPotencialFuturo + "tú&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resPotencialFuturo = resPotencialFuturo + "Èl/ella/usté&nbsp;&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resPotencialFuturo = resPotencialFuturo + "nosotros/es&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resPotencialFuturo = resPotencialFuturo + "vosotros/es&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resPotencialFuturo = resPotencialFuturo + "ellos/es&nbsp;&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br><br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			
			
			//Contenido Potencial Condicional --> se acumula en la variable resultado
			start = tablaAux.toLowerCase().indexOf("<td>") + 4;
			
			if( start < 0 ) {
			    Log.d(TAG, "Error con las marcas de la tabla HTML");
				return "";
			} else {
			    end = tablaAux.toLowerCase().indexOf("<br/>", start);
			    if( end < 0 ) {
			        Log.d(TAG, "Error con las marcas de la tabla HTML ");
			    	return "";			    	
			    } else {
			    	resPotencialCondicional = resPotencialCondicional + "yo"+tablaAux.substring(start, end) + "<br>";
			    }
			}			
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resPotencialCondicional = resPotencialCondicional + "tú&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resPotencialCondicional = resPotencialCondicional + "Èl/ella/usté&nbsp;&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resPotencialCondicional = resPotencialCondicional + "nosotros/es&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resPotencialCondicional = resPotencialCondicional + "vosotros/es&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br>";
		    }
			tablaAux = tablaAux.substring(end);
			
			start = 5;
		    end = tablaAux.toLowerCase().indexOf("<br/>", start);
		    if( end < 0 ) {
		        Log.d(TAG, "Error con las marcas de la tabla HTML ");
		    	return "";			    	
		    } else {
		    	resPotencialCondicional = resPotencialCondicional + "ellos/es&nbsp;&nbsp;&nbsp;&nbsp;"+tablaAux.substring(start, end) + "<br><br>";
		    }
			tablaAux = tablaAux.substring(end);
			
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG,"Error convirtiendo conjugacion" + e.getMessage());
        }
		
		if (resImperativo.length() > 0 &&
				resIndicativoPresente.length() > 0 &&
				resIndicativoPretImperfecto.length() > 0 &&
				resIndicativoPretIndefinido.length() > 0 &&
				resIndicativoPluscuamperfecto.length() > 0 &&
				resSubjuntivoPresente.length() > 0 &&
				resSubjuntivoPretImperfecto.length() > 0 &&
				resPotencialFuturo.length() > 0 &&
				resPotencialCondicional.length() > 0)
		{
        //componer el resultado final y devolverlo
        resultado = resFNPers + resImperativo + resIndicativo + resIndicativoPresente + resIndicativoPretImperfecto +
		            resIndicativoPretIndefinido + resIndicativoPluscuamperfecto + resSubjuntivo + 
		            resSubjuntivoPresente + resSubjuntivoPretImperfecto + resPotencial +
		            resPotencialFuturo + resPotencialCondicional;
        
		return resultado;
		}
		else
		{
			return "";
		}
	}
}