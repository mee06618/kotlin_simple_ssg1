import java.text.SimpleDateFormat

fun main() {
    println("== SIMPLE SSG 시작 ==")

    articleRepository.makeTestArticles()

    while (true) {
        print("명령어) ")
        val command = readLineTrim()

        val rq = Rq(command)

        when (rq.actionPath) {
            "/system/exit" -> {
                println("프로그램을 종료합니다.")
                break
            }
            "/article/write" -> {
                print("제목")
                val title = readLineTrim()
                print("내용")
                val body = readLineTrim()
                articleRepository.addArticle(title, body)

            }
            "/article/list" -> {

                articleRepository.viewArticles(rq.getIntParam("page", 1),rq.getStringParam("keyword",default = "xxx"))

            }
            "/article/modify" -> {

                articleRepository.modifyArticles(rq.getIntParam("id",default = -1))
            }
            "/article/delete" -> {

                articleRepository.modifyArticles(rq.getIntParam("id",default = -1))
            }
            "/article/detail" -> {
                val id = rq.getIntParam("id", 0)

                if (id == 0) {
                    println("id를 입력해주세요.")
                    continue
                }

                val article = articleRepository.articles[id - 1]

                println(article)
            }
        }
    }

    println("== SIMPLE SSG 끝 ==")
}

class Rq(command: String) {
    val actionPath: String
    private val paramMap: Map<String, String>

    init {
        val commandBits = command.split("?", limit = 2)

        actionPath = commandBits[0].trim()
        val queryStr = if (commandBits.lastIndex == 1 && commandBits[1].isNotEmpty()) {
            commandBits[1].trim()
        } else {
            ""
        }

        paramMap = if (queryStr.isEmpty()) {
            mapOf()
        } else {
            val paramMapTemp = mutableMapOf<String, String>()

            val queryStrBits = queryStr.split("&")

            for (queryStrBit in queryStrBits) {
                val queryStrBitBits = queryStrBit.split("=", limit = 2)
                val paramName = queryStrBitBits[0]
                val paramValue = if (queryStrBitBits.lastIndex == 1 && queryStrBitBits[1].isNotEmpty()) {
                    queryStrBitBits[1].trim()
                } else {
                    ""
                }

                if (paramValue.isNotEmpty()) {
                    paramMapTemp[paramName] = paramValue
                }
            }

            paramMapTemp.toMap()
        }
    }

    fun getStringParam(name: String, default: String): String {
        return paramMap[name] ?: default
    }

    fun getIntParam(name: String, default: Int): Int {
        return if (paramMap[name] != null) {
            try {
                paramMap[name]!!.toInt()
            } catch (e: NumberFormatException) {
                default
            }
        } else {
            default
        }
    }
}

// 게시물 관련 시작
data class Article(
    val id: Int,
    val title: String,
    val body: String,
    val regDate: String,
    val updateDate: String


)

object articleRepository {
    val articles = mutableListOf<Article>()
    var lastId = 0

    fun addArticle(title: String, body: String) {
        val id = ++lastId
        val regDate = Util.getNowDateStr()
        val updateDate = Util.getNowDateStr()
        articles.add(Article(id,title,body, regDate, updateDate))
    }

    fun makeTestArticles() {
        for (id in 1..100) {
            addArticle("제목_$id", "내용_$id")
        }
    }

    fun viewArticles(id: Int, keyword: String) {
        var lastNum= (id * 5)
        var firstNum=(id - 1) * 5
        val temp = articles.reversed()
        if (id == null) {

            for (i in 0 until 5) {
                println("${temp[i].id} / ${temp[i].title} / ${temp[i].body} / ${temp[i].regDate} / ${temp[i].updateDate}")
            }
        } else {

            var arr2 = mutableListOf<Article>()
            if (temp.any { it.title == keyword }) {
                for (i in 0 until 100) {
                    if (temp[i].title.contains(keyword))
                        arr2.add(temp[i])
                }

                for (i in firstNum until lastNum) {
                    println("${arr2[i].id} / ${arr2[i].title} / ${arr2[i].body} / ${arr2[i].regDate} / ${arr2[i].updateDate}")
                }
            }
            for (i in firstNum until lastNum) {
                println("${temp[i].id} / ${temp[i].title} / ${temp[i].body} / ${temp[i].regDate} / ${temp[i].updateDate}")
            }


        }
    }
        fun modifyArticles(num: Int) {
            print("새제목 : ")
            val title = readLine()!!.trim()
            print("새내용 : ")
            val body = readLine()!!.trim()
            val num = articles[num - 1].id
            val update = Util.getNowDateStr()
            val reg = articles[num - 1].regDate
            val temp = Article(num, title, body, reg, update)
            articles.removeAt(num - 1)
            articles.add(num- 1, temp)
        }

        fun deleteArticles(num: Int, default: Int) {
            if (num == 0) {
                println("없습니다")
            } else {
                articles.removeAt(num - 1)
                println("${num}번 삭제됨")
            }
        }

}


// 게시물 관련 끝



// 유틸 관련 시작
fun readLineTrim() = readLine()!!.trim()

object Util {
    fun getNowDateStr(): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        return format.format(System.currentTimeMillis())
    }
}
// 유틸 관련 끝