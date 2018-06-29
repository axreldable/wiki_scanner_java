package ru.star.csv

import spock.lang.Specification

class CsvQueueHolderSpec extends Specification {
    def "Fail to create instance of the class"() {
        when: "you try to create instance of utility class"
        (CsvQueueHolder) Class.forName(CsvQueueHolder.getName()).newInstance()

        then:
        thrown UnsupportedOperationException
    }
}
