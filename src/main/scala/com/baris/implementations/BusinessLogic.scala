package com.baris.implementations

import com.baris.Environments.AppEnvironment
import org.apache.kafka.clients.producer.ProducerRecord
import zio.{Chunk, ZIO}
import zio.kafka.consumer.{Consumer, OffsetBatch, Subscription}
import zio.kafka.producer.Producer
import zio.kafka.serde.Serde
import zio._
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.Console
import com.baris.implementations.ConfigurationService._
import scala.util.{Success}


object BusinessLogic {

   val run:RIO[AppEnvironment with Clock with Blocking with Console, Unit] =
     for {
       config      <- getConfig
       inputTopic  = config.kafka.inputTopic
       outputTopic = config.kafka.outputTopic
       start       <- Consumer
       .subscribeAnd(Subscription.topics(inputTopic))
       .plainStream(Serde.string.asTry, Serde.string.asTry)
       .map { record =>
         val key   = record.record.key()
         val value = record.record.value()

         (key, value) match {
           case (Success(key),Success(value)) => {
             val producerRecord: ProducerRecord[String, String] = new ProducerRecord(outputTopic, key, value)
             (producerRecord, record.offset)
           }
           case _ => {
             val producerRecord: ProducerRecord[String, String] = new ProducerRecord(outputTopic, "", "")
             (producerRecord, record.offset)
           }
         }

       }
         .tap(cr => console.putStrLn(cr._1.value()))
         .mapChunksM { chunk =>
           val records     = chunk.map(_._1).filter(record => record.value() == "")
           val offsetBatch = OffsetBatch(chunk.map(_._2).toSeq)

           Producer.produceChunk[Any, String, String](records) *> offsetBatch.commit.as(Chunk(()))
         }
         .runDrain
         .provideSomeLayer(KafkaConsumerAndProducer.consumerAndProducerLive ++ Clock.live ++ Blocking.live ++ Console.live)
     } yield start






}
