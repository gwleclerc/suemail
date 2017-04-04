package org.gwleclerc.suemail.mails

import java.util.Date

/**
 * Created by gwleclerc on 22/02/17.
 */
data class Mail(val from: String, val to: String, val cc: String, val subject: String, val body: String, val sentDate: Date) {
    companion object {
        fun filterRegex(from: Regex, to: Regex, cc: Regex, subject: Regex, body: Regex ): (Mail) -> Boolean {
            return fun(mail: Mail): Boolean {
                return from.containsMatchIn(mail.from)
                        && to.containsMatchIn(mail.to)
                        && cc.containsMatchIn(mail.cc)
                        && subject.containsMatchIn(mail.subject)
                        && body.containsMatchIn(mail.body)
            }
        }
    }
}

