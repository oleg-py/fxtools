package compilation

import org.scalatest.{FlatSpec, Matchers}

class ReadOnlyPropertySpec extends FlatSpec with Matchers {
  behavior of "[property].observe() extension method"

  it should "support read-only object properties" in {
    """
       import monix.reactive._
       import scalafx.Includes._
       import scalafx.beans.property._
       import fx.tools.reactive.syntax._

       val prop: ReadOnlyObjectProperty[List[Int]] = ObjectProperty[List[Int]](Nil)
       val result: Observable[List[Int]] = prop.observe()
    """ should compile
  }

  it should "support read-only primitive properties" in {
    """
      import monix.reactive._
      import scalafx.Includes._
      import scalafx.beans.property._
      import fx.tools.reactive.syntax._

      val prop: ReadOnlyBooleanProperty = BooleanProperty(false)
      val result: Observable[Boolean] = prop.observe()
    """ should compile
  }
}
