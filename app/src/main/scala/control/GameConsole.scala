package control

import console.Transform
import model.{Entity, Target}
import org.mtrupkin.control.SpriteConsole
import org.mtrupkin.math.{Point, Size}

import scalafx.animation.TranslateTransition
import scalafx.event.ActionEvent
import scalafx.scene.{control => sfxc, input => sfxi, layout => sfxl}
import scalafx.scene.Node
import scalafx.scene.canvas.Canvas
import scalafx.scene.layout.Pane
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Line, StrokeLineJoin}
import scalafx.util.Duration
import scalafx.Includes._

/**
  * Created by mtrupkin on 8/23/2016.
  */

trait GameConsole extends Node {
  def clear(): Unit
  def draw(entity: Entity): Unit
  def pick(p: Point): Option[Target]
  def target(entity: Entity): Unit
  def cursor(entity: Entity): Unit
  def fireTorpedo(p1: Point, p0: Point): Unit
  def firePhaser(p1: Point, p0: Point): Unit
}

class GameConsoleImpl(screen: Size) extends Pane {
  minWidth = screen.width
  minHeight = screen.height
  var entities: Seq[(Point, Entity)] = Nil

  val canvas = new Canvas(screen.width, screen.height)
  val gc = canvas.graphicsContext2D
  val sprites = SpriteConsole(screen)

  children.add(canvas)
  children.add(sprites)

  def clear(): Unit = {
    gc.clearRect(0, 0, screen.width, screen.height)
    sprites.clear()
    entities = Nil
  }

  def pick(p: Point): Option[Target] = {
    def accept(e: (Point, Target)): Boolean = {
      val n = (p - e._1).normal
      n < (e._2.sprite.size / 2).normal
    }

    entities.collectFirst {
      case e: (Point, Target) if accept(e) => e._2
    }
  }

  def draw(p: Point, e: Entity): Unit = {
    entities = entities :+ (p, e)
    sprites.drawSprite(p, e.sprite)
  }

  def drawCrossHair(p: Point, size: Size, color: Color): Unit = {
    val adj = size / 2.0
    val p0 = p - adj

    gc.stroke = color
    gc.lineWidth = 3
    gc.lineJoin = StrokeLineJoin.Bevel

    gc.strokeRect(p0.x, p0.y, size.width, size.height)
  }

  def drawTarget(p: Point, size: Size): Unit = drawCrossHair(p, size, Color.WhiteSmoke)

  def drawCursor(p: Point, size: Size): Unit = drawCrossHair(p, size, Color.YellowGreen)

  def fireTorpedo(p1: Point, p0: Point): Unit = {
    val torpedo = new Circle() {
      radius = 5
      fill = Color.Red
    }

    children.add(torpedo)
  //    torpedo.relocate(p0.x + 5, p0.y + 5)

    val animation = new TranslateTransition(Duration(500), torpedo)
    animation.fromX = p0.x
    animation.fromY = p0.y

    animation.toX = p1.x
    animation.toY = p1.y

    animation.play()
  }

  def firePhaser(p1: Point, p0: Point): Unit = {
    val v = (p1 - p0).normal(50)
    val p2 = p1 - v

    val phaser = new Line() {
      managed = false
      stroke = Color.Red
      endX = v.x
      endY = v.y
    }
    children.add(phaser)

    val animation = new TranslateTransition(Duration(500), phaser) {
      fromX = p0.x
      fromY = p0.y

      toX = p2.x
      toY = p2.y

      onFinished = (e: ActionEvent) => { children.remove(phaser) }
    }

    animation.play()
  }
}

class TransformConsole(val transform: Transform) extends GameConsoleImpl(transform.screenSize) with GameConsole {
  import transform._

  def draw(e: Entity): Unit = {
    super.draw(screen(e.position), e)
  }

  override def target(entity: Entity): Unit = {
    super.drawTarget(screen(entity.position), entity.sprite.size)
  }

  override def cursor(entity: Entity): Unit = {
    super.drawCursor(screen(entity.position), entity.sprite.size)
  }

  override def fireTorpedo(p1: Point, p0: Point): Unit = {
    super.fireTorpedo(screen(p1), screen(p0))
  }

  override def firePhaser(p1: Point, p0: Point): Unit = {
    super.firePhaser(screen(p1), screen(p0))
  }
}

object GameConsole {
  def apply(t: Transform): GameConsole = new TransformConsole(t)
}
