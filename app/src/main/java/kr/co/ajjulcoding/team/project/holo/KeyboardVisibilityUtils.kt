package kr.co.ajjulcoding.team.project.holo

import android.view.Window

class KeyboardVisibilityUtils(
    private val window: Window,
    //private val onShowKeyboard: ((keyboardHeight: Int) -> Unit)? = null,
    private val onShowKeyboard: (() -> Unit)? = null,
    private val onHideKeyboard: (() -> Unit)? = null
) {
}