import spock.lang.Specification

class UrlQueueSpec extends Specification {

    def 'queue reads from file and returns null when empty'() {

        given:
            UrlQueue queue = new UrlQueue(location)

        when:
            expectedNumberOfUrls.times {
                assert queue.getNextUrl() != null
            }

        then:
            queue.getNextUrl() == null

        where:
            location                             | expectedNumberOfUrls
            'src/test/resources/ten-urls.txt'    | 10
            'src/test/resources/twenty-urls.txt' | 20
    }

    def 'when there are failed urls, they are re-served from the queue'() {

        given:
            UrlQueue queue = new UrlQueue(location)

        when:
            expectedNumberOfUrls.times {
                queue.returnUrl(queue.getNextUrl())
            }

            expectedNumberOfUrls.times {
                assert queue.getNextUrl() != null
            }

        then:
            queue.getNextUrl() == null

        where:
            location                             | expectedNumberOfUrls
            'src/test/resources/ten-urls.txt'    | 10
            'src/test/resources/twenty-urls.txt' | 20
    }

}
