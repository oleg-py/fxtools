package fx.tools.monix

import scalafx.beans.value.ObservableValue

import monix.execution.Cancelable
import monix.reactive.Observable
import monix.reactive.OverflowStrategy.Unbounded

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
