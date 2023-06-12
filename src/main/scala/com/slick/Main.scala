package com.slick

import java.time.LocalDate
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.{ExecutionContext, Future}
import java.util.concurrent.Executors

object PrivateExecutionContext {
  val executor = Executors.newFixedThreadPool(4)
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executor)
}

object Main {
  import slick.jdbc.PostgresProfile.api._
  import PrivateExecutionContext._
  val Amelie = Movie(1L, "Amelie", LocalDate.of(1999,9,9), 999)
  val LordOfTheRings = Movie(2L, "LordOfTheRings", LocalDate.of(1111,1,1), 0)
  val Audrey = Actor(1L, "Audrey Justine Tautou")
  val Mathieu = Actor(2L, "Mathieu Kassovitz")
  val wood = Actor(3L, "Elijah Wood")
  val mapping1 = MovieActorMapping(100L, 1L, 1L)
  val mapping2 = MovieActorMapping(100L, 1L, 2L)
  val mapping3 = MovieActorMapping(100L, 2L, 3L)

  def insertMovie(): Unit = {
    val action = SlickTables.movieTable ++= Seq(Amelie, LordOfTheRings)

    // A Future represents a value which may or may not currently be available,
    // but will be available at some point, or an exception if that value could not be made available.
    // val executor = Executors.newFixedThreadPool(4)
    // implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executor)

    // db.run() method takes an instance of DBIOAction and execute
    // DBIOAction[R,T,E]: R (result type of the query), T (if streaming is supported or not), E (effect type: Read/Write/Transaction/DDL).
    val futureId = Connection.db.run(action)

    futureId.onComplete {
      case Success(newMovieId) => println(s"Query is successful ,new id is $newMovieId")
      case Failure(exception) => println(s"Query failed, reason: $exception")
    }
    Thread.sleep(1000)
  }
  def readAllMovie(): Unit = {
    val action = SlickTables.movieTable.result
    val futureResult: Future[Seq[Movie]] = Connection.db.run(action)

    futureResult.onComplete {
      case Success(movieList) => println(s"List of Movies: $movieList")
      case Failure(exception) => println(s"Query failed, reason: $exception")
    }

    Thread.sleep(1000)
  }

  def readSomeMovie(): Unit = {
    val action = SlickTables.movieTable.filter(_.id === 1L).update(Amelie.copy(name = "Not interested"))
    val futureInt: Future[Int] = Connection.db.run(action)

    futureInt.onComplete {
      case Success(newMovieId) => println(s"Query is successful ,new id is $newMovieId")
      case Failure(exception) => println(s"Query failed, reason: $exception")
    }

    Thread.sleep(1000)
  }

  def deleteMovie(): Unit = {
    val action = SlickTables.movieTable.filter(_.name.like("%Not%")).delete
    val futureInt: Future[Int] = Connection.db.run(action)

    Thread.sleep(1000)
  }
  def mutipleAction(): Unit = {
    val addMovies = SlickTables.movieTable ++= Seq(Amelie, LordOfTheRings)
    val addActor = SlickTables.actorTable ++= Seq(Audrey, Mathieu, wood)
    val relation = SlickTables.movieActorMappingTable ++= Seq(mapping1,  mapping2,  mapping3)
    val action = DBIO.seq(addMovies, addActor, relation)
    Connection.db.run(action.transactionally)

    Thread.sleep(1000)
  }
  def findActorByMovieId(movieId: Long): Future[Seq[Actor]] = {
    val query = SlickTables.movieActorMappingTable
      .filter(_.movieId === movieId)
      .join(SlickTables.actorTable)
      .on(_.actorId  === _.id)
      .map(_._2) //_: join result table,  _._2: columns in actorTable, as the actor object
    // map: select specific fields (columns)

    Connection.db.run(query.result)
  }



  def main(args: Array[String]):Unit = {
    mutipleAction()

    findActorByMovieId(1L).onComplete {
      case Success(actors) => println(s"Actors: $actors")
      case Failure(exception) => println(s"Query failed, reason: $exception")
    }
    Thread.sleep(5000)
    PrivateExecutionContext.executor.shutdown()
  }
}































