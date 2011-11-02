import spock.lang.Specification
import spock.lang.Shared

class ResultsWriterSpec extends Specification {

    @Shared String fileLocation = "temp"
    @Shared Website google = new Website('google')
    @Shared Website yahoo = new Website('yahoo')

    def tearDown() {
        new File(fileLocation).delete()
    }

    def 'results writer correctly formats output file'() {

        given:

            ResultsWriter resultsWriter = new ResultsWriter(fileLocation)

        when:
            results.each { Website site, List results ->
                resultsWriter.registerSuccessfulCrawl(site, results)
            }

        then:
            new File(fileLocation).text == expectedFileContents

        where:
            results                                                                               | expectedFileContents
            [(google): []]                                                         | 'google:[]\n'
            [(google): [], (yahoo): ['coreMetrics']]                | 'google:[]\nyahoo:[coreMetrics]\n'
            [(google): [], (yahoo): ['coreMetrics', 'clickTracks']] | 'google:[]\nyahoo:[coreMetrics,clickTracks]\n'

    }

    def 'results writer writes errors'() {

        given:
            ResultsWriter resultsWriter = new ResultsWriter(fileLocation)

        when:
            resultsWriter.registerFailedCrawl(site)

        then:
            new File(fileLocation).text == expectedFileContents

        where:
            site   | expectedFileContents
            google | 'google:CRAWL FAILED\n'
            yahoo  | 'yahoo:CRAWL FAILED\n'
    }
}
