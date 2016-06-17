package model

import org.mtrupkin.cell.{Cell, CellMap}
import org.mtrupkin.console.{ConsoleChar, RGB}
import org.mtrupkin.math.{Matrix, Point, Size}
import org.mtrupkin.random.Perlin
import random.{NoiseBuilder, Voronoi}

/**
  * Created by mtrupkin on 3/22/2016.
  */
class World extends CellMap {
  val size = Size(40, 20)
  val cells = new Matrix[Cell](size)

  def apply(p: Point): Option[Cell] = if(size.in(p)) Some(cells(p)) else None
  def update(p: Point, c: Cell): Unit = { cells(p) = c }
}

object WorldBuilder {
  def apply(): World = {
    val world = new World
    world.size.foreach(p => {
      world(p) = new Cell {
        def move: Boolean = ???
        val char: ConsoleChar = {
          val width: Double = world.size.width
          val height: Double = world.size.height
          val x = p.x * 10.0 //(p.x / width) * 1
          val y = p.y * 10.0  //(p.y / height) * 1
          val noise = {
            import NoiseBuilder._
            val frequency = 5.0 / (width * 10)
            val persistence = 0.16
            val f = (x: Double, y: Double) => Math.pow(Voronoi.noise(x, y), 4.0)
            val octaved = octaves(persistence, modulate(frequency, f))
            octaved(x, y)
          }
//          println(s"$noise")
          val rgb = if (noise <= 1.0) {
            val color = (noise * 255).toInt
            RGB(color, color, color)
          } else RGB.Green

          ConsoleChar(' ', RGB.Black, rgb)
        }
      }
    })
    world
  }
}
