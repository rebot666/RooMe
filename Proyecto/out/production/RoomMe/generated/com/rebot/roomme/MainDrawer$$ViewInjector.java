// Generated code from Butter Knife. Do not modify!
package com.rebot.roomme;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class MainDrawer$$ViewInjector {
  public static void inject(Finder finder, final com.rebot.roomme.MainDrawer target, Object source) {
    View view;
    view = finder.findById(source, 2130968626);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2130968626' for field 'title' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.title = (android.widget.TextView) view;
  }

  public static void reset(com.rebot.roomme.MainDrawer target) {
    target.title = null;
  }
}
