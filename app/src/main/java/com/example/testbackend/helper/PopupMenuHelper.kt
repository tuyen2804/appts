package com.example.testbackend.helper

import android.content.Context
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.PopupMenu

class PopupMenuHelper(private val context: Context) {

    fun showPopupMenu(view: View, data: List<String>, editText: EditText, onItemSelected: ((MenuItem) -> Unit)? = null) {
        val popupMenu = PopupMenu(context, view)
        data.forEachIndexed { index, item ->
            popupMenu.menu.add(0, index, 0, item)
        }

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            editText.setText(menuItem.title)
            onItemSelected?.invoke(menuItem)
            true
        }

        popupMenu.show()
    }
}
