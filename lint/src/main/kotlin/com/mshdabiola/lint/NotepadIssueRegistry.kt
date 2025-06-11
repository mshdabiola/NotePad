

package com.mshdabiola.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.mshdabiola.lint.designsystem.DesignSystemDetector

class NotepadIssueRegistry : IssueRegistry() {

    override val issues = listOf(
        DesignSystemDetector.ISSUE,
        TestMethodNameDetector.FORMAT,
        TestMethodNameDetector.PREFIX,
    )

    override val api: Int = CURRENT_API

    override val minApi: Int = 12

    override val vendor: Vendor = Vendor(
        vendorName = "Notepad",
        feedbackUrl = "https://github.com/mshdabiola/notepad/issues",
        contact = "https://github.com/mshdabiola/notepad",
    )
}
