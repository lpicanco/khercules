package com.khercules.cli.command

import kotlin.system.exitProcess
import picocli.CommandLine

@CommandLine.Command(name = "quit", description = ["Quit this shell"])
internal class QuitCommand : Runnable {
    override fun run() {
        exitProcess(0)
    }
}
