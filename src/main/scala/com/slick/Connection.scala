package com.slick

import slick.jdbc.PostgresProfile.api._

// scala object is a singleton instance
object Connection {
  val db = Database.forConfig("postgres")
}
