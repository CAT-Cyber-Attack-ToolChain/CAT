package contoller

import view.Observer

abstract class Controller {
    private var list = mutableListOf<Observer>();

    fun addObserver(observer: Observer){
        list.add(observer)
    }

    fun removeObserver(observer: Observer) {
        list.remove(observer)
    }

    fun notifyObservers() {
        list.forEach(){
            it.update()
        }
    }
}