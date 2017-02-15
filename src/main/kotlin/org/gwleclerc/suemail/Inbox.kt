package org.gwleclerc.suemail

import org.jsoup.Jsoup
import java.util.*
import javax.mail.*
import javax.mail.search.FlagTerm
import javax.mail.search.SearchTerm


/**
 * Created by gwleclerc on 14/02/17.
 */
object Inbox {

    val SMTP_HOST="mail.smtp.host"
    val SMTP_USER="mail.smtp.user"
    val SMTP_PASSWORD="mail.smtp.password"

    fun read(props: Properties) {

        val session = Session.getDefaultInstance(props, null)

        val store = session.getStore("imaps")
        val host = props.getProperty(SMTP_HOST)
        val user = props.getProperty(SMTP_USER)
        val password = props.getProperty(SMTP_PASSWORD)
        store.connect(host, user, password)

        val inbox = store.getFolder("inbox")
        inbox.open(Folder.READ_WRITE)
        val seenFlag = Flags(Flags.Flag.SEEN)
        val unseenFlagTerm = FlagTerm(seenFlag, false)
        val messages = inbox.search(SupportSearch(unseenFlagTerm))

        println("Total Messages:- " + messages.size)


        for (i in 0 until messages.size) {
            println("------------------------------")
            println("Mail Subject: ${messages[i].subject} Date: ${messages[i].sentDate}")
            println("------------------------------")
            println(getText(messages[i]))
            println("------------------------------")
            println()
            println()
        }

        inbox.close(false)
        store.close()
    }

    private fun getText(p: Part): String {
        var text: String = ""
        when {
            p.isMimeType("text/plain") -> text = p.content.toString()
            p.isMimeType("text/html") -> text = Jsoup.parse(p.content.toString()).text()
            p.isMimeType("multipart/*") -> {
                val mp: Multipart = p.content as Multipart
                for (i in 0 until mp.count) {
                    val bp = mp.getBodyPart(i)
                    text += getText(bp)
                }
            }
        }
        return text
    }
}

class SupportSearch(val flagTerm: FlagTerm) : SearchTerm() {

    override fun match(msg: Message?): Boolean {
        var match = flagTerm.match(msg)
        if (match) {

        }
        return match
    }

}