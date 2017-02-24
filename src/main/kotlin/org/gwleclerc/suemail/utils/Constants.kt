package org.gwleclerc.suemail.utils

/**
 * Created by gwleclerc on 21/02/17.
 */
object Constants {
    // Init IMAP connection
    val IMAP_HOST_KEY="imap.host"
    val IMAP_PORT_KEY="imap.port"
    val IMAP_USER_KEY="imap.user"
    val IMAP_PASSWORD_KEY="imap.password"
    val STORE_PROTOCOL_KEY = "mail.store.protocol"
    val MAIL_IMAPS_SSL_TRUST = "mail.imaps.ssl.trust"
    val MAIL_IMAPS_SSL_SOCKET_FACTORY = "mail.imaps.ssl.socketFactory"

    // Filter keys
    val MAIL_FOLDER_KEY = "mail.folder"
    val MAIL_FILTER_NEW_ISSUE_FROM_ADDRESS_KEY = "mail.filter.new.issue.from.address"
    val MAIL_FILTER_NEW_ISSUE_TO_ADDRESS_KEY = "mail.filter.new.issue.to.address"
    val MAIL_FILTER_NEW_ISSUE_CC_ADDRESS_KEY  = "mail.filter.new.issue.cc.address"
    val MAIL_FILTER_NEW_ISSUE_SUBJECT_KEY = "mail.filter.new.issue.subject"
    val MAIL_FILTER_NEW_ISSUE_BODY_KEY = "mail.filter.new.issue.body"

    val MAIL_FILTER_NEW_RESPONSE_FROM_ADDRESS_KEY = "mail.filter.new.response.from.address"
    val MAIL_FILTER_NEW_RESPONSE_TO_ADDRESS_KEY = "mail.filter.new.response.to.address"
    val MAIL_FILTER_NEW_RESPONSE_CC_ADDRESS_KEY = "mail.filter.new.response.cc.address"
    val MAIL_FILTER_NEW_RESPONSE_SUBJECT_KEY = "mail.filter.new.response.subject"
    val MAIL_FILTER_NEW_RESPONSE_BODY_KEY = "mail.filter.new.response.body"

    // Jira
    val JIRA_URL_KEY = "jira.url"
    val JIRA_USER_KEY = "jira.user"
    val JIRA_PASSWORD_KEY = "jira.password"
    val JIRA_PROJECT_KEY = "jira.project"

    // Recherche JIRA
    val JIRA_SEARCH_FIELDS_KEY = "jira.search.fields"
    val JIRA_SEARCH_FIELD_VALUE_KEY = "jira.search.%s.value"
    val JIRA_SEARCH_FIELD_SUBJECT_PATTERN_KEY = "jira.search.%s.subject.pattern"
    val JIRA_SEARCH_FIELD_BODY_PATTERN_KEY = "jira.search.%s.body.pattern"

    // Creation / Modification
    val JIRA_ISSUE_FIELDS_KEY = "jira.%s.issue.fields"

    // Text Field
    val JIRA_ISSUE_FIELD_VALUE_KEY = "jira.%s.issue.%s.field.value"
    val JIRA_ISSUE_FIELD_SUBJECT_PATTERN_KEY = "jira.%s.issue.%s.field.subject.pattern"
    val JIRA_ISSUE_FIELD_BODY_PATTERN_KEY = "jira.%s.issue.%s.field.body.pattern"

    // Option Field
    val JIRA_ISSUE_OPTION_VALUES_KEY = "jira.%s.issue.%s.option.values"
    val JIRA_ISSUE_OPTION_VALUE_KEY = "jira.%s.issue.%s.option.value"
    val JIRA_ISSUE_OPTION_SUBJECT_PATTERN_KEY = "jira.%s.issue.%s.option.subject.pattern"
    val JIRA_ISSUE_OPTION_BODY_PATTERN_KEY = "jira.%s.issue.%s.option.body.pattern"

    // Status
    val JIRA_ISSUE_STATUS = "jira.issue.status"

    val CUSTOM_FIELD_PATTERN = "^customfield_(\\d+)$".toRegex()
}