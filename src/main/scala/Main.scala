import sttp.client4.curl.internal.CurlApi._
import sttp.client4.curl.internal.CurlCode.CurlCode
import sttp.client4.curl.internal.CurlInfo._
import sttp.client4.curl.internal.CurlOption.{Header => _, _}
import sttp.client4.curl.internal._
import scala.collection.immutable.Seq
import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import scala.scalanative.libc.stdio.{fclose, fopen, FILE}
import scala.scalanative.libc.stdlib._
import scala.scalanative.libc.string._
import scala.scalanative.unsafe
import scala.scalanative.unsafe.{CSize, Ptr, _}
import scala.scalanative.unsigned._
import scala.compiletime.*

object Main {

  transparent inline def check(inline code: CurlCode): Unit =
    if (code != CurlCode.Ok) {
      println(s"curl error: $code in ${codeOf(code)}")
    }

  def main(args: Array[String]): Unit =
    given Zone = Zone.open()
    val curl = CurlApi.init() // curl_easy_init()
    //check(curl.option(Verbose, parameter = true))
    val headers = List("Content-Type: application/json", "Accept: application/json")
      .foldLeft(new CurlList(null)) { case (acc, h) =>
        new CurlList(acc.ptr.append(h))
      }
    check(curl.option(HttpHeader, headers.ptr))
    check(curl.option(Url, "http://example.com"))
    check(curl.option(PostFields, "name=daniel"))
    val x = CurlCode(CCurl.perform(curl))
    println(x)
    curl.cleanup()
}
