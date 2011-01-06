package com.scribdroid.android;

import com.scribdroid.scribbler.Scribbler;
import android.app.Application;

public class MyApp extends Application {

	private Scribbler scrib = null;
	
	public Scribbler getScribbler(){
		return this.scrib;
	}
	
	public void setScribbler(Scribbler aScrib){
		this.scrib = aScrib;
	}
}
