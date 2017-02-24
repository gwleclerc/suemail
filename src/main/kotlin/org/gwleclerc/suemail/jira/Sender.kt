package org.gwleclerc.suemail.jira

import com.beust.klaxon.*
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.flatMap
import com.github.kittinunf.result.map
import org.gwleclerc.suemail.mails.Mail
import org.gwleclerc.suemail.utils.Constants
import org.gwleclerc.suemail.utils.Utils
import java.util.*

/**
 * Created by gwleclerc on 22/02/17.
 */
class Sender(val url: String, val user : String, val password : String, val project : String) {
    fun manageIssues(mails: List<Mail>, props: Properties) {
        mails.forEach { mail ->
            val json = JIRA.find(url, Utils.searchMailJQL(project, mail, props), user, password)
            val result = json.flatMap { value ->
                val nbResults = value.int("total") ?: 0
                if (nbResults == 0) {
                    createIssue(mail, props)
                } else {
                    val issues = value.array<JsonObject>("issues") ?: JsonArray()
                    println("issues: ${issues.size}")
                    val fixVersion = if (issues.size > 0) issues[0].obj("fields")?.array<JsonObject>("fixVersions") ?: JsonArray() else JsonArray()
                    println("fixVersion: $fixVersion")
                    if (fixVersion.size > 0) {
                        createIssue(mail, props)
                    } else {
                        editIssue(mail, props, issues[0].string("key") ?: "" )
                    }
                }
            }
            when (result) {
                is Result.Success -> println(result.value)
                is Result.Failure -> println(result.error)
            }
            println()
        }
    }

    private fun createIssue(mail: Mail, props : Properties): Result<String, Exception> {
        println("creating issue")
        val body = Utils.createIssue(mail, props)
        return JIRA.createIssue(url, project, body, user, password).map { "Issue Created" }
    }

    private fun editIssue(mail: Mail, props : Properties, issue: String): Result<Any, Exception> {
        println("editing issue")
        val body = Utils.createIssue(mail, props, edit = true)
        val result = JIRA.editIssue(url, issue, body, user, password)
        val statusList = props.getProperty(Constants.JIRA_ISSUE_STATUS, "").split(",")
        statusList.forEach { status ->
            JIRA.getTransition(url, issue, user, password).map { res ->
                val transitions = res.array<JsonObject>("transitions")?.map { transition ->
                    val key = transition.obj("to")?.string("name") ?: ""
                    val value = transition.string("id") ?: ""
                    key to value
                }?.toMap() ?: mapOf()
                JIRA.doTransition(url, issue, transitions[status]?: "", user, password)
            }
        }
        return result.map { "Issue Updated" }
    }
}