package model

import org.mtrupkin.random.Dice

/**
  * Created by mtrupkin on 12/19/2014.
  */
object Combat {
  def attack(rating: Int, accuracy: Int): Int = {
    val rolls = (1 to rating).map(_ => Dice(6)).filter(_ > 1).map(_ + accuracy)

    val damages = rolls.map {
      case 1 => 0
      case x if (x <= 5) => 1
      case x if (x >= 6) => 2
    }

    damages.sum
  }
}