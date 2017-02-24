package org.gwleclerc.suemail.utils

import com.beust.klaxon.JsonObject
import com.beust.klaxon.json
import org.gwleclerc.suemail.mails.Mail
import java.util.*

/**
 * Created by gwleclerc on 23/02/17.
 */
object Utils {

    fun search(mail: Mail, field:String, props: Properties): String {
        val valueKey = Constants.JIRA_SEARCH_FIELD_VALUE_KEY.format(field)
        val value = props.getProperty(valueKey)
        if (value != null) {
            println("value: $value")
            return value
        }
        val subjectKey = Constants.JIRA_SEARCH_FIELD_SUBJECT_PATTERN_KEY.format(field)
        val subjectPattern = props.getProperty(subjectKey)
        if (subjectPattern != null) {
            println("subject: ${mail.subject}")
            println("subject key: $subjectKey")
            println("subject pattern: $subjectPattern")
            return subjectPattern.toRegex(RegexOption.IGNORE_CASE).matchEntire(mail.subject)?.groups?.get(1)?.value ?: ""
        }
        val bodyKey = Constants.JIRA_SEARCH_FIELD_BODY_PATTERN_KEY.format(field)
        val bodyPattern = props.getProperty(bodyKey)
        if (bodyPattern != null) {
            println("body: ${mail.body}")
            println("body key: $bodyKey")
            println("body pattern: $bodyPattern")
            return bodyPattern.toRegex(RegexOption.IGNORE_CASE).matchEntire(mail.body)?.groups?.get(1)?.value ?: ""
        }
        return ""
    }

    fun createIssue(mail: Mail, props: Properties, edit: Boolean = false): JsonObject {
        val bodyFields = JsonObject()
        val mode = if (edit) "edit" else "new"
        val fields = props.getProperty(Constants.JIRA_ISSUE_FIELDS_KEY.format(mode), "")
                .split(",")
                .map(String::trim)
                .filter{ field -> field != "" }
        fields.forEach { field ->
            val isAnOption =  props.getProperty(Constants.JIRA_ISSUE_OPTION_VALUES_KEY.format(mode, field), "") != ""
            if (isAnOption) {
                createOption(field, mail, props, bodyFields, mode)
            } else {
                createField(field, mail, props, bodyFields, mode)
            }
        }
        val body = JsonObject()
        body["fields"] = bodyFields
        return body
    }

    private fun createField(field: String, mail: Mail, props: Properties, fields: JsonObject, mode: String) {
        val bodyKey = Constants.JIRA_ISSUE_FIELD_BODY_PATTERN_KEY.format(mode, field)
        val bodyPattern = props.getProperty(bodyKey)
        if (bodyPattern != null ) {
            val matched = bodyPattern.toRegex(RegexOption.IGNORE_CASE).matchEntire(mail.body)?.groups?.get(1)?.value ?: ""
            fields[field] = matched
        }
        val subjectKey = Constants.JIRA_ISSUE_FIELD_SUBJECT_PATTERN_KEY.format(mode, field)
        val subjectPattern = props.getProperty(subjectKey)
        if (subjectPattern != null) {
            val matched = subjectPattern.toRegex(RegexOption.IGNORE_CASE).matchEntire(mail.subject)?.groups?.get(1)?.value ?: ""
            fields[field] = matched
        }
        val valueKey = Constants.JIRA_ISSUE_FIELD_VALUE_KEY.format(mode, field)
        val value = props.getProperty(valueKey)
        if (value != null) {
            fields[field] = value
        }
    }

    private fun createOption(field: String, mail: Mail, props: Properties, fields: JsonObject, mode: String){
        val optionsKey = Constants.JIRA_ISSUE_OPTION_VALUES_KEY.format(mode, field)
        val map: Map<String,String> = props.getProperty(optionsKey, "")
                .split(",")
                .map(String::trim)
                .filter { value -> value.split("=").size > 1 }
                .map { value ->
                    val entry = value.split("=")
                    entry[0] to entry[1]
                }
                .toMap()
        val bodyKey = Constants.JIRA_ISSUE_OPTION_BODY_PATTERN_KEY.format(mode, field)
        val bodyPattern = props.getProperty(bodyKey)
        if (bodyPattern != null ) {
            val matched = bodyPattern.toRegex(RegexOption.IGNORE_CASE).matchEntire(mail.body)?.groups?.get(1)?.value ?: ""
            val jsonValue = JsonObject()
            jsonValue["value"] = map[matched]
            fields[field] = jsonValue
        }
        val subjectKey = Constants.JIRA_ISSUE_OPTION_SUBJECT_PATTERN_KEY.format(mode, field)
        val subjectPattern = props.getProperty(subjectKey)
        if (subjectPattern != null) {
            val matched = subjectPattern.toRegex(RegexOption.IGNORE_CASE).matchEntire(mail.subject)?.groups?.get(1)?.value ?: ""
            val jsonValue = JsonObject()
            jsonValue["value"] = map[matched]
            fields[field] = jsonValue
        }
        val valueKey = Constants.JIRA_ISSUE_OPTION_VALUE_KEY.format(mode, field)
        val value = props.getProperty(valueKey)
        if (value != null) {
            val jsonValue = JsonObject()
            jsonValue["value"] = value
            fields[field] = jsonValue
        }
    }

    fun getFieldKey(field: String): String {
        val id = Constants.CUSTOM_FIELD_PATTERN.matchEntire(field)?.groups?.get(1)?.value
        id ?: return "$field="
        return "cf[$id]~"
    }

    fun searchMailJQL(project: String, mail: Mail, props: Properties): String {
        val res = StringBuilder()
        res.append("jql=")
        res.append("project=$project")
        val fields = props.getProperty(Constants.JIRA_SEARCH_FIELDS_KEY, "").split(",").map(String::trim)
        fields.forEach { field ->
            val key = Utils.getFieldKey(field)
            res.append("%20AND%20$key${Utils.search(mail, field, props)}")
        }
        res.append("%20ORDER%20BY%20updated%20DESC")
        println(res.toString())
        return res.toString()
    }

}