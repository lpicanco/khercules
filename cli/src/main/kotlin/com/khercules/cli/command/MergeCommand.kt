package com.khercules.cli.command

import picocli.CommandLine

@CommandLine.Command(name = "merge", description = ["run the segment merge process"])
class MergeCommand : Runnable {
    @CommandLine.ParentCommand
    private val parent: CliCommand? = null

    override fun run() {
        parent?.collection?.getSegmentMerger()?.execute()
    }
}
