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
    @Shared Website google = new Website('google.com')

    def setup() {
        fetcher = mock(PageFetcher)
        queue = mock(UrlQueue)
        bugsList = mock(BugsList)
        writer = mock(ResultsWriter)

        worker = new CrawlerWorker(queue, writer, fetcher, bugsList)
    }

    def 'crawler returns url to the queue when unable to connect and failCount < 5'() {

        given:
            fetcher.getHtml("http://www.google.com").raises(new IOException())
            fetcher.getHtml("http://google.com").raises(new IOException())

            queue.returnFailedSiteToQueue(google)

        when:
            play {
                worker.crawlSite(google)
            }

        then:
            true
    }

    def 'crawler registers failure when unable to connect after 5 attempts'() {

        given:
            fetcher.getHtml("http://www.google.com").raises(new IOException())
            fetcher.getHtml("http://google.com").raises(new IOException())

            writer.registerFailedCrawl(google)

        when:
            play {
                google.failCount = 4
                worker.crawlSite(google)
            }

        then:
            true
    }

    def 'crawler registers success but with empty bugs list for site with no relevant tags in markup'() {

        given:
            fetcher.getHtml("http://www.google.com").returns(Jsoup.parse('<html><p class="nothing"></p></html>'))

            writer.registerSuccessfulCrawl(google, [])

        when:
            play {
                worker.crawlSite(google)
            }

        then:
            true

    }

    def 'crawler registers success but with empty bugs list for site with relevant tags but no bugs'() {

        given:
            fetcher.getHtml("http://www.google.com").returns(Jsoup.parse('<html><img src="http://nothing"/></html>'))

            writer.registerSuccessfulCrawl(google, [])

            bugsList.hasMatchingPatterns("http://nothing").returns(false)

        when:
            play {
                worker.crawlSite(google)
            }

        then:
            true

    }

    @Unroll('crawler registers success but with single bug for site with 1 bug in #tagType tag')
    def 'crawler registers success but with single bug for site with 1 bug in tag'() {

        given:
            fetcher.getHtml("http://www.google.com").returns(Jsoup.parse('<html><' + tagType + ' src="http://nothing"/></html>'))

            writer.registerSuccessfulCrawl(google, ['someTrackingSoftware'])

            bugsList.hasMatchingPatterns("http://nothing").returns(true)
            bugsList.getBugNameForString("http://nothing").returns('someTrackingSoftware')

        when:
            play {
                worker.crawlSite(google)
            }

        then:
            true

        where:
            tagType   << ['img', 'script' , 'iframe' ]

    }

    def 'crawler registers multiple bugs'() {

        given:
            fetcher.getHtml("http://www.google.com").returns(Jsoup.parse(html))

            writer.registerSuccessfulCrawl(google, expectedBugsList)

            expectedBugsList.each { String bug ->
                bugsList.hasMatchingPatterns("http://${bug}.js").returns(true)
                bugsList.getBugNameForString("http://${bug}.js").returns(bug)
            }
        
        when:
            play {
                worker.crawlSite(google)
            }

        then:
            true

        where:
            expectedBugsList              | html
            ['something', 'anotherthing'] | '<html><img src="http://something.js"/><iframe src="http://anotherthing.js"/></html>'




    }
}
