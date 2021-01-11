package sap 

import io.gatling.core.Predef._ 
import io.gatling.http.Predef._
import scala.concurrent.duration._
import io.gatling.core.feeder._
import io.gatling.core.session._
import io.gatling.core.controller.throttle._
import scala.io.Source

class Determination_1666 extends Simulation { 

  val records = jsonFile("determination_1666.json").readRecords

  val DurationMinutes = records(0).get("Duration").map(_.toString).getOrElse("0").toInt
  val NumberOfUsers = records(0).get("Users").map(_.toString).getOrElse("0").toInt
  val UsersRampUpDuration = records(0).get("UsersRampUpDuration").map(_.toString).getOrElse("0").toInt
  val Version = records(0).get("Version").map(_.toString).getOrElse("")   
  val EndpointDet = records(0).get("Endpoint").map(_.toString).getOrElse("")
  val BaseUrl = records(0).get("URL").map(_.toString).getOrElse("")
  val Token = records(0).get("Token").map(_.toString).getOrElse("")
  var Rps = records(0).get("Req/m").map(_.toString).getOrElse("0").toInt
  var ReqRampUpDuration = records(0).get("ReqRampUpDuration").map(_.toString).getOrElse("0").toInt

  println ("Parameters:\n------------------")
  printf ("Duration %d minutes:\n", DurationMinutes)
  printf ("Users: %d\n", NumberOfUsers)
  printf ("Users Ramp Up Duration minutes: %d\n", UsersRampUpDuration)
  printf ("Version: %s\n", Version)  
  printf ("Endpoint V0: %s\n", EndpointDet)
  printf ("URL: %s\n", BaseUrl)  
  printf ("Token: %s\n", Token)
  printf ("Req/m: %d\n", Rps)
  printf ("Req/m Ramp Up Duration minutes: %d\n", ReqRampUpDuration)

  if (Rps > 0) {
    Rps = Rps/60
  }

  var RequestsPath = "resources/v1/"
  var JsonFieldToCheckSuccess = "$.items"
  var items = ""   

  val request = Source.fromFile(RequestsPath + "request.json").getLines.mkString

  println(request)

  val httpProtocol = http 
    .baseUrl(BaseUrl)
    .header("Content-Type", "application/json")
    .acceptHeader("application/json") 


  val warm_up= scenario("Warm up") 
    .during(30 seconds) {
      exec(http("warm_up") 
      .post(EndpointDet)
      .body(StringBody(request))) 
  }
  val scn0 = scenario("Quote to SAP Tax calculator engine - 0") 
    .during(DurationMinutes minutes) {
      exec(http("exec_0") 
      .post(EndpointDet)
      .body(StringBody(request))) 
  }
  val scn1 = scenario("Quote to SAP Tax calculator engine - 1") 
    .during(DurationMinutes minutes) {
      exec(http("exec_1") 
      .post(EndpointDet)
      .body(StringBody(request))) 
  }
  val scn2 = scenario("Quote to SAP Tax calculator engine - 2") 
    .during(DurationMinutes minutes) {
      exec(http("exec_2") 
      .post(EndpointDet)
      .body(StringBody(request))) 
  }

  val scn3 = scenario("50000 Req/Min Quote to SAP Tax calculator engine")
    .during(DurationMinutes minutes) {
      exec(http("exec_rpm")
        .post(EndpointDet)
        .body(StringBody(request)))
    }



  val scn = scenario("call to determination service")
    .during(DurationMinutes minutes) {
      exec(http("exec_rpm")
        .post(EndpointDet)
        .body(StringBody(request)))
    }

    val rpmScenarios = warm_up.pause(30 seconds).exec(scn)

   setUp(
    rpmScenarios
      .inject(
        rampUsers(NumberOfUsers) during (UsersRampUpDuration minutes))
      .throttle(
        reachRps(Rps) in (ReqRampUpDuration minutes),
        holdFor((DurationMinutes - ReqRampUpDuration) minutes))
  ).protocols(httpProtocol)

}