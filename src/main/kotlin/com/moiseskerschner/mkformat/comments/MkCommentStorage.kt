package com.moiseskerschner.mkformat.comments

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.intellij.openapi.vfs.VirtualFile
import java.io.File

object MkCommentStorage {
    private val gson = Gson()

    fun save(file: VirtualFile, comments: List<MkComment>) {
        val sourceFile = File(file.path)
        val commentsDir = File(sourceFile.parentFile, ".mk-comments")
        if (!commentsDir.exists()) {
            commentsDir.mkdirs()
        }
        val jsonFile = File(commentsDir, "${sourceFile.name}.json")
        jsonFile.writeText(gson.toJson(comments))
    }

    fun load(file: VirtualFile): List<MkComment> {
        val sourceFile = File(file.path)
        val jsonFile = File(sourceFile.parentFile, ".mk-comments/${sourceFile.name}.json")
        if (!jsonFile.exists()) return emptyList()
        val type = object : TypeToken<List<MkComment>>() {}.type
        return gson.fromJson(jsonFile.readText(), type)
    }
}
