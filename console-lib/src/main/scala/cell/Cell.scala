package org.mtrupkin.cell

import org.mtrupkin.math.Point

/**
  * Created by mtrupkin on 3/23/2016.
  */
trait Cell {
  def move: Boolean
}

trait CellMap {
  def apply(p: Point): Option[Cell]
}