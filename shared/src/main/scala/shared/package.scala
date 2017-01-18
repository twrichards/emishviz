import upickle.{json, default}

package object shared {

  type CaitMap = Map[String, CaitYearDetail]
  type CaitYearDetail = Map[String, CaitYearCountryDetail]
  type CaitYearCountryDetail = Map[String, Map[String, Double]]

  type ISO3166 = Array[Map[String, String]]

  def stringifyCait(caitMap: CaitMap): String =
    upickle.json.write(upickle.default.writeJs[CaitMap](caitMap))

  def stringifyCaitYearDetail(yearDetail: CaitYearDetail): String =
    upickle.json.write(upickle.default.writeJs[CaitYearDetail](yearDetail), indent = 4)

  def parseCait(unparsedJSON: String): CaitMap =
    default.readJs[CaitMap](json.read(unparsedJSON))

  def parseISO3166(unparsedJSON: String): ISO3166 =
    default.readJs[ISO3166](json.read(unparsedJSON))

  val NAME = "name"
  val VALUE = "value"
  val MUTE = "mute"
  val PADDING = "padding"
  val HEATMAP = "heatmap"
  val ID = "id"
  val COUNTRY_CODE = "country-code"

  val GASES = "gases"
  val CO2 = "co2"
  val CH4 = "ch4"
  val N2O = "n2o"

  val SOURCE = "source"
  val ENERGY = "energy"
  val TRANSPORT = "transport"
  val AGRICULTURE = "agriculture"
  val INDUSTRIAL = "industrial"
  val WASTE = "waste"
  val LAND_USE_CHANGE = "land use change"

  val POPULATION = "population"
  val GDP = "gdp"
  val PPP = "ppp"
  val USD = "usd"

  //TODO move to own object
  val CaitCountryToAlpha5 = Map(
    "Afghanistan" -> "asafg",
    "Albania" -> "eualb",
    "Algeria" -> "afdza",
    "Angola" -> "afago",
    "Antigua & Barbuda" -> "naatg",
    "Argentina" -> "saarg",
    "Armenia" -> "asarm",
    "Australia" -> "ocaus",
    "Austria" -> "euaut",
    "Azerbaijan" -> "asaze",
    "Bahamas, The" -> "nabhs",
    "Bahrain" -> "asbhr",
    "Bangladesh" -> "asbgd",
    "Barbados" -> "nabrb",
    "Belarus" -> "eublr",
    "Belgium" -> "eubel",
    "Belize" -> "nablz",
    "Benin" -> "afben",
    "Bhutan" -> "asbtn",
    "Bolivia" -> "sabol",
    "Bosnia & Herzegovina" -> "eubih",
    "Botswana" -> "afbwa",
    "Brazil" -> "sabra",
    "Brunei" -> "asbrn",
    "Bulgaria" -> "eubgr",
    "Burkina Faso" -> "afbfa",
    "Burundi" -> "afbdi",
    "Cambodia" -> "askhm",
    "Cameroon" -> "afcmr",
    "Canada" -> "nacan",
    "Cape Verde" -> "afcpv",
    "Central African Republic" -> "afcaf",
    "Chad" -> "aftcd",
    "Chile" -> "sachl",
    "China" -> "aschn",
    "Colombia" -> "sacol",
    "Comoros" -> "afcom",
    "Congo, Dem. Rep." -> "afcod",
    "Congo, Rep." -> "afcog",
    "Cook Islands" -> "occok",
    "Costa Rica" -> "nacri",
    "Cote d'Ivoire" -> "afciv",
    "Croatia" -> "euhrv",
    "Cuba" -> "nacub",
    "Cyprus" -> "ascyp",
    "Czech Republic" -> "eucze",
    "Denmark" -> "eudnk",
    "Djibouti" -> "afdji",
    "Dominica" -> "nadma",
    "Dominican Republic" -> "nadom",
    "Ecuador" -> "saecu",
    "Egypt" -> "afegy",
    "El Salvador" -> "naslv",
    "Equatorial Guinea" -> "afgnq",
    "Eritrea" -> "aferi",
    "Estonia" -> "euest",
    "Ethiopia" -> "afeth",
    "Fiji" -> "ocfji",
    "Finland" -> "eufin",
    "France" -> "eufra",
    "Gabon" -> "afgab",
    "Gambia, The" -> "afgmb",
    "Georgia" -> "asgeo",
    "Germany" -> "eudeu",
    "Ghana" -> "afgha",
    "Greece" -> "eugrc",
    "Grenada" -> "nagrd",
    "Guatemala" -> "nagtm",
    "Guinea" -> "afgin",
    "Guinea-Bissau" -> "afgnb",
    "Guyana" -> "saguy",
    "Haiti" -> "nahti",
    "Honduras" -> "nahnd",
    "Hungary" -> "euhun",
    "Iceland" -> "euisl",
    "India" -> "asind",
    "Indonesia" -> "asidn",
    "Iran" -> "asirn",
    "Iraq" -> "asirq",
    "Ireland" -> "euirl",
    "Israel" -> "asisr",
    "Italy" -> "euita",
    "Jamaica" -> "najam",
    "Japan" -> "asjpn",
    "Jordan" -> "asjor",
    "Kazakhstan" -> "askaz",
    "Kenya" -> "afken",
    "Kiribati" -> "ockir",
    "Korea, Dem. Rep. (North)" -> "asprk",
    "Korea, Rep. (South)" -> "askor",
    "Kuwait" -> "askwt",
    "Kyrgyzstan" -> "askgz",
    "Laos" -> "aslao",
    "Latvia" -> "eulva",
    "Lebanon" -> "aslbn",
    "Lesotho" -> "aflso",
    "Liberia" -> "aflbr",
    "Libya" -> "aflby",
    "Lithuania" -> "eultu",
    "Luxembourg" -> "eulux",
    "Macedonia, FYR" -> "eumkd",
    "Madagascar" -> "afmdg",
    "Malawi" -> "afmwi",
    "Malaysia" -> "asmys",
    "Maldives" -> "asmdv",
    "Mali" -> "afmli",
    "Malta" -> "eumlt",
    "Mauritania" -> "afmrt",
    "Mauritius" -> "afmus",
    "Mexico" -> "namex",
    "Moldova" -> "eumda",
    "Mongolia" -> "asmng",
    "Montenegro" -> "eumne",
    "Morocco" -> "afmar",
    "Mozambique" -> "afmoz",
    "Myanmar" -> "asmmr",
    "Namibia" -> "afnam",
    "Nauru" -> "ocnru",
    "Nepal" -> "asnpl",
    "Netherlands" -> "eunld",
    "New Zealand" -> "ocnzl",
    "Nicaragua" -> "nanic",
    "Niger" -> "afner",
    "Nigeria" -> "afnga",
    "Niue" -> "ocniu",
    "Norway" -> "eunor",
    "Oman" -> "asomn",
    "Pakistan" -> "aspak",
    "Palau" -> "ocplw",
    "Panama" -> "napan",
    "Papua New Guinea" -> "ocpng",
    "Paraguay" -> "sapry",
    "Peru" -> "saper",
    "Philippines" -> "asphl",
    "Poland" -> "eupol",
    "Portugal" -> "euprt",
    "Qatar" -> "asqat",
    "Romania" -> "eurou",
    "Russian Federation" -> "asrus",
    "Rwanda" -> "afrwa",
    "Saint Kitts & Nevis" -> "nakna",
    "Saint Lucia" -> "nalca",
    "Saint Vincent & Grenadines" -> "navct",
    "Samoa" -> "ocwsm",
    "Sao Tome & Principe" -> "afstp",
    "Saudi Arabia" -> "assau",
    "Senegal" -> "afsen",
    "Serbia" -> "eusrb",
    "Seychelles" -> "afsyc",
    "Sierra Leone" -> "afsle",
    "Singapore" -> "assgp",
    "Slovakia" -> "eusvk",
    "Slovenia" -> "eusvn",
    "Solomon Islands" -> "ocslb",
    "South Africa" -> "afzaf",
    "Spain" -> "euesp",
    "Sri Lanka" -> "aslka",
    "Sudan" -> "afsdn",
    "Suriname" -> "sasur",
    "Swaziland" -> "afswz",
    "Sweden" -> "euswe",
    "Switzerland" -> "euche",
    "Syria" -> "assyr",
    "Tajikistan" -> "astjk",
    "Tanzania" -> "aftza",
    "Thailand" -> "astha",
    "Togo" -> "aftgo",
    "Tonga" -> "octon",
    "Trinidad & Tobago" -> "natto",
    "Tunisia" -> "aftun",
    "Turkey" -> "astur",
    "Turkmenistan" -> "astkm",
    "Uganda" -> "afuga",
    "Ukraine" -> "euukr",
    "United Arab Emirates" -> "asare",
    "United Kingdom" -> "eugbr",
    "United States" -> "nausa",
    "Uruguay" -> "saury",
    "Uzbekistan" -> "asuzb",
    "Vanuatu" -> "ocvut",
    "Venezuela" -> "saven",
    "Vietnam" -> "asvnm",
    "Yemen" -> "asyem",
    "Zambia" -> "afzmb",
    "Zimbabwe" -> "afzwe"
  )

}
