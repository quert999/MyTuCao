package bean

data class Result(val hid: String = "",
                  val title: String = "",
                  val play: Int = 0,
                  val mukio: Int = 0,
                  val create: String = "",
                  val thumb: String = "",
                  val typename: String = "",
                  val typeid: Int = 0,
                  val description: String = "",
                  val user: String = "",
                  val userid: String = "",
                  val keywords: String = "",
                  val part: Int = 0
                  ,var video: MutableList<ResultVideo> = mutableListOf()
)