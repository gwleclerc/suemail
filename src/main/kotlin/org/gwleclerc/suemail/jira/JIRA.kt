package org.gwleclerc.suemail.jira

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.obj
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.httpPut
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.map

/**
 * Created by gwleclerc on 23/02/17.
 */
object JIRA {

    fun find(url: String, jql : String, user: String, password: String): Result<JsonObject, Exception> {
        val (request, response, result) = "$url/search?$jql".httpGet().authenticate(user, password).responseString()
        return result.map { data ->
            val parser = Parser()
            val json = parser.parse(StringBuilder(data)) as? JsonObject ?: JsonObject()
            json
        }
    }

    fun getTransition(url: String, issue: String, user: String, password: String): Result<JsonObject, Exception> {
        val (request, response, result) = "$url/issue/$issue/transitions".httpGet().authenticate(user, password).responseString()
        return result.map { data ->
            val parser = Parser()
            val json = parser.parse(StringBuilder(data)) as? JsonObject ?: JsonObject()
            json
        }
    }

    fun doTransition(url: String, issue: String, transitionID: String,  user: String, password: String) {
        val body = JsonObject()
        val transition = JsonObject(mapOf("id" to transitionID))
        body["transition"] = transition
        "$url/issue/$issue/transitions".httpPost()
                .body(body.toJsonString())
                .header("Content-Type" to "application/json")
                .authenticate(user, password)
                .responseString()
    }

    fun createIssue(url: String, project: String, issue: JsonObject, user: String, password: String): Result<String, Exception> {
        val fields = issue.obj("fields") ?: JsonObject()
        fields["project"] = JsonObject(mapOf("key" to project))
        fields["issuetype"] = JsonObject(mapOf("name" to "Support"))
        issue["fields"] = fields
        println(issue.toJsonString(true))
        val (request, response, result) = "$url/issue/".httpPost()
                .body(issue.toJsonString())
                .header("Content-Type" to "application/json")
                .authenticate(user, password)
                .responseString()
        return result
    }

    fun editIssue(url: String, key: String, issue: JsonObject, user: String, password: String): Result<String, Exception> {
        val (request, response, result) = "$url/issue/$key".httpPut()
                .body(issue.toJsonString())
                .header("Content-Type" to "application/json")
                .authenticate(user, password)
                .responseString()
        return result
    }
}