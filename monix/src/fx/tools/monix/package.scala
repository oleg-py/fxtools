package fx.tools

import scala.language.implicitConversions

import javafx.beans.{property => jprop, value => jvalue}
import javafx.collections.ObservableList
import javafx.event.{Event, EventHandler}
import javafx.{collections => jcoll}

import scalafx.Includes._
import scalafx.beans.property.{ObjectProperty, Property, ReadOnlyObjectProperty}
import scalafx.beans.value.ObservableValue

import _root_.monix.execution.Scheduler.Implicits.global
import _root_.monix.reactive.Observable

package object monix {
  implicit def sfxObservableValueToColonEqSyntax[A]      (self: Property[A, _]         ): PropertyToColonEqSyntax[A]       = new PropertyToColonEqSyntax[A](self)
  implicit def jfxObjectPropertyToColonEqSyntax[A]       (self: jprop.ObjectProperty[A]): PropertyToColonEqSyntax[A]       = new PropertyToColonEqSyntax[A](self)
  implicit def jfxObservableBooleanValueToColonEqSyntax  (self: jprop.BooleanProperty  ): PropertyToColonEqSyntax[Boolean] = new PropertyToColonEqSyntax(self)
  implicit def jfxObservableIntValueToColonEqSyntax      (self: jprop.IntegerProperty  ): PropertyToColonEqSyntax[Int]     = new PropertyToColonEqSyntax(self)
  implicit def jfxObservableBooleanValueToColonEqSyntax  (self: jprop.DoubleProperty   ): PropertyToColonEqSyntax[Double]  = new PropertyToColonEqSyntax(self)
  implicit def jfxObservableBooleanValueToColonEqSyntax  (self: jprop.FloatProperty    ): PropertyToColonEqSyntax[Float]   = new PropertyToColonEqSyntax(self)
  implicit def jfxObservableBooleanValueToColonEqSyntax  (self: jprop.LongProperty     ): PropertyToColonEqSyntax[Long]    = new PropertyToColonEqSyntax(self)
  implicit def jfxObservableListToColonEqSyntax[A]       (self: jcoll.ObservableList[A]): PropertyToColonEqSyntax[ObservableList[A]]    = new PropertyToColonEqSyntax(ObjectProperty(self))

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
