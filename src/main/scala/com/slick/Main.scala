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

  def insertMovie(): Unit = {
    val query = SlickTables.movieTable += Amelie

    // A Future represents a value which may or may not currently be available,
    // but will be available at some point, or an exception if that value could not be made available.
    // val executor = Executors.newFixedThreadPool(4)
    // implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executor)
    val futureId: Future[Int] = Connection.db.run(query)

    futureId.onComplete {
      case Success(newMovieId) => println(s"Query is successful ,new id is $newMovieId")
      case Failure(exception) => println(s"Query failed, reason: $exception")
    }
    Thread.sleep(1000)
  }
  def readAllMovie(): Unit = {
    val query = SlickTables.movieTable.result
    val futureResult: Future[Seq[Movie]] = Connection.db.run(query)

    futureResult.onComplete {
      case Success(movieList) => println(s"List of Movies: $movieList")
      case Failure(exception) => println(s"Query failed, reason: $exception")
    }

    Thread.sleep(1000)
  }

  def readSomeMovie(): Unit = {
    val query = SlickTables.movieTable.filter(_.id === 1L).update(Amelie.copy(name = "Not interested"))
    val futureInt: Future[Int] = Connection.db.run(query)

    futureInt.onComplete {
      case Success(newMovieId) => println(s"Query is successful ,new id is $newMovieId")
      case Failure(exception) => println(s"Query failed, reason: $exception")
    }

    Thread.sleep(1000)
  }

  def deleteMovie(): Unit = {
    val query = SlickTables.movieTable.filter(_.name.like("%Not%")).delete
    val futureInt: Future[Int] = Connection.db.run(query)

    Thread.sleep(1000)
  }

  def main(args: Array[String]):Unit = {
//    insertMovie()
//    readAllMovie()
//    readSomeMovie()
    deleteMovie()
  }
}



