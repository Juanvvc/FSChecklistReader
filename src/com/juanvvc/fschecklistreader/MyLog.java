/*  FSChecklistReader is an Android application to show checklists for flight simulators
    Copyright (C) 2014, Juan Vera <juanvvc@gmail.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/


package com.juanvvc.fschecklistreader;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import android.util.Log;

/** Use this class instead of android.util.Log: simplify the
 * process of uploading to Google Play. During developing, set DEBUG=true
 * and messages will appear on the LogCat. Just before uploading to Google Play,
 * set DEBUG=false and messages won't appear and, more importantly, your application
 * will run slightly faster!
 * @author juanvi
 */
public class MyLog{
	private static boolean DEBUG=true;
	
	public static void setDebug(boolean d) {
		MyLog.DEBUG = d;
	}
	public static boolean isDebug() {
		return MyLog.DEBUG;
	}
	
	public static void i(Object o, String msg){
		if(DEBUG) Log.i(o.getClass().getSimpleName(), msg);
	}
	public static void d(Object o, String msg){
		if(DEBUG) Log.d(o.getClass().getSimpleName(), msg);
	}
	public static void v(Object o, String msg){
		if(DEBUG) Log.v(o.getClass().getSimpleName(), msg);
	}
	public static void e(Object o, String msg){
		if(DEBUG) Log.e(o.getClass().getSimpleName(), msg);
	}
	public static void w(Object o, String msg){
		if(DEBUG) Log.e(o.getClass().getSimpleName(), msg);
	}
	
	public static void i(String t, String msg){
		if(DEBUG) Log.i(t, msg);
	}
	public static void d(String t, String msg){
		if(DEBUG) Log.d(t, msg);
	}
	public static void v(String t, String msg){
		if(DEBUG) Log.v(t, msg);
	}
	public static void e(String t, String msg){
		if(DEBUG) Log.e(t, msg);
	}
	public static void w(String t, String msg){
		if(DEBUG) Log.e(t, msg);
	}
	
	public static String stackToString(Exception e) {
		Writer result = new StringWriter();
		PrintWriter printWriter = new PrintWriter(result);
		e.printStackTrace(printWriter);
		return result.toString();
	}
}