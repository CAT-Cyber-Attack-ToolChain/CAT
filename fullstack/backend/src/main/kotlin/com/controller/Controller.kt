package com.controller

import com.view.Updatable

abstract class Controller {
    private var list = mutableListOf<Updatable>()

    fun addObserver(observer: Updatable) {
        list.add(observer)
    }

    fun removeObserver(observer: Updatable) {
        list.remove(observer)
    }

    fun notifyObservers() {
        list.forEach() {
            it.update()
        }
    }
}