package com.khercules.cli

import com.khercules.cli.command.KKVCommand
import picocli.CommandLine

fun main(args: Array<String>) {
    System.exit(CommandLine(KKVCommand()).execute(*args))
}
