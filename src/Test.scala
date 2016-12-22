object Test extends App {
  import scalafx.beans.property._
  import fx.tools.reactive.syntax._

  val prop: ReadOnlyBooleanProperty = BooleanProperty(false)
  prop.observe()
}
