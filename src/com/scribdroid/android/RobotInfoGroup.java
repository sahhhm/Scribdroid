package com.scribdroid.android;

import java.util.ArrayList;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class RobotInfoGroup extends ActivityGroup {

  public static RobotInfoGroup group;

  // Keep track of previously visited
  private ArrayList<View> history;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    history = new ArrayList<View>();
    group = this;

    // Start the root activity within the group and get its view
    View view = getLocalActivityManager().startActivity("RobotInfo",
        new Intent(this, RobotInfoActivity.class)).getDecorView();

    // replace view of group
    replaceView(view);
  }

  public void replaceView(View v) {
    history.add(v);
    setContentView(v);
  }

  public void back() {
    if (history.size() > 0) {
      history.remove(history.size() - 1);
      setContentView(history.get(history.size() - 1));
    }
  }

  @Override
  public void onBackPressed() {
    RobotInfoGroup.group.back();
    return;
  }

}
