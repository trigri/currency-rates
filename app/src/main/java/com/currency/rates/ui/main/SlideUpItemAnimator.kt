package com.currency.rates.ui.main

import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator


class SlideUpItemAnimator : SimpleItemAnimator() {
    var pendingAdditions: MutableList<RecyclerView.ViewHolder> = ArrayList()
    var pendingRemovals: MutableList<RecyclerView.ViewHolder> = ArrayList()

    override fun runPendingAnimations() {
        val additionsTmp = pendingAdditions
        val removalsTmp = pendingRemovals
        pendingAdditions = ArrayList()
        pendingRemovals = ArrayList()
        for (removal in removalsTmp) { // run the pending remove animation
            animateRemoveImpl(removal)
        }
        removalsTmp.clear()
        if (!additionsTmp.isEmpty()) {
            val adder = Runnable {
                for (addition in additionsTmp) { // run the pending add animation
                    animateAddImpl(addition)
                }
                additionsTmp.clear()
            }
            // play the add animation after the remove animation finished
            ViewCompat.postOnAnimationDelayed(
                additionsTmp[0].itemView,
                adder,
                removeDuration
            )
        }
    }

    override fun animateAdd(holder: RecyclerView.ViewHolder): Boolean {
        pendingAdditions.add(holder)
        // translate the new items vertically so that they later slide in from the bottom
        holder.itemView.translationY = 300f
        // also make them invisible
        holder.itemView.alpha = 0f
        // this requests the execution of runPendingAnimations()
        return true
    }

    override fun animateRemove(holder: RecyclerView.ViewHolder): Boolean {
        pendingRemovals.add(holder)
        // this requests the execution of runPendingAnimations()
        return true
    }

    private fun animateAddImpl(holder: RecyclerView.ViewHolder) {
        val view: View = holder.itemView
        val anim = ViewCompat.animate(view)
        anim // undo the translation we applied in animateAdd
            .translationY(0f) // undo the alpha we applied in animateAdd
            .alpha(1f)
            .setDuration(addDuration)
            .setInterpolator(DecelerateInterpolator())
            .setListener(object : ViewPropertyAnimatorListener {
                override fun onAnimationStart(view: View?) {
                    dispatchAddStarting(holder)
                }

                override fun onAnimationEnd(view: View) {
                    anim.setListener(null)
                    dispatchAddFinished(holder)
                    // cleanup
                    view.translationY = 0f
                    view.alpha = 1f
                }

                override fun onAnimationCancel(view: View?) {}
            }).start()
    }

    private fun animateRemoveImpl(holder: RecyclerView.ViewHolder) {
        val view: View = holder.itemView
        val anim = ViewCompat.animate(view)
        anim // translate horizontally to provide slide out to right
            .translationX(view.getWidth().toFloat()) // fade out
            .alpha(0f)
            .setDuration(removeDuration)
            .setInterpolator(AccelerateInterpolator())
            .setListener(object : ViewPropertyAnimatorListener {
                override fun onAnimationStart(view: View?) {
                    dispatchRemoveStarting(holder)
                }

                override fun onAnimationEnd(view: View) {
                    anim.setListener(null)
                    dispatchRemoveFinished(holder)
                    // cleanup
                    view.translationX = 0f
                    view.alpha = 1f
                }

                override fun onAnimationCancel(view: View?) {}
            }).start()
    }


    override fun animateMove(
        holder: RecyclerView.ViewHolder?,
        fromX: Int,
        fromY: Int,
        toX: Int,
        toY: Int
    ): Boolean { // don't handle animateMove because there should only be add/remove animations
        dispatchMoveFinished(holder)
        return false
    }

    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder?,
        newHolder: RecyclerView.ViewHolder?,
        fromLeft: Int,
        fromTop: Int,
        toLeft: Int,
        toTop: Int
    ): Boolean { // don't handle animateChange because there should only be add/remove animations
        if (newHolder != null) {
            dispatchChangeFinished(newHolder, false)
        }
        dispatchChangeFinished(oldHolder, true)
        return false
    }

    override fun endAnimations() {}
    override fun isRunning(): Boolean {
        return false
    }

    override fun endAnimation(item: RecyclerView.ViewHolder) {}
}