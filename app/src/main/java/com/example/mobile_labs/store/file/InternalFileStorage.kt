package com.example.mobile_labs.store.file

import android.content.Context
import android.util.Log
import com.example.mobile_labs.model.disney.DisneyCharacter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class InternalBackupManager(private val context: Context) {

    private val backupFileName = "backup_copy.txt"

    fun saveInternalBackup(originalFile: File) {
        val internalFile = File(context.filesDir, backupFileName)
        originalFile.copyTo(internalFile, overwrite = true)
    }

    fun backupExists(): Boolean {
        val internalFile = File(context.filesDir, backupFileName)
        return internalFile.exists()
    }

    fun restoreExternal(): Boolean {
        val internalFile = File(context.filesDir, backupFileName)
        if (!internalFile.exists()) return false

        val externalDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOCUMENTS)
        val externalFile = File(externalDir, "disney_backup.txt")
        internalFile.copyTo(externalFile, overwrite = true)
        return true
    }
}
