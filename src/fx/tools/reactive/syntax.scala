package fx.tools.reactive

import javafx.beans.{property => jprop, value => jvalue}
import javafx.{collections => jcoll}
import javafx.collections.ObservableList
import javafx.event.{Event, EventHandler}

import scala.language.implicitConversions
import scalafx.Includes._
import scalafx.application.Platform.runLater
import scalafx.beans.property.{ObjectProperty, Property, ReadOnlyObjectProperty}
import scalafx.beans.value.ObservableValue

import fx.tools.internal.NotSubclass._
import monix.execution.{Cancelable, CancelableFuture}
import monix.execution.Scheduler.Implicits.global
import monix.reactive.Observable
import monix.reactive.OverflowStrategy.Unbounded

object syntax {
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

  implicit def sfxObservableValueToColonEqSyntax[A]      (self: Property[A, _]         ): PropertyToColonEqSyntax[A]       = new PropertyToColonEqSyntax[A](self)
  implicit def jfxObjectPropertyToColonEqSyntax[A]       (self: jprop.ObjectProperty[A]): PropertyToColonEqSyntax[A]       = new PropertyToColonEqSyntax[A](self)
  implicit def jfxObservableBooleanValueToColonEqSyntax  (self: jprop.BooleanProperty  ): PropertyToColonEqSyntax[Boolean] = new PropertyToColonEqSyntax(self)
  implicit def jfxObservableIntValueToColonEqSyntax      (self: jprop.IntegerProperty  ): PropertyToColonEqSyntax[Int]     = new PropertyToColonEqSyntax(self)
  implicit def jfxObservableBooleanValueToColonEqSyntax  (self: jprop.DoubleProperty   ): PropertyToColonEqSyntax[Double]  = new PropertyToColonEqSyntax(self)
  implicit def jfxObservableBooleanValueToColonEqSyntax  (self: jprop.FloatProperty    ): PropertyToColonEqSyntax[Float]   = new PropertyToColonEqSyntax(self)
  implicit def jfxObservableBooleanValueToColonEqSyntax  (self: jprop.LongProperty     ): PropertyToColonEqSyntax[Long]    = new PropertyToColonEqSyntax(self)
  implicit def jfxObservableListToColonEqSyntax[A]       (self: jcoll.ObservableList[A]): PropertyToColonEqSyntax[ObservableList[A]]    = new PropertyToColonEqSyntax(ObjectProperty(self))


  class PropertyToObserveSyntax[A](val self: ObservableValue[A, _]) extends AnyVal {

    def observe(): Observable[A] = Observable.create(Unbounded) { sub =>
      sub.onNext(self.value)
      self.onChange {
        sub.onNext(self.value)
        ()
      }
      Cancelable()
    }
  }

  implicit def sfxObservableValueToObserveSyntax[A]      (self: ObservableValue[A, _]          ): PropertyToObserveSyntax[A]       = new PropertyToObserveSyntax[A](self)
  implicit def jfxObservableObjectValueToObserveSyntax[A](self: jvalue.ObservableObjectValue[A]): PropertyToObserveSyntax[A]       = new PropertyToObserveSyntax(self)
  implicit def jfxObservableBooleanValueToObserveSyntax  (self: jprop.ReadOnlyBooleanProperty  ): PropertyToObserveSyntax[Boolean] = new PropertyToObserveSyntax(self)
  implicit def jfxObservableIntValueToObserveSyntax      (self: jprop.ReadOnlyIntegerProperty  ): PropertyToObserveSyntax[Int]     = new PropertyToObserveSyntax(self)
  implicit def jfxObservableBooleanValueToObserveSyntax  (self: jprop.ReadOnlyDoubleProperty   ): PropertyToObserveSyntax[Double]  = new PropertyToObserveSyntax(self)
  implicit def jfxObservableBooleanValueToObserveSyntax  (self: jprop.ReadOnlyFloatProperty    ): PropertyToObserveSyntax[Float]   = new PropertyToObserveSyntax(self)
  implicit def jfxObservableBooleanValueToObserveSyntax  (self: jprop.ReadOnlyLongProperty     ): PropertyToObserveSyntax[Long]    = new PropertyToObserveSyntax(self)

  implicit class ObservableToPropertySyntax[A](observable: Observable[A]) {
    def property(initial: A): ReadOnlyObjectProperty[A] = {
      val prop = ObjectProperty[A](initial)
      observable.foreach(prop.update)
      prop
    }
  }

  // A special case for observables of handle { ... } because Java class has no variance
  implicit def eventHandlerSubst[A <: Event](eh: EventHandler[Nothing]): EventHandler[A] =
    eh.asInstanceOf[EventHandler[A]]
}
