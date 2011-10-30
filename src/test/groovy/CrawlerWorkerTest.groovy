import org.gmock.WithGMock
import spock.lang.Specification

@WithGMock
public class CrawlerWorkerTest extends Specification {

    def 'crawler hits correct Url'() {

        given:
            PageFetcher fetcher = mock(PageFetcher)
            fetcher.getHtml("http://google.com")
            CrawlerWorker worker = new CrawlerWorker(fetcher: fetcher)

        when:
            
            play {
                worker.crawlSite('google.com')
            }

        then:
            true

    }
}
