package com.illidancstormrage.utils.snackbar

import android.view.View
import com.google.android.material.snackbar.Snackbar

/**
 * @param message SnackBar显式文本
 * @param duration SnackBar持续时间
 * @param actionBtnText SnackBar按钮文本
 * @param action SnackBar设置属性
 * @param clickListener 按钮响应行为
 */
fun View.showSnackBar(
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT,
    actionBtnText: String? = null,
    action: (Snackbar.() -> Unit)? = null,
    clickListener: (() -> Unit)? = null,
) {
    val snackBar = Snackbar.make(this, message, duration)
    if (actionBtnText != null && clickListener != null) {
        snackBar.setAction(actionBtnText) {
            clickListener()
        }
    }
    if (action != null) {
        snackBar.action()
    }
    snackBar.show()
}
