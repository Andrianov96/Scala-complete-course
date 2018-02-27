package lectures.concurrent

import java.net.URL
import java.util.concurrent.atomic.{AtomicInteger, AtomicLong}
import java.util.concurrent.locks.ReentrantLock

import org.jsoup.Jsoup
import java.util.concurrent.{CopyOnWriteArrayList, _}

import scala.collection.JavaConverters._

/**
  * В данном задании необходимо реализовать поискового робота, в задачу которого входит
  * подсчитать количество русских слов в английской Википедии.
  *
  * Начинать надо с заглавной страницы и идти по ссылкам с нее далее вглубь.
  * Чтобы не выкачивать всю википедию, предлагается ограничиться 1000 страниц.
  * Считать надо только уникальные страницы, скачивать дубли нельзя!
  * За пределы домена en.wikipedia.org выходить не надо.
  *
  * Необходимо реализовать как однопоточный, так и многопоточный вариант (с заданным уровнем параллельности).
  * Использовать можно только родные Java-конструкции для работы с многопоточностью:
  * Thread, synchronized, concurrent-коллекции, примитивы работы с потоками.
  *
  */
class WebCrawler(numberOfThreads: Int) {

  private val WebPagesLimit = 1000
  private val InitialPage = "https://en.wikipedia.org/wiki/Main_Page"
  private val URLFirstPart = "https://en.wikipedia.org/wiki/"
  private val Word = """\b+([А-Яа-яЁё]+)\b+""".r
  private val wastedURLs = new CopyOnWriteArraySet[URL]()
  private val pagesList = new CopyOnWriteArrayList[URL]()
  var totalWords = new AtomicLong(0)
  private val visitedPages = new AtomicInteger(WebPagesLimit)

  def crawl(): Long = {
    // Start your implementation from here

    def oneThreadJob(): Unit = {
      val lock = new ReentrantLock()
      lock.lock()
      val url = pagesList.get(0)
      pagesList.remove(0)
      lock.unlock()
      crawlOnePage(url)
    }

    def crawlOnePage(url: URL): Unit = {
      wastedURLs.add(url)
      val link: String = url.toString
      val response = Jsoup.connect(link).ignoreContentType(true)
        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1").execute()

      val contentType: String = response.contentType
      if (contentType.startsWith("text/html")) {
        val doc = response.parse()
        val page = doc.toString
        val onThisPage = countWords(page)
        totalWords.getAndAdd(onThisPage)
        if (pagesList.size() < 1000) {
          val links = doc.getElementsByTag("a")
            .asScala
            .map(e => e.attr("href"))
            .filter(s => s.startsWith("/wiki/"))
            .map(s => URLFirstPart + s.drop(6)).map(link => new URL(link))
            .filterNot(link => wastedURLs.contains(link))
            .toList
          pagesList.addAll(links.asJava)
        }
      }
      visitedPages.getAndDecrement()
    }

    pagesList.add(new URL(InitialPage))
    val executor = Executors.newFixedThreadPool(numberOfThreads)

    def getTasks: Seq[Runnable] =
    (1 to WebPagesLimit).map { _ =>
      new Runnable {
        override def run(): Unit = {
          while (pagesList.size() == 0){
            Thread.sleep(11)
          }
          oneThreadJob()
        }
      }
    }

    getTasks.foreach(executor.submit)
    executor.shutdown()
    while (!executor.isTerminated) {
      println(s"- progress: ${visitedPages.get()}")
      Thread.sleep(1000)
    }

    totalWords.get()
  }

  private def countWords(page: String): Long = {
    Word.findAllIn(page).size
  }

}

object WebCrawler extends App {

  val startTime = System.currentTimeMillis()
  val result = new WebCrawler(1).crawl()
  val elapsedTime = System.currentTimeMillis() - startTime
  println(s"SingleCrawler took $elapsedTime ms, got $result words")

  val fourfoldCrawler = new WebCrawler(4)
  val result4 = new WebCrawler(4).crawl()
  val elapsedTime4 = System.currentTimeMillis() - startTime - elapsedTime
  println(s"FourfoldCrawler took $elapsedTime4 ms, got $result4 words")

}
