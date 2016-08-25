package org.mtrupkin.control

import javafx.scene.layout.Pane

import org.mtrupkin.math.{Point, Size}
import spriteset._

trait SpriteConsole extends Pane {
  /** prepare to draw */
  def clear(): Unit

  def drawSprite(p: Point, sprite: Sprite): Unit
}

class SpriteConsoleImpl(val screen: Size) extends SpriteConsole {
  setMinSize(screen.width, screen.height)

  def clear(): Unit = getChildren.clear()

  /** position is in screen coordinates and is in center of image */
  def drawSprite(p: Point, sprite: Sprite): Unit = {
    import sprite._
    val adj = size / 2.0
    val p0 = p - adj
    if (screen.in(p0)) {
      imageView.relocate(p0.x, p0.y)
      getChildren.add(imageView)
    }
  }
}


object SpriteConsole {
  def apply(screen: Size): SpriteConsole = new SpriteConsoleImpl(screen)
}