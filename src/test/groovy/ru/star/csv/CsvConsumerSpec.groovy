package ru.star.csv

import ru.star.printer.model.CsvModel
import spock.lang.Specification

import static ru.star.utils.FileUtils.readFromFile

class CsvConsumerSpec extends Specification {

    def createCsvModel() {
        CsvModel.builder()
                .fileId("08_001")
                .articleName("Агностический теизм")
                .url("https://ru.wikipedia.org/wiki/Агностический_теизм")
                .category("/home/axreldable/IdeaProjects/wiki")
                .level(0)
                .articleSize(952)
                .build()
    }

    def "Success consumer work"() {
        setup:
        def file = File.createTempFile("file", ".txt")
        def consumer = new CsvConsumer(new BufferedWriter(new FileWriter(file.getAbsolutePath())))

        CsvQueueHolder.articles.add(createCsvModel())

        when:
        Thread thread = new Thread(consumer)
        thread.start()
        Thread.sleep(1000)
        thread.interrupt()

        then:
        readFromFile(file.getAbsolutePath()) == "08_001,Агностический теизм,https:" +
                "//ru.wikipedia.org/wiki/Агностический_теизм,/home/axreldable/IdeaProjects" +
                "/wiki,0,952" + System.getProperty("line.separator")
    }

    def "Doesn't throw IOException during writing, just log it"() {
        setup:
        CsvQueueHolder.articles.add(createCsvModel())

        Writer writer = Mock(Writer.class)
        writer.write(_ as String) >> { String csvString -> throw new IOException() }
        def consumer = new CsvConsumer(writer)

        when:
        consumer.run()

        then:
        notThrown IOException
    }
}
