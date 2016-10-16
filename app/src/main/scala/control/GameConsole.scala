package control

import console.Transform
import model.{Entity, Projectile, Ship, Target}
import org.mtrupkin.math.{Point, Size, Vect}

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

    def update(elapsed: Int): Unit = {
      val p0 = spritePosition
      if (screen.in(p0)) {
        translateX = p0.x
        translateY = p0.y
      }
    }
  }

  class TargetNode(val target: Target) extends EntityNode(target)

  class ShipNode(ship: Ship) extends TargetNode(ship) {
    override def update(elapsed: Int): Unit = {
      super.update(elapsed)
      rotate = -ship.heading.theta * 180 / Math.PI
    }
  }

  var nodes: List[EntityNode] = Nil

  def update(elapsed: Int): Unit = {
    //gc.clearRect(0,0,screen.width, screen.height)
    nodes.foreach(_.update(elapsed))
  }

  def pick(p: Point): Option[Target] = {
    def accept(e: EntityNode): Boolean = {
      val n = (p - e.position).normal
      n < (e.size / 2).normal
    }

    nodes.collectFirst {
      case t : TargetNode if accept(t) => t.target
    }
  }

  def add(e: Entity): Unit = {
    val node = e match {
      case ship: Ship => new ShipNode(ship)
      case target: Target => new TargetNode(target)
      case _ => new EntityNode(e)
    }

    children.add(node)
    nodes = node :: nodes
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

  def toEntityNode(entity: Entity): EntityNode = {
    nodes.find(_.entity == entity).get
  }

  def move(entity: Ship, _p: Point): Future[Unit] = {
    val p1 = toSpritePosition(_p, entity.sprite.size)//transform.screen(_p)
    val p0 = toSpritePosition(entity.position, entity.sprite.size)

    val promise = Promise[Unit]()
    val entityNode = toEntityNode(entity)

    val v = _p - entity.position

    val translate = new TranslateTransition(Duration(250), entityNode) {
      fromX = p0.x
      fromY = p0.y

      toX = p1.x
      toY = p1.y
    }

    val rotate = new RotateTransition(Duration(250), entityNode) {
      val radians = entity.heading.angle(v)
      val theta = -radians*180/Math.PI
      byAngle = theta
    }

    val seqAnimation = new SequentialTransition  {
      children.add(rotate)
      children.add(translate)
      onFinished = (e: ActionEvent) => promise success ()
    }
    seqAnimation.play()
    promise.future
  }

  def destroy(entity: Entity): Future[Unit] = {
    val promise = Promise[Unit]()
    val entityNode = toEntityNode(entity)
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
