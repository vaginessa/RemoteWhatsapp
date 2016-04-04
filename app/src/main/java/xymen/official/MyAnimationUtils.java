package xymen.official;

import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;

public class MyAnimationUtils {

    public static void animate(RecyclerView.ViewHolder holder,Boolean isDown){
        ObjectAnimator animator = ObjectAnimator.ofFloat(holder.itemView,"translationY",isDown?100:-100,0);
        animator.setDuration(1000);
        animator.start();

    }
}
