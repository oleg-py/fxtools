package fx.tools.reactive

import javafx.collections.ObservableList

import scalafx.application.Platform.runLater
import scalafx.beans.property.{ObjectProperty, Property, ReadOnlyObjectProperty, ReadOnlyProperty}

import fx.tools.internal.NotSubclass._
import monix.execution.CancelableFuture
import monix.execution.Scheduler.Implicits.global
import monix.reactive.Observable
import monix.reactive.subjects.BehaviorSubject

object syntax {
  implicit class PropertyToObservableColonEqSyntax[Prop, A]
  (self: Prop)
  (implicit isProp: Prop => Property[A, _]) {

    def :=(value: Observable[A])
          (implicit ev: A ¬<:< Iterable[_]): Unit = {
      bind(value)
      ()
    }

    def :=[B](value: Observable[_ <: Iterable[B]])
             (implicit ev: A <:< ObservableList[B]): Unit = {
      bindList(value)
      ()
    }

    def bind(value: Observable[A]): CancelableFuture[Unit] = {
      for (a <- value) runLater {
        self() = a
      }
    }

    def bindList[B](value: Observable[_ <: Iterable[B]])
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

  implicit class PropertyToObservableSyntax[Prop, A]
  (self: Prop)
  (implicit isProp: Prop => ReadOnlyProperty[A, _]) {
    def observe(): Observable[A] = {
      val subj = BehaviorSubject[A](self())
      self.onChange {
        subj.onNext(self())
        ()
      }
      subj
    }
  }

  implicit class ObservableToPropertySyntax[A](observable: Observable[A]) {
    def property(initial: A): ReadOnlyObjectProperty[A] = {
      val prop = ObjectProperty[A](initial)
      observable.foreach(prop.update)
      prop
    }
  }
}
