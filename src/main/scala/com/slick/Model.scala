package com.slick

import java.time.LocalDate

case class Movie(id: Long,name: String,releaseDate: LocalDate,lengthInMin: Int)

object SlickTables {
  import slick.jdbc.PostgresProfile.api._

  class MovieTable(tag: Tag) extends Table[Movie](tag, Some("movies"),"Movie" /*schema name*/) {
    // slick column object
    def id = column[Long]("movie_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def releaseDate = column[LocalDate]("release_date")
    def lengthInMin = column[Int]("length_in_min")
    // * methods: "projection" pack from database columns to scala object, unpack scala object to database column
    def * = (id, name, releaseDate, lengthInMin) <> (Movie.tupled, Movie.unapply)
  }
  val movieTable = TableQuery[MovieTable]
}
