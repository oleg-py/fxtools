package fx.tools.monix

import javafx.collections.ObservableList

import scalafx.application.Platform.runLater
import scalafx.beans.property.Property

import fx.tools.internal.NotSubclass.¬<:<
import monix.execution.CancelableFuture
import monix.execution.Scheduler.Implicits.global
import monix.reactive.Observable

class PropertyToColonEqSyntax[A](self: Property[A, _]) {
  def :=[C](value: Observable[C])
    (implicit ev: A ¬<:< Iterable[_], conv: C => A): Unit = {
    bind(value.map(conv))
    ()
  }

  def :=[B, C](value: Observable[Iterable[C]])
    (implicit ev: A <:< ObservableList[B], conv: C => B): Unit = {
    bindList(value.map(_.map(conv)))
    ()
  }

  def bind(value: Observable[A]): CancelableFuture[Unit] = {
    for (a <- value) runLater { self() = a }
  }

  def bindList[B](value: Observable[Iterable[B]])
    (implicit ev: A <:< ObservableList[B]): CancelableFuture[Unit] = {
    for (seq <- value) runLater {
      val ol = ev(self())
      val iter = seq.iterator
      var idx = 0
      while (idx < ol.size && iter.hasNext) {
        val el = iter.next()
        if (el != ol.get(idx)) {
          ol.set(idx, el)
        }
        idx += 1
      }
      while (iter.hasNext) {
        ol.add(iter.next())
        idx += 1
      }
      if (idx < ol.size) {
        ol.remove(idx, ol.size())
      }
    }
  }
}
