package fx.tools.internal

/**
  * This is used as evidence that Property[A, _] is expected to be
  * encountered with second type parameter J, i.e. Property[A, J]
  *
  * @tparam A - ScalaFX type of given property
  * @tparam J - expected JavaFX type of that property
  */
sealed trait ToJavaFxAs[A, J]

object ToJavaFxAs extends ToJavaFxAsLowPriority {
  implicit val boolean : Boolean ToJavaFxAs java.lang.Boolean = null
  implicit val double  : Double  ToJavaFxAs Number  = null
  implicit val float   : Float   ToJavaFxAs Number  = null
  implicit val int     : Int     ToJavaFxAs Number  = null
  implicit val long    : Long    ToJavaFxAs Number  = null
}

sealed trait ToJavaFxAsLowPriority {
  implicit def any[A]: A ToJavaFxAs A = null
}
