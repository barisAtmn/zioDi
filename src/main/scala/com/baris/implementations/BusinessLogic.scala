package com.baris.implementations

import com.baris.Environments.AppEnvironment
import org.apache.kafka.clients.producer.ProducerRecord
import zio.{Chunk, ZIO}
import zio.kafka.consumer.{Consumer, OffsetBatch, Subscription}
import zio.kafka.producer.Producer
import zio.kafka.serde.Serde
import zio._, zio.blocking.Blocking, zio.clock.Clock


object BusinessLogic {

   val run:RIO[AppEnvironment with Clock with Blocking, Unit] =
       Consumer
         .subscribeAnd(Subscription.topics("zio-input--all"))
         .plainStream(Serde.string, Serde.string)
         .map { record =>
           val key   = record.record.key()
           val value = record.record.value()

           val producerRecord: ProducerRecord[String, String] = new ProducerRecord("zio-output-topic", key, value)
           (producerRecord, record.offset)
         }
         .mapChunksM { chunk =>
           val records     = chunk.map(_._1)
           val offsetBatch = OffsetBatch(chunk.map(_._2).toSeq)

           Producer.produceChunk[Any, String, String](records) *> offsetBatch.commit.as(Chunk(()))
         }
         .runDrain
         .provideSomeLayer(KafkaConsumerAndProducer.consumerAndProducerLive ++ Clock.live ++ Blocking.live)





}
