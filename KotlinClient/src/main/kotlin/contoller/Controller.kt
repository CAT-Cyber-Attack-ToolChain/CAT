package contoller

import view.Updateable

abstract class Controller {
  private var list = mutableListOf<Updateable>()

  fun addObserver(observer: Updateable) {
    list.add(observer)
  }

  fun removeObserver(observer: Updateable) {
    list.remove(observer)
  }

  fun notifyObservers() {
    list.forEach {
      it.update()
    }
  }
}