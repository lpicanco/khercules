package com.khercules.cli.command

import picocli.CommandLine

@CommandLine.Command(name = "compact", description = ["run the segment compaction process"])
class CompactCommand: Runnable {
    @CommandLine.ParentCommand
    private val parent: CliCommand? = null

    override fun run() {
        parent?.collection?.getSegmentCompactor()?.execute()
    }
}
