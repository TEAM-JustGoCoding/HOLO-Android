package kr.co.ajjulcoding.team.project.holo

class UtilityBillItem(content: String?) {
    var content: String? = content

    @JvmName("getContent1")
    fun getContent(): String? {
        return content
    }

    @JvmName("setContent1")
    fun setContent(content: String?) {
        this.content = content
    }
}