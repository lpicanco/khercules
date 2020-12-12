package com.khercules.cli.command

import picocli.CommandLine

@CommandLine.Command(name = "put", description = ["put a value by a key"])
class PutCommand: Runnable {
    @CommandLine.Parameters(index = "0", description = ["key to put"])
    lateinit var key: String

    @CommandLine.Parameters(index = "1", description = ["value to put"])
    lateinit var value: String

    @CommandLine.ParentCommand
    private val parent: CliCommand? = null

    override fun run() {
        parent?.collection?.put(key, value.toByteArray())
    }
}
