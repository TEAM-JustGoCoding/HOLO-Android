package kr.co.ajjulcoding.team.project.holo

class NotificationItem(name: String?, message: String?) {
    var name: String? = name
    var message: String? = message

    @JvmName("getMessage1")
    fun getMessage(): String? {
        return message
    }

    @JvmName("getName1")
    fun getName(): String? {
        return name
    }

    @JvmName("setMessage1")
    fun setMessage(message: String?) {
        this.message = message
    }

    @JvmName("setName1")
    fun setName(name: String?) {
        this.name = name
    }
}