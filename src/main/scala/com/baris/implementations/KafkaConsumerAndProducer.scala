package com.baris.implementations

import com.baris.ConfigurationModule
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import zio.{Has, ZIO, ZLayer, ZManaged}
import zio.blocking.Blocking
import zio.clock.Clock
import zio.kafka.consumer.Consumer.AutoOffsetStrategy
import zio.kafka.consumer.{Consumer, ConsumerSettings, Offset, Subscription}
import zio.kafka.producer.{Producer, ProducerSettings}
import zio.kafka.serde.Serde

import scala.util.{Failure, Success, Try}

object KafkaConsumerAndProducer {

  val consumerManaged: ZManaged[ConfigurationModule with Clock with Blocking, Throwable, Consumer.Service] = for {
    conf <- ConfigurationService.load.toManaged_
    consumerSettings = ConsumerSettings(List(conf.kafka.bootstrapServers)).withGroupId("zio").withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    consumerService <- Consumer.make(consumerSettings)
  } yield consumerService

  val producerManaged= for {
    conf <- ConfigurationService.load.toManaged_
    producerSettings = ProducerSettings(List(conf.kafka.bootstrapServers))
    producerService  <- Producer.make(producerSettings,Serde.string, Serde.string)
  } yield producerService

  val consumer: ZLayer[ConfigurationModule with Clock with Blocking, Throwable, Consumer] =
    ZLayer.fromManaged(consumerManaged)

  val producer =
    ZLayer.fromManaged(producerManaged)

  val consumerAndProducerLive = consumer ++ producer

}
