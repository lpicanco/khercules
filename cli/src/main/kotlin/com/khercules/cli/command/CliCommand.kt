package com.khercules.cli.command

import com.khercules.KVCollection
import picocli.CommandLine

@CommandLine.Command(name = "", subcommands = [GetCommand::class, PutCommand::class, QuitCommand::class])
internal class CliCommand(val collection: KVCollection) : Runnable {
    override fun run() {
    }
}
