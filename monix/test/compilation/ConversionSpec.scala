package compilation

import org.scalatest.{FlatSpec, Matchers}

class ConversionSpec extends FlatSpec with Matchers {
  behavior of "[property of A] := [observable of B]"

  it should "bind property to observable if implicitly[B => A]" in {
    """
       import javafx.beans.property._

       import scala.language.implicitConversions

       import monix.reactive.Observable
       import fx.tools.monix._

       trait A
       trait B

       implicit def btoa(b: B): A = new A { }

       val ol: ObjectProperty[A] = null
       ol := Observable[B](new B { })
    """ should compile
  }
}
