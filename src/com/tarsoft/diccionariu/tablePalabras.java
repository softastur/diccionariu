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

/** 
 * Clase (Tabla de Datos) que sirve para contener las palabras y/o
 * codigos para la lista de palabras propuestas
 *
 * @author itaravika (c) 2012
 * 
 */

public class tablePalabras {
	
	private String colPalabra ="";
	private String colCod ="";
	
	public tablePalabras(String colPalabra, String colCod) {
		
		this.colPalabra = colPalabra;
		this.colCod = colCod;		
	}

	public String getcolPalabra() {
		return colPalabra;
	}
 
	public void setcolPalabra(String colPalabra) {
		this.colPalabra = colPalabra;
	}
	
	public String getcolCod() {
		return colCod;
	}
 
	public void setcolCod(String colCod) {
		this.colCod = colCod;
	}

}

