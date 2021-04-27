import java.lang.NullPointerException
import java.lang.NumberFormatException

fun readLineTrim() = readLine()!!.trim()

fun main() {
    println("== SIMPLE SSG 시작 ==")

    while (true) {
        print("명령어) ")
        val command = readLineTrim() // 입력 : /article/detail?id=1&title=제목1

        val rq = Rq(command)

        println(rq.getStringParam("title","1") == "제목1") // true
        println(rq.getIntParam("id",-1) == -1) // true
    }

    println("== SIMPLE SSG 끝 ==")
}

class Rq(command: String) {
    val actionPath: String
    val paramMap: Map<String, String>

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

            // queryStr = id=1&body=2&title=3&age
            val queryStrBits = queryStr.split("&")

            for (queryStrBit in queryStrBits) {
                // queryStrBit = id=1
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

    fun getStringParam(title:String,default: String): String? {
        return paramMap[title]?:default //좌측이 null이면 우측실행
    }
    fun getIntParam(id:String,default: Int): Int {
        return try{
        paramMap[id]!!.toInt()}
            catch(e:NumberFormatException){
                default
            } ?:default
    }
}