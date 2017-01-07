package fx.tools.internal

import scala.language.higherKinds
import scalafx.beans.value.ObservableValue

trait ObservableValueMType {
  type ObservableValueM[A] >: ObservableValue[A, _] <: ObservableValue[A, _]
}
