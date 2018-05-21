package lectures.concurrent

import java.net.URL
import java.util.concurrent.atomic.{AtomicInteger, AtomicLong}
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.{CopyOnWriteArrayList, _}

import org.jsoup.Jsoup

import scala.annotation.tailrec
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
  private val watchedURLs = new ConcurrentHashMap[String, Long]()
  private val concurrentQueueURLs = new ConcurrentLinkedQueue[String]()
  val totalWords = new AtomicLong(0)
  private val unVisitedPages = new AtomicInteger(WebPagesLimit)

  def crawl(): Long = {
    // Start your implementation from here
    val lock = new ReentrantLock()
    def oneThreadJob(): Unit = {
      val url = getUrlFromList()
      crawlOnePage(url)
    }
    @tailrec
    def getUrlFromList(): String = {
      var url = ""
      if (concurrentQueueURLs.size() != 0) {
        lock.lock()
        if (concurrentQueueURLs.size() != 0 ) {
          url = concurrentQueueURLs.poll()
        }
        lock.unlock()
      } else {
        Thread.sleep(12)
      }
      if (url == "") getUrlFromList() else url
    }

    def crawlOnePage(link: String): Unit = {
      if (watchedURLs.putIfAbsent(link, 0).eq(null)) {
        throw new Exception("watch url second time")
      }
      val response = Jsoup.connect(link).ignoreContentType(true)
        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1").execute()

      val contentType: String = response.contentType
      if (contentType.startsWith("text/html")) {
        val doc = response.parse()
        val page = doc.toString
        val onThisPage = countWords(page)
        totalWords.getAndAdd(onThisPage)
        if (concurrentQueueURLs.size() < 1000) {
          val links = doc.getElementsByTag("a")
            .asScala
            .map(e => e.attr("href"))
            .filter(s => s.startsWith("/wiki/"))
            .map(s => URLFirstPart + s.drop(6))
            .filterNot(link => watchedURLs.contains(link))
            .toList
          concurrentQueueURLs.addAll(links.asJava)
        }
      }
      unVisitedPages.getAndDecrement()
    }

    concurrentQueueURLs.add(InitialPage)
    val executor = Executors.newFixedThreadPool(numberOfThreads)

    def getTasks: Seq[Runnable] =
    (1 to WebPagesLimit).map { _ =>
      new Runnable {
        override def run(): Unit = {
          oneThreadJob()
        }
      }
    }

    getTasks.foreach(executor.submit)
    executor.shutdown()
    while (!executor.isTerminated) {
      println(s"- progress: ${unVisitedPages.get()}")
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
