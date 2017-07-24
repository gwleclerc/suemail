package org.gwleclerc.suemail.mails

import com.sun.mail.util.MailSSLSocketFactory
import org.gwleclerc.suemail.utils.Constants
import org.gwleclerc.suemail.utils.Messages
import java.util.Properties
import java.util.Date
import javax.mail.Store
import javax.mail.Session
import javax.mail.Folder
import javax.mail.Flags
import javax.mail.Message
import javax.mail.Message.RecipientType
import javax.mail.search.FlagTerm

/**
 * Created by gwleclerc on 14/02/17.
 */
object Reader {

    private fun initStore(props: Properties): Store? {
        val protocol = props.getProperty(Constants.STORE_PROTOCOL_KEY, "imaps")

        // init insecure
        val imapProps = System.getProperties()
        val socketFactory = MailSSLSocketFactory()
        socketFactory.isTrustAllHosts = true
        imapProps.put(Constants.MAIL_IMAPS_SSL_SOCKET_FACTORY, socketFactory)
        imapProps.setProperty(Constants.STORE_PROTOCOL_KEY, protocol)
        imapProps.setProperty(Constants.MAIL_IMAPS_SSL_TRUST, "*")

        //init store
        val session = Session.getDefaultInstance(imapProps, null)
        var store: Store? = null
        try {
            store = session.getStore(protocol)
            val host = props.getProperty(Constants.IMAP_HOST_KEY, "")
            val port = props.getProperty(Constants.IMAP_PORT_KEY, "")
            val user = props.getProperty(Constants.IMAP_USER_KEY, "")
            val password = props.getProperty(Constants.IMAP_PASSWORD_KEY, "")
            store.connect(host, port.toInt(), user, password)
        } finally {
            return store
        }
    }

    fun fetchMails(props: Properties): List<Mail> {
        val store = initStore(props)
        val folder = props.getProperty(Constants.MAIL_FOLDER_KEY, "inbox")
        val inbox = store?.getFolder(folder)
        inbox?.open(Folder.READ_WRITE)
        val seenFlag = Flags(Flags.Flag.SEEN)
        val unseenFlagTerm = FlagTerm(seenFlag, false)
        var messages = arrayOf<Message>()
        try {
            messages = if (inbox != null) inbox.search(unseenFlagTerm) else arrayOf()
        } catch (e: Exception) {
            println(e)
        }
        println("Total Messages:- " + messages.size)
        val mails = messages.map { message ->
            message.setFlag(Flags.Flag.SEEN, true)
            Mail(
                    from = Messages.getFrom(message),
                    to = Messages.getDest(message, RecipientType.TO),
                    cc = Messages.getDest(message, RecipientType.CC),
                    subject = message.subject ?: "",
                    body = Messages.getText(message),
                    sentDate = message.sentDate ?: Date()
            )
        }

        println("Received emails:")
        mails.forEach { mail -> println(mail.subject) }
        println()

        val fromNewIssue = toRegex(props, Constants.MAIL_FILTER_NEW_ISSUE_FROM_ADDRESS_KEY)
        val toNewIssue = toRegex(props, Constants.MAIL_FILTER_NEW_ISSUE_TO_ADDRESS_KEY)
        val ccNewIssue = toRegex(props, Constants.MAIL_FILTER_NEW_ISSUE_CC_ADDRESS_KEY)
        val subjectNewIssue = toRegex(props, Constants.MAIL_FILTER_NEW_ISSUE_SUBJECT_KEY)
        val bodyNewIssue = toRegex(props, Constants.MAIL_FILTER_NEW_ISSUE_BODY_KEY)
        val newIssues = mails.filter(Mail.filterRegex(fromNewIssue, toNewIssue, ccNewIssue, subjectNewIssue, bodyNewIssue))
        println("New issues:- " + newIssues.size)
        newIssues.forEach { mail -> println(mail.subject) }
        println()

        val fromNewResponse = toRegex(props, Constants.MAIL_FILTER_NEW_RESPONSE_FROM_ADDRESS_KEY)
        val toNewResponse = toRegex(props, Constants.MAIL_FILTER_NEW_RESPONSE_TO_ADDRESS_KEY)
        val ccNewResponse = toRegex(props, Constants.MAIL_FILTER_NEW_RESPONSE_CC_ADDRESS_KEY)
        val subjectNewResponse = toRegex(props, Constants.MAIL_FILTER_NEW_RESPONSE_SUBJECT_KEY)
        val bodyNewResponse = toRegex(props, Constants.MAIL_FILTER_NEW_RESPONSE_BODY_KEY)
        val newMessages = mails.filter(Mail.filterRegex(fromNewResponse, toNewResponse, ccNewResponse, subjectNewResponse, bodyNewResponse))
        println("New messages in existing issues:- " + newMessages.size)
        newMessages.forEach { mail -> println(mail.subject) }
        println()

        inbox?.close(false)
        store?.close()
        return newIssues.plus(newMessages)
    }

    private fun toRegex(props: Properties, filter: String): Regex {
        return props.getProperty(filter, ".*").toRegex(RegexOption.IGNORE_CASE)
    }
}