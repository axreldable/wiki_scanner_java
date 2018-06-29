package ru.star.csv

import ru.star.printer.model.CsvModel
import spock.lang.Specification

import static ru.star.utils.FileUtils.readFromFile

class CsvConsumerSpec extends Specification {

    def "test consumer file writing"() {
        setup:
        def file = File.createTempFile("file", ".txt")
        def consumer = new CsvConsumer(file.getAbsolutePath())

        def model = CsvModel.builder()
                .fileId("08_001")
                .articleName("Агностический теизм")
                .url("https://ru.wikipedia.org/wiki/Агностический_теизм")
                .category("/home/axreldable/IdeaProjects/wiki")
                .level(0)
                .articleSize(952)
                .build()
        CsvQueueHolder.articles.add(model)

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
}
