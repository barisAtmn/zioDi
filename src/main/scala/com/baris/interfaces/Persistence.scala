package com.baris.interfaces

import com.baris.User
import zio.Task

object Persistence {
  trait Service[A] {
    def get(id: Int): Task[A]
    def create(user: User): Task[A]
    def delete(id: Int): Task[Boolean]
    def createDB: Task[Boolean]
  }
}
