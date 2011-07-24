package com.scribdroid.android;

import com.scribdroid.scribbler.Scribbler;
import android.app.Application;

public class MyApp extends Application {

  private Scribbler scrib;

  public Scribbler getScribbler() {
    return scrib;
  }

  public void setScribbler(Scribbler aScrib) {
    scrib = aScrib;
  }
}
