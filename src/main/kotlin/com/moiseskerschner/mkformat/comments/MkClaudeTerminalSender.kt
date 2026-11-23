package com.moiseskerschner.mkformat.comments

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.content.Content
import com.intellij.terminal.ui.TerminalWidget
import org.jetbrains.plugins.terminal.TerminalToolWindowManager

object MkClaudeTerminalSender {

    private const val NOTIFICATION_GROUP_ID = "mk-format"
    private const val TERMINAL_TOOL_WINDOW_ID = "Terminal"
    private const val BRACKETED_PASTE_START = "\u001b[200~"
    private const val BRACKETED_PASTE_END = "\u001b[201~"
    private const val SUBMIT_KEY = "\r"

    private val logger = Logger.getInstance(MkClaudeTerminalSender::class.java)

    fun send(project: Project, terminalTabName: String, message: String): Boolean {
        val sendToTab = findSender(project, terminalTabName)
        if (sendToTab == null) {
            notifyTabNotFound(project, terminalTabName)
            return false
        }
        sendToTab(message)
        return true
    }

    private fun findSender(project: Project, terminalTabName: String): ((String) -> Unit)? {
        val toolWindow = ToolWindowManager.getInstance(project)
            .getToolWindow(TERMINAL_TOOL_WINDOW_ID) ?: return null
        val contents = toolWindow.contentManager.contents.toList()
        val matches = { tabTitle: String? -> tabTitleMatches(tabTitle, terminalTabName) }
        val classicWidget = findClassicWidget(contents, matches)
        if (classicWidget != null) return { message -> writeToTerminal(classicWidget, message) }
        return MkNewTerminalAccess.findSender(project, matches, contents)
    }

    private fun findClassicWidget(contents: List<Content>, matches: (String?) -> Boolean): TerminalWidget? {
        val content = contents.firstOrNull { matches(it.displayName) } ?: return null
        return TerminalToolWindowManager.findWidgetByContent(content)
    }

    private fun tabTitleMatches(tabTitle: String?, terminalTabName: String): Boolean {
        if (tabTitle == null) return false
        return tabTitle.contains(terminalTabName, ignoreCase = true)
    }

    private fun writeToTerminal(widget: TerminalWidget, message: String) {
        ApplicationManager.getApplication().executeOnPooledThread {
            try {
                val connector = widget.ttyConnector ?: return@executeOnPooledThread
                connector.write(BRACKETED_PASTE_START + message + BRACKETED_PASTE_END)
                connector.write(SUBMIT_KEY)
            } catch (exception: Exception) {
                logger.warn("Failed to write comments to the terminal", exception)
            }
        }
    }

    private fun notifyTabNotFound(project: Project, terminalTabName: String) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup(NOTIFICATION_GROUP_ID)
            .createNotification(
                "Terminal tab \"$terminalTabName\" not found. Comments were only copied to the clipboard.",
                NotificationType.WARNING
            )
            .notify(project)
    }

}
