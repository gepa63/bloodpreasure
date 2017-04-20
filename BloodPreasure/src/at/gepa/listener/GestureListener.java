package at.gepa.listener;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class GestureListener extends GestureDetector.SimpleOnGestureListener
{
    
	public static String currentGestureDetected;
    
   // Override s all the callback methods of GestureDetector.SimpleOnGestureListener
   @Override
   public boolean onSingleTapUp(MotionEvent ev) {
       currentGestureDetected="onSingleTapUp: " + ev.toString();
    
     return true;
   }
   @Override
   public void onShowPress(MotionEvent ev) {
       currentGestureDetected="onShowPress: " +ev.toString();
     
   }
   @Override
   public void onLongPress(MotionEvent ev) {
       currentGestureDetected="onLongPress: " +ev.toString();
    
   }
   @Override
   public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
       //currentGestureDetected="onScroll: " +e1.toString()+ "  "+e2.toString();
	   currentGestureDetected="onScroll: distanceX=" + distanceX + " distanceY=" + distanceY;
   
     return true;
   }
   @Override
   public boolean onDown(MotionEvent ev) {
       currentGestureDetected="onDown: " +ev.toString();
     
     return true;
   }
   @Override
   public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
       currentGestureDetected="onFling: " +e1.toString()+ "  "+e2.toString();
     return true;
   }
}
