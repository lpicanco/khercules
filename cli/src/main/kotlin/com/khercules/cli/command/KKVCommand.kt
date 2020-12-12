package com.khercules.cli.command

import com.khercules.Config
import com.khercules.KVCollection
import picocli.CommandLine
import java.util.Scanner

@CommandLine.Command(
    name = "kkv",
    description = ["KHercules CLI"]
)
internal class KKVCommand: Runnable {
    @CommandLine.Parameters(description = ["Database path"])
    lateinit var databaseLocation: String

    override fun run() {
        val config = Config(databaseLocation = databaseLocation)
        print(PROMPT)
        val input: Scanner = Scanner(System.`in`).useDelimiter("\n")

        KVCollection(config).use { collection ->
            val cli = CommandLine(CliCommand(collection))
            while (true) {
                if (input.hasNext()) {
                    cli.execute(*input.next().split(" ").toTypedArray())
                    print(PROMPT)
                }
            }
        }
    }

    companion object {
        private const val PROMPT = "khercules> "
    }
}
