package org.gwleclerc.suemail

import org.gwleclerc.suemail.jira.Sender
import org.gwleclerc.suemail.mails.Reader
import org.gwleclerc.suemail.utils.Constants
import java.io.File
import java.io.FileInputStream
import java.util.*
import kotlin.system.exitProcess


/**
 * Created by gwleclerc on 14/02/17.
 */

fun main(args: Array<String>) {
    if (args.size < 1) {
        println("Usage:")
        println("   java -jar suemail.jar <property-file-path>")
        exitProcess(1)
    }
    val props = Properties()
    props.load(FileInputStream(File(args[0])))
    val url = props.getProperty(Constants.JIRA_URL_KEY, "")
    val user = props.getProperty(Constants.JIRA_USER_KEY, "")
    val password = props.getProperty(Constants.JIRA_PASSWORD_KEY, "")
    val project = props.getProperty(Constants.JIRA_PROJECT_KEY, "")
    val mails = Reader.fetchMails(props)
    Sender(url, user, password, project).manageIssues(mails, props)
}