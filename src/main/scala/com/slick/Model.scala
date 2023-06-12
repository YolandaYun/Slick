package com.slick

import java.time.LocalDate

final case class Movie(id: Long,name: String,releaseDate: LocalDate,lengthInMin: Int)
final case class Actor(id: Long, name: String)
final case class MovieActorMapping(id: Long, movieId: Long, actorId: Long)

object SlickTables {
  import slick.jdbc.PostgresProfile.api._

  // Some("movies"): schema name
  class MovieTable(tag: Tag) extends Table[Movie](tag, Some("movies"),"Movie") {
    // slick column object
    def id = column[Long]("movie_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def releaseDate = column[LocalDate]("release_date")
    def lengthInMin = column[Int]("length_in_min")
    // * methods: "projection" pack from database columns to scala object, unpack scala object to database column
    override def * = (id, name, releaseDate, lengthInMin) <> (Movie.tupled, Movie.unapply)
  }
  val movieTable = TableQuery[MovieTable]

  class ActorTable(tag: Tag) extends Table[Actor](tag, Some("movies"), "Actor") {
    def id = column[Long]("actor_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    override def * = (id, name) <> (Actor.tupled, Actor.unapply)
  }

  val actorTable = TableQuery[ActorTable]

  class MovieActorMappingTable(tag: Tag) extends Table[MovieActorMapping](tag, Some("movies"), "MovieActorMapping") {
    def id = column[Long]("movie_actor_id", O.PrimaryKey, O.AutoInc)
    def movieId = column[Long]("movie_id")
    def actorId = column[Long]("actor_id")

    override def * = (id, movieId, actorId) <> (MovieActorMapping.tupled, MovieActorMapping.unapply)
  }

  val movieActorMappingTable = TableQuery[MovieActorMappingTable]

}
