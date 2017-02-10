package models

import org.scalatestplus.play._

class AbstractCaitCsvRepresentationSpec extends PlaySpec {

  "An AbstractCaitCsvRepresentation" must {

    "safely extract a double from an Array[String] with a default" in {

      AbstractCaitCsvRepresentation.safeDouble(Array("2.1"), 0) mustBe 2.1
      AbstractCaitCsvRepresentation.safeDouble(Array("string"), 0) mustBe 0.0
      AbstractCaitCsvRepresentation.safeDouble(Array("string"), 0, -1.0) mustBe -1.0
      AbstractCaitCsvRepresentation.safeDouble(Array("2.1", "string"), 1, -1.0) mustBe -1.0
      AbstractCaitCsvRepresentation.safeDouble(Array("2.1", "string"), 0, -1.0) mustBe 2.1

    }

    "replace commas within quotes with pipes (used for csv line) " in {

      val replaceFunc = AbstractCaitCsvRepresentation.replaceQuotedCommas

      replaceFunc("""blah,"blah,blah",blah""") mustBe "blah,blah|blah,blah"
      replaceFunc("blah,blah,blah") mustBe "blah,blah,blah"

    }

    "group by year selects the 2nd column" in {

      val groupByYearFunc = AbstractCaitCsvRepresentation.groupByYear

      groupByYearFunc(Array("1","2","3")) mustBe "2"

      val sample = Array(
        Array("1", "2", "3"),
        Array("1", "2", "3"),
        Array("a", "b", "c")
      ).groupBy(groupByYearFunc)

      sample must have size 2
      sample("2") must have length 2
      sample("b") must have length 1

    }

    "group by country selects first column and restores commas" in {

      val groupByCountryRestoringCommasFunc =
        AbstractCaitCsvRepresentation.groupByCountryRestoringCommas

      groupByCountryRestoringCommasFunc(Array("1","2","3")) mustBe "1"
      groupByCountryRestoringCommasFunc(Array("1|a","2","3")) mustBe "1,a"

      val sample = Array(
        Array("1", "2", "3"),
        Array("1", "2", "3"),
        Array("1|a","2","3")
      ).groupBy(groupByCountryRestoringCommasFunc)

      sample must have size 2
      sample("1") must have length 2
      sample("1,a") must have length 1

    }

  }

}
