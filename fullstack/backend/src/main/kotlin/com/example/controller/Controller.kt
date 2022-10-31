package com.example.controller

import com.example.view.Updatable


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