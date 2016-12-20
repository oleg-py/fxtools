package fx.tools.internal

object NotSubclass {
  class ¬<:<[A, B]
  object ¬<:< extends LowPriorityImplicits {
    implicit def noImplicitForSubclass1[A, B](implicit ev: A <:< B): A ¬<:< B = sys.error("stub")
    implicit def noImplicitForSubclass2[A, B](implicit ev: A <:< B): A ¬<:< B = sys.error("stub")
  }

  private[NotSubclass] sealed trait LowPriorityImplicits {
    implicit def notSubclassEvidence[A, B] = new ¬<:<[A, B]
  }
}
