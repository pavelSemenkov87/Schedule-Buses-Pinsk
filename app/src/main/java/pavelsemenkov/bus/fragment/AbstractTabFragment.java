package pavelsemenkov.bus.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

public class AbstractTabFragment
  extends Fragment
{
  protected Context context;
  private String title;
  protected View view;
  
  public String getTitle()
  {
    return this.title;
  }
  
  public void setTitle(String paramString)
  {
    this.title = paramString;
  }
}
