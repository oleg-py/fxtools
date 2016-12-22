package compilation

import org.scalatest.{FlatSpec, Matchers}

class ObservableListSpec extends FlatSpec with Matchers {
  behavior of "[observable list] := [observable of seq]"

  it should "allow binding the values together" in {
    """
       import javafx.collections.ObservableList
       import monix.reactive.Observable
       import scalafx.Includes._ // unused, but should NOT cause conflicts
       import fx.tools.reactive.syntax._

       val ol: ObservableList[String] = null
       ol := Observable(Seq("1"), Seq("1", "2", "3"), Seq("Oh, screw that"))
    """ should compile
  }

  behavior of "[property of observable list] := [observable of seq]"

  it should "allow binding the values together" in {
    """
      import javafx.beans.property.ObjectProperty
      import javafx.collections.ObservableList
      import monix.reactive.Observable
      import scalafx.Includes._ // unused, but should NOT cause conflicts
      import fx.tools.reactive.syntax._

      val ol: ObjectProperty[ObservableList[String]] = null
      ol := Observable(Seq("1"), Seq("1", "2", "3"), Seq("Oh, screw that"))
    """ should compile
  }
}
