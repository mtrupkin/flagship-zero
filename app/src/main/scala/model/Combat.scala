package model

import org.mtrupkin.random.Dice

/**
  * Created by mtrupkin on 12/19/2014.
  */
object Combat {
  def attack(rating: Int): Int = {
    val rolls = (1 to rating).map(_ => Dice(6))

    val damages = rolls.map {
      case 1 => 0
      case x if (x >= 2 && x <= 5) => 1
      case 6 => 2
    }

    damages.sum
  }
}