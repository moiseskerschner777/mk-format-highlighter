package com.moiseskerschner.mkformat.comments

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager

object MkClaudeTerminalSender {

    private const val NOTIFICATION_GROUP_ID = "mk-format"
    private const val TERMINAL_PLUGIN_ID = "org.jetbrains.plugins.terminal"
    private const val TERMINAL_TOOL_WINDOW_ID = "Terminal"

    fun send(project: Project, terminalTabName: String, message: String): Boolean {
        if (!isTerminalPluginInstalled()) return false
        val sendToTab = findSender(project, terminalTabName)
        if (sendToTab == null) {
            notifyTabNotFound(project, terminalTabName)
            return false
        }
        sendToTab(message)
        return true
    }

    private fun isTerminalPluginInstalled(): Boolean {
        return PluginManagerCore.isPluginInstalled(PluginId.getId(TERMINAL_PLUGIN_ID))
    }

    private fun findSender(project: Project, terminalTabName: String): ((String) -> Unit)? {
        val toolWindow = ToolWindowManager.getInstance(project)
            .getToolWindow(TERMINAL_TOOL_WINDOW_ID) ?: return null
        val contents = toolWindow.contentManager.contents.toList()
        val matches = { tabTitle: String? -> tabTitleMatches(tabTitle, terminalTabName) }
        return MkClassicTerminalAccess.findSender(contents, matches)
            ?: MkNewTerminalAccess.findSender(project, matches, contents)
    }

    private fun tabTitleMatches(tabTitle: String?, terminalTabName: String): Boolean {
        if (tabTitle == null) return false
        return tabTitle.contains(terminalTabName, ignoreCase = true)
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
