package me.rerere.lint_rules

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiMethodCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod

@Suppress("UnstableApiUsage")
class BadExtensionFunctionCheck(): Detector(), SourceCodeScanner {
    override fun createUastHandler(context: JavaContext): UElementHandler {
        return object : UElementHandler() {
            override fun visitMethod(node: UMethod) {
                context.report(
                    issue = ISSUE,
                    scopeClass = node,
                    location = context.getLocation(node),
                    message = "Test Message"
                )
            }
        }
    }

    companion object {
        @JvmField
        val ISSUE = Issue.create(
            id = "BadExtensionFunction",
            briefDescription = "在基础类型上滥用扩展函数",
            explanation = """
                如非必要，请不要在基础数据类型和String类型上添加扩展函数，尤其是和APP功能有耦合的扩展函数
                
                例如，将String转化为日期Date的扩展函数，String和日期直接并无强关联，创建这样的扩展函数只
                会让APP变得难以维护
            """.trimIndent(),
            priority = 4,
            severity = Severity.WARNING,
            implementation = Implementation(
                BadExtensionFunctionCheck::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}