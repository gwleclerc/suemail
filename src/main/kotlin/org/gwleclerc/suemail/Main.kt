package org.gwleclerc.suemail

import java.io.File
import java.io.FileInputStream
import java.util.*


/**
 * Created by gwleclerc on 14/02/17.
 */

fun main(args: Array<String>) {
    val props = Properties()
    props.load(FileInputStream(File("test.properties")))
    Inbox.read(props)
}