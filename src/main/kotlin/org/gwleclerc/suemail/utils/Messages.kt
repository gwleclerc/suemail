package org.gwleclerc.suemail.utils

import javax.mail.Message
import javax.mail.Multipart
import javax.mail.Part

/**
 * Created by gwleclerc on 22/02/17.
 */
object Messages {

    fun getFrom(message: Message): String {
        val dest = message.from
        dest ?: return ""
        return dest.asList().joinToString()
    }

    fun getDest(message: Message, type: Message.RecipientType): String {
        val dest = message.getRecipients(type)
        dest ?: return ""
        return dest.asList().joinToString()
    }

    fun getText(p: Part): String {
        val text: String = when {
            p.isMimeType("text/plain") -> p.content.toString()
            p.isMimeType("multipart/*") -> {
                val mp: Multipart = p.content as Multipart
                (0 until mp.count).map { i ->
                    val bp = mp.getBodyPart(i)
                    getText(bp)
                }.joinToString()
            }
            else -> ""
        }
        return text
    }
}