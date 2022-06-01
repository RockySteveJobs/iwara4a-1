package me.rerere.lint_rules

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue

@Suppress("UnstableApiUsage")
class LintRegistry: IssueRegistry() {
    override val issues: List<Issue> = listOf(
        BadExtensionFunctionCheck.ISSUE
    )

    override val api: Int
        get() = CURRENT_API

    override val minApi: Int
        get() = 10
}