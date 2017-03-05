import model.Database.IVSeries
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

  "Application" should {

    "send 404 on a bad request" in new WithApplication{
      route(FakeRequest(GET, "/boum")) must beNone
    }

    "render the index page" in new WithApplication{
      val home = route(FakeRequest(GET, "/")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
      contentAsString(home) must contain ("Your new application is ready.")
    }

    "ivseries should equal with other ivseries if parameters are equals" in new WithApplication{
      val ivSeries1 = new IVSeries(3, "Testing series")
      val ivSeries2 = new IVSeries(3, "Testing series")
      val ivSeries3 = new IVSeries(4, "Another testing series")

      assert(ivSeries1 == ivSeries2)
      assert(ivSeries1 != ivSeries3)
    }
  }
}
