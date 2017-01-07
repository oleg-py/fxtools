package fx.tools

import scala.language.implicitConversions

import javafx.beans.{InvalidationListener, Observable}
import javafx.beans.property.{ObjectProperty => JObjectProperty}
import javafx.collections.ObservableList
import javafx.event.{EventHandler, Event}

import scalafx.application.Platform
import scalafx.beans.binding.Bindings
import scalafx.beans.property.{ObjectProperty, Property}
import scalafx.beans.value.ObservableValue

import _root_.cats.Monad
import _root_.cats.syntax.functor._
import fx.tools.internal.{ObservableValueMType, ToJavaFxAs}

package object cats extends ObservableValueMType {
  implicit val catsMonadForObservableValueM: Monad[ObservableValueM] =
    new Monad[ObservableValueM] {
      override def map[A, B](fa: ObservableValueM[A])(f: (A) => B): ObservableValueM[B] =
        Bindings.createObjectBinding(() => f(fa()), fa)

      override def pure[A](x: A): ObservableValueM[A] = ObjectProperty(x)

      override def ap[A, B](ff: ObservableValueM[(A) => B])(fa: ObservableValueM[A]): ObservableValueM[B] = {
        Bindings.createObjectBinding(() => ff()(fa()), ff, fa)
      }

      override def flatMap[A, B](fa: ObservableValueM[A])(f: (A) => ObservableValueM[B]): ObservableValueM[B] = {
        var pb = f(fa())
        val opb = ObjectProperty(pb())
        fa onChange {
          pb = f(fa())
          opb() = pb()
        }
        pb onChange {
          opb() = pb()
        }
        opb
      }

      private def flatMapT[A, B](fa: ObservableValueM[A])(f: (A) => ObservableValueM[B]): ObservableValueM[B] = {
        var pb = f(fa())
        val opb = ObjectProperty(pb())
        fa onChange {
          pb = f(fa())
          Platform.runLater {
            opb() = pb()
          }
        }
        pb onChange {
          opb() = pb()
        }
        opb
      }

      override def tailRecM[A, B](a: A)(f: (A) => ObservableValueM[Either[A, B]]): ObservableValueM[B] = {
        flatMapT(f(a)) {
          case Right(b) => pure(b)
          case Left(nextA) => tailRecM(nextA)(f)
        }
      }

      override def product[A, B](fa: ObservableValueM[A], fb: ObservableValueM[B]): ObservableValueM[(A, B)] = {
        Bindings.createObjectBinding(() => (fa(), fb()), fa, fb)
      }
    }

  implicit class ObservableValueExistentialSyntax[A, J](val self: ObservableValue[A, J]) extends AnyVal {
    @inline def erase: ObservableValueM[A] = self
    @inline def M : ObservableValueM[A] = self
  }

  implicit class ObservableValue1Syntax[A](self: ObservableValueM[A]) {
    @inline def refine[J](implicit ev: A ToJavaFxAs J): ObservableValue[A, J] =
      self.asInstanceOf[ObservableValue[A, J]]

    def foreach[B](cb: A => Unit): Unit = {
      cb(self())
      self onChange {
        cb(self())
      }
      ()
    }
  }

  implicit class PropertyBindingToObservableValueMSyntax[A, J](val self: Property[A, J]) extends AnyVal {
    def <== (ov1: ObservableValueM[A])(implicit ev: A ToJavaFxAs J): Unit = self <== ov1.refine
  }

  implicit class JavaPropertyBindingToObservableValueMSyntax[J](val self: JObjectProperty[J]) extends AnyVal {
    def <==[A] (ov1: ObservableValueM[A])(implicit conv: A => J): Unit = self.bind(ov1.map(conv).refine[J].delegate)
  }

  implicit class ObservableListPropertyBinding[J](val self: JObjectProperty[ObservableList[J]]) extends AnyVal {
    def <==[A] (ov1: ObservableValueM[_ <: Seq[A]])(implicit conv: A => J): Unit = {
      ov1.foreach(syncLists(self.get, _))

      self.addListener(new InvalidationListener {
        override def invalidated(observable: Observable): Unit = {
          syncLists(self.get, ov1())
        }
      })

    }
  }

  implicit class ObservableListBinding[J](val self: ObservableList[J]) extends AnyVal {
    def <==[A] (ov1: ObservableValueM[_ <: Seq[A]])(implicit conv: A => J): Unit = {
      ov1.foreach(syncLists(self, _))
    }
  }

  @inline private def syncLists[A, J](ol: ObservableList[J], seq: Seq[A])(implicit conv: A => J) = {
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

  implicit def eventHandlerNothingSubstitution[A <: Event](eh: EventHandler[Nothing]): EventHandler[A] =
    eh.asInstanceOf[EventHandler[A]]
}
