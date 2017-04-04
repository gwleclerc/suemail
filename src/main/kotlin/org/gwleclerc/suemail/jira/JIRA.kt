package org.gwleclerc.suemail.jira

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.obj
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.httpPut
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.map

/**
 * Created by gwleclerc on 23/02/17.
 */
object JIRA {

    fun find(url: String, jql: String, user: String, password: String): Result<JsonObject, Exception> {
        return doGet("$url/search?$jql", user, password)
    }

    fun getTransition(url: String, issue: String, user: String, password: String): Result<JsonObject, Exception> {
        return doGet("$url/issue/$issue/transitions", user, password)
    }

    /**
     * Returns the JSON result of a GET call or an exception if the call failed
     */
    private fun doGet(url: String, user: String, password: String): Result<JsonObject, Exception> {
        val (_, _, result) = url.httpGet().authenticate(user, password).responseString()
        return result.map { data -> Parser().parse(StringBuilder(data)) as? JsonObject ?: JsonObject() }
    }

    fun doTransition(url: String, issue: String, transitionID: String, user: String, password: String): Result<String, Exception> {
        val body = JsonObject()
        val transition = JsonObject(mapOf("id" to transitionID))
        body["transition"] = transition

        return doRequest("$url/issue/$issue/transitions".httpPost(), body, user, password)
    }

    fun createIssue(url: String, project: String, issue: JsonObject, user: String, password: String): Result<String, Exception> {
        val fields = issue.obj("fields") ?: JsonObject()
        fields["project"] = JsonObject(mapOf("key" to project))
        fields["issuetype"] = JsonObject(mapOf("name" to "Support"))
        issue["fields"] = fields
        println(issue.toJsonString(true))

        return doRequest("$url/issue/".httpPost(), issue, user, password)
    }

    fun editIssue(url: String, key: String, issue: JsonObject, user: String, password: String): Result<String, Exception> {
        return doRequest("$url/issue/$key".httpPut(), issue, user, password)
    }

    /**
     * Calls a request with the given body and returns the result or an exception if the call failed
     */
    private fun doRequest(request: Request, body: JsonObject, user: String, password: String): Result<String, Exception> {
        val (_, _, result) = request
                .body(body.toJsonString())
                .header("Content-Type" to "application/json")
                .authenticate(user, password)
                .responseString()
        return result
    }
}