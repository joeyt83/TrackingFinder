import org.gmock.WithGMock
import spock.lang.Specification
import org.jsoup.Jsoup
import spock.lang.Unroll
import spock.lang.Shared

@WithGMock
public class CrawlerWorkerSpec extends Specification {

    @Shared CrawlerWorker worker
    @Shared PageFetcher fetcher
    @Shared UrlQueue queue
    @Shared BugsList bugsList
    @Shared ResultsWriter writer

    def setup() {
        fetcher = mock(PageFetcher)
        queue = mock(UrlQueue)
        bugsList = mock(BugsList)
        writer = mock(ResultsWriter)

        worker = new CrawlerWorker(fetcher: fetcher, queue: queue, bugsList: bugsList, resultsWriter: writer)
    }

    def 'crawler registers failure when unable to connect'() {

        given:
            fetcher.getHtml("http://google.com").raises(new IOException())

            queue.returnUrl('google.com')

        when:
            play {
                worker.crawlSite('google.com')
            }

        then:
            true
    }

    def 'crawler registers success but with empty bugs list for site with no relevant tags in markup'() {

        given:
            fetcher.getHtml("http://google.com").returns(Jsoup.parse('<html><p class="nothing"></p></html>'))

            writer.registerSuccessfulCrawl('google.com', [])

        when:
            play {
                worker.crawlSite('google.com')
            }

        then:
            true

    }

    def 'crawler registers success but with empty bugs list for site with relevant tags but no bugs'() {

        given:
            fetcher.getHtml("http://google.com").returns(Jsoup.parse('<html><img src="nothing"/></html>'))

            writer.registerSuccessfulCrawl('google.com', [])

            bugsList.hasMatchingPatterns("nothing").returns(false)

        when:
            play {
                worker.crawlSite('google.com')
            }

        then:
            true

    }

    @Unroll('crawler registers success but with single bug for site with 1 bug in #tagType tag')
    def 'crawler registers success but with single bug for site with 1 bug in tag'() {

        given:
            fetcher.getHtml("http://google.com").returns(Jsoup.parse('<html><' + tagType + ' src="nothing"/></html>'))

            writer.registerSuccessfulCrawl('google.com', ['someTrackingSoftware'])

            bugsList.hasMatchingPatterns("nothing").returns(true)
            bugsList.getBugNameForString("nothing").returns('someTrackingSoftware')

        when:
            play {
                worker.crawlSite('google.com')
            }

        then:
            true

        where:
            tagType   << ['img', 'script' , 'iframe' ]

    }

    def 'crawler registers multiple bugs'() {

        given:
            fetcher.getHtml("http://google.com").returns(Jsoup.parse(html))

            writer.registerSuccessfulCrawl('google.com', expectedBugsList)

            expectedBugsList.each { String bug ->
                bugsList.hasMatchingPatterns("${bug}.js").returns(true)
                bugsList.getBugNameForString("${bug}.js").returns(bug)
            }
        
        when:
            play {
                worker.crawlSite('google.com')
            }

        then:
            true

        where:
            expectedBugsList              | html
            ['something', 'anotherthing'] | '<html><img src="something.js"/><iframe src="anotherthing.js"/></html>'




    }
}
