package org.mainapp.config.kafka;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.mainapp.data.Match;
import org.mainapp.data.MatchEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

import org.springframework.kafka.support.serializer.JacksonJsonSerde;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {

    @Bean
    public KStream<Long, MatchEvent> kStream(StreamsBuilder streamsBuilder) {

        JsonSerializer<MatchEvent> jsonSerializer = new JsonSerializer<>();
        JsonDeserializer<MatchEvent> jsonDeserializer = new JsonDeserializer<>(MatchEvent.class);

        Serde<Long> keySerde = Serdes.Long();
        Serde<MatchEvent> valueSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);


        Serde<List<MatchEvent>> listSerde = new Serdes.ListSerde<>(ArrayList.class, valueSerde);


        KStream<Long, MatchEvent> stream = streamsBuilder.stream("match-events",
                Consumed.with(keySerde, valueSerde));


        stream.groupByKey(Grouped.with(keySerde, valueSerde))
                .aggregate(
                        ArrayList::new,
                        (id, event, list) -> {
                            list.add(event);
                            return list;
                        },
                        Materialized.<Long, List<MatchEvent>, KeyValueStore<Bytes, byte[]>>as("match-history-store")
                                .withKeySerde(keySerde)
                                .withValueSerde(listSerde)
                );

        return stream;
    }
}
