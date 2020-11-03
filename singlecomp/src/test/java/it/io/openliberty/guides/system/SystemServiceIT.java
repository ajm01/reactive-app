// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
package it.io.openliberty.guides.system;

import static org.junit.Assert.assertNotNull;

import java.time.Duration;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Test;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import org.microshed.testing.kafka.KafkaConsumerClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.Arrays;

import io.openliberty.guides.models.SystemLoad;
import io.openliberty.guides.models.SystemLoad.SystemLoadDeserializer;

//@MicroShedTest
//@SharedContainerConfig(AppContainerConfig.class)
public class SystemServiceIT {

    //@KafkaConsumerClient(valueDeserializer = SystemLoadDeserializer.class,
    //        groupId = "system-load-status",
    //        topics = "systemLoadTopic",
    //        properties = ConsumerConfig.AUTO_OFFSET_RESET_CONFIG + "=earliest")
    //public static KafkaConsumer<String, SystemLoad> consumer;

    @Test
    public void testCpuStatus() {
       Properties consumerConfig = new Properties();
       consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-cluster-kafka-bootstrap.reactive.svc:9092");
       consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, "system-load-status");
       consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.openliberty.guides.models.SystemLoad$SystemLoadDeserializer");
       consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
	   
       KafkaConsumer<String, SystemLoad> consumer = new KafkaConsumer<>(consumerConfig);
	   consumer.subscribe(Arrays.asList("systemLoadTopic"));
	   
        ConsumerRecords<String, SystemLoad> records =
                consumer.poll(Duration.ofMillis(30 * 1000));
        System.out.println("Polled " + records.count() + " records from Kafka:");

        for (ConsumerRecord<String, SystemLoad> record : records) {
            SystemLoad sl = record.value();
            System.out.println(sl);
            assertNotNull(sl.hostname);
            assertNotNull(sl.loadAverage);
        }
        consumer.commitAsync();
    }
}