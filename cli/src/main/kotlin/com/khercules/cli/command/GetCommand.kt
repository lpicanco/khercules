package com.khercules.cli.command

import picocli.CommandLine

@CommandLine.Command(name = "get", description = ["get a value by a key"])
class GetCommand : Runnable {
    @CommandLine.Parameters(description = ["key to get value"])
    lateinit var key: String

    @CommandLine.ParentCommand
    private val parent: CliCommand? = null

    override fun run() {
        println(parent?.collection?.get(key)?.decodeToString())
    }
}
