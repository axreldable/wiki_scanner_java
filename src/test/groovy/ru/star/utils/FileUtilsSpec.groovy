package ru.star.utils

import spock.lang.Specification
import spock.lang.Unroll

import static ru.star.utils.FileUtils.readFromFile
import static ru.star.utils.FileUtils.saveToFile

class FileUtilsSpec extends Specification {

    @Unroll
    def "Write '#input' and read '#output' from file"() {
        setup:
        def file = File.createTempFile("file", ".txt")

        when:
        saveToFile(input, file.getAbsolutePath())

        then:
        output == readFromFile(file.getAbsolutePath())

        where:
        input             || output
        "123"             || "123"
        "text"            || "text"
        "text\nnext_line" || "text\nnext_line"
    }

    def "Read from nonexistent file"() {
        when:
        def output = readFromFile("nonexistent.txt")

        then:
        output == null
    }

    @Unroll
    def "When I write '#firstIn' and then '#secondIn' to the file the '#result' will be there"() {
        setup:
        def file = File.createTempFile("file", ".txt")

        when:
        saveToFile(firstIn, file.getAbsolutePath())
        saveToFile(secondIn, file.getAbsolutePath())

        then:
        result == readFromFile(file.getAbsolutePath())

        where:
        firstIn || secondIn || result
        "123"   || "321"    || "321"
        "text1" || "text2"  || "text2"
    }

    def "Nothing happens when you write to nonexistent file"() {
        when: "you try to save in nonexistent file"
        saveToFile("text", "*/*")

        then:
        noExceptionThrown()
    }

    def "Fail to create instance of the class"() {
        when: "you try to create instance of utility class"
        (FileUtils) Class.forName(FileUtils.getName()).newInstance()

        then:
        thrown UnsupportedOperationException
    }

    def "Couldn't create existing dirs and return false"() {
        setup:
        def dir = File.createTempDir()

        when:
        def isCreate = FileUtils.createDirs(dir.getAbsolutePath())

        then:
        !isCreate
    }

    def "Create dirs"() {
        when:
        def isCreate = FileUtils.createDirs("temp/someDir")

        then:
        isCreate

        cleanup:
        new File("temp/someDir").delete()
        new File("temp").delete()
    }
}
