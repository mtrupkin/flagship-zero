package control

import console.Transform
import model.{Entity, Projectile, Ship, Targetable}
import org.mtrupkin.math.{Point, Size, Vect}

import scala.collection.mutable
import scala.concurrent.{Future, Promise}
import scalafx.animation.{FadeTransition, RotateTransition, SequentialTransition, TranslateTransition}
import scalafx.event.ActionEvent
import scalafx.scene.{control => sfxc, input => sfxi, layout => sfxl}
import scalafx.scene.Node
import scalafx.scene.canvas.Canvas
import scalafx.scene.layout.Pane
import scalafx.scene.paint.Color
import scalafx.scene.shape._
import scalafx.util.Duration
import scalafx.Includes._
import scalafx.scene.image.ImageView

/**
  * Created by mtrupkin on 8/23/2016.
  */
class GameConsole(val transform: Transform) extends Pane {
  val screen: Size = transform.screenSize
  minWidth = screen.width
  minHeight = screen.height
  val canvas = new Canvas(screen.width, screen.height)
  val gc = canvas.graphicsContext2D
  children.add(canvas)

  val movePath = new Path() {
    stroke = Color.Blue
  }
  children.add(movePath)

  // transform entity sprite to screen coordinate
  // adjust so sprite is centered on the position
  def toSpritePosition(p: Point, size: Size): Point = {
    val position: Point = transform.screen(p)
    val adj = size / 2.0
    position - adj
  }

  def toSpritePosition(e: Entity): Point = toSpritePosition(e.position, e.sprite.size)

  class EntityNode(val entity: Entity) extends ImageView(entity.sprite.imageView) {
    def size = entity.sprite.size
    // center of entity in screen coordinates
    def position = transform.screen(entity.position)
    // upper left of entity in screen coordinates
    def spritePosition = toSpritePosition(entity.position, size)

    update(0)

    def update(elapsed: Int): Unit = {
      val p0 = spritePosition
      if (screen.in(p0)) {
        translateX = p0.x
        translateY = p0.y
      }
    }
  }

  class TargetNode(val target: Targetable) extends EntityNode(target)

  class ShipNode(ship: Ship) extends TargetNode(ship) {
    override def update(elapsed: Int): Unit = {
      super.update(elapsed)
      rotate = -ship.heading.theta * 180 / Math.PI
    }
  }

  class ProjectileNode(p: Projectile) extends TargetNode(p) {
    override def update(elapsed: Int): Unit = {
      super.update(elapsed)
    }
  }

  var nodes: scala.collection.mutable.HashSet[EntityNode] = mutable.HashSet.empty

  def update(elapsed: Int, entities: Seq[Entity]): Unit = {
    //gc.clearRect(0,0,screen.width, screen.height)
    val removed = nodes.filterNot(n => entities.exists(e => n.entity == e))
    removed.foreach(r => {
      children -= r
      nodes -= r
    })

    entities.foreach(e => {
      val node = findEntityNode(e)
      node match {
        case Some(n) => n.update(elapsed)
        case None => createNode(e)
      }
    })
  }

  def pick(p: Point): Option[Targetable] = {
    def accept(e: EntityNode): Boolean = {
      val n = (p - e.position).normal
      n < (e.size / 2).normal
    }

    nodes.collectFirst {
      case t : TargetNode if accept(t) => t.target
    }
  }

  def createNode(e: Entity): Unit = {
    val node = e match {
      case ship: Ship => new ShipNode(ship)
      case projectile: Projectile => new ProjectileNode(projectile)
      case target: Targetable => new TargetNode(target)
      case _ => new EntityNode(e)
    }
    nodes += node
    children += node
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

  def displayLineMove(_p0: Point, _v: Vect): Unit = {
    val p0 = transform.screen(_p0)
    val _p1 = _p0 + _v
    val p1 = transform.screen(_p1)


    movePath.elements.clear()
    val moveTo = MoveTo(p0.x, p0.y)
    val move = LineTo(p1.x, p1.y)
    movePath.elements.addAll(moveTo, move)
  }

  def firePhaser(_p1: Point, _p0: Point): Future[Unit] = {
    val promise = Promise[Unit]()

    val p1 = transform.screen(_p1)
    val p0 = transform.screen(_p0)

    val v = (p1 - p0).normal(50)
    val p2 = p1 - v

    val phaser = new Line() {
      managed = false
      stroke = Color.Red
      endX = v.x
      endY = v.y
    }
    children.add(phaser)

    val animation = new TranslateTransition(Duration(200), phaser) {
      fromX = p0.x
      fromY = p0.y

      toX = p2.x
      toY = p2.y

      onFinished = (e: ActionEvent) => {
        children.remove(phaser)
        promise success ()
      }
    }

    animation.play()

    promise.future
  }

  def findEntityNode(entity: Entity): Option[EntityNode] = {
    nodes.find(_.entity == entity)
  }

  def destroy(entity: Entity): Future[Unit] = {
    val promise = Promise[Unit]()
    val entityNode = findEntityNode(entity).get
    val animation = new FadeTransition(Duration(500), entityNode) {
      fromValue = 1.0
      toValue = 0.3
      cycleCount = 1
      autoReverse = true

      onFinished = (e: ActionEvent) => {
        nodes = nodes.filterNot(_ == entityNode)
        children.remove(entityNode)
        promise success ()
      }
    }
    animation.play()
    promise.future
  }
}
