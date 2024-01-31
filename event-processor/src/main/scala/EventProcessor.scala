
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.scala._
import io.circe.parser.decode
import io.circe.generic.auto._
import org.apache.flink.api.common.eventtime.{SerializableTimestampAssigner, WatermarkStrategy}
import org.apache.flink.api.common.serialization.SimpleStringEncoder
import org.apache.flink.core.fs.Path
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.util.Collector
import java.time.{Duration, LocalDateTime}
import java.time.format.DateTimeFormatter
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink
import io.circe.syntax._

object EventProcessor extends App {

        case class Event(userId: String, postcode: String, webpage: String, timestamp: Long )
        case class PageView(postCode: String, dataTimeWindow: String, noOfPageViews: Long)

        val env = StreamExecutionEnvironment.getExecutionEnvironment

        val kafkaBootstrapServers = "kafka:29092"
        val kafkaTopic = "events"

        // Flink Kafka Consumer
        val kafkaConsumerProperties = new java.util.Properties()
        kafkaConsumerProperties.setProperty("bootstrap.servers", kafkaBootstrapServers)
        val kafkaConsumer = new FlinkKafkaConsumer[Event](kafkaTopic, new EventDeserializer(), kafkaConsumerProperties)

        val outputPath = new Path("/opt/flink/log")

        val fileSink: StreamingFileSink[String] = StreamingFileSink
          .forRowFormat(outputPath, new SimpleStringEncoder[String]("UTF-8"))
          .withRollingPolicy(
                  DefaultRollingPolicy.builder()
                    .withRolloverInterval(Time.minutes(1).toMilliseconds)
                    .withInactivityInterval(Time.minutes(1).toMilliseconds/2)
                    .build()
          ).build()

        val kafkaDataStream: DataStream[Event] = env.addSource(kafkaConsumer)

        val withTimestampsAndWatermarks: DataStream[Event]
                        = kafkaDataStream.assignTimestampsAndWatermarks(WatermarkStrategy
                          .forBoundedOutOfOrderness[Event](Duration.ofSeconds(20))
                          .withTimestampAssigner(new SerializableTimestampAssigner[(Event)] {
                                  override def extractTimestamp(element: (Event), recordTimestamp: Long):
                                  Long = element.timestamp
                          }))

        val aggregatedStream: DataStream[PageView] = withTimestampsAndWatermarks
                                                        .keyBy(_.postcode)
                                                        .window(TumblingEventTimeWindows.of(Time.minutes(1)))
                                                        .process(new PageViewAggregationFunction())

        val jsonOutputStream: DataStream[String] = aggregatedStream.map(pv => pv.asJson.noSpaces)
        jsonOutputStream.print()

        jsonOutputStream.addSink(fileSink).setParallelism(1)

        env.execute("Flink Event Processor")

        class EventDeserializer extends org.apache.flink.api.common.serialization.DeserializationSchema[Event]
        {
                override def deserialize(message: Array[Byte]): Event = {
                        val jsonString = new String(message, "UTF-8")
                        decode[Event](jsonString) match {
                                case Right(data) => data
                                case Left(error) =>
                                        throw new RuntimeException(s"Error deserializing JSON: $error")
                        }
                }

                override def isEndOfStream(nextElement: Event): Boolean = false
                override def getProducedType: TypeInformation[Event] = TypeInformation.of(classOf[Event])
        }


        class PageViewAggregationFunction extends ProcessWindowFunction[Event, PageView, String, TimeWindow] {
                override def process(key: String, context: Context, input: Iterable[Event], out: Collector[PageView]): Unit = {
                        val postcode = key
                        val count = input.size
                        val windowStart = context.window.getStart

                        // Convert windowStart to a human-readable date-time format
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        val windowStartDateTime = LocalDateTime.ofEpochSecond(windowStart / 1000, 0, java.time.OffsetDateTime.now.getOffset)
                        val formattedWindowStart = windowStartDateTime.format(formatter)

                        out.collect(PageView(postcode, formattedWindowStart, count.toLong))
                }
        }


}