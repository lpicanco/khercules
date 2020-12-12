package com.khercules.cli.command

import picocli.CommandLine
import kotlin.system.exitProcess

@CommandLine.Command(name = "quit", description = ["Quit this shell"])
internal class QuitCommand : Runnable {
    override fun run() {
        exitProcess(0)
    }
}
