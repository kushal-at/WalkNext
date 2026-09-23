package com.walknxt.app.core.utils

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object SetupHelper {
    
    suspend fun copyAssetsToInternalStorage(context: Context) = withContext(Dispatchers.IO) {
        // Copy the PMTiles file if it exists in assets
        val pmtilesName = "offline_map.pmtiles"
        val pmtilesFile = File(context.filesDir, pmtilesName)
        
        if (!pmtilesFile.exists()) {
            try {
                context.assets.open(pmtilesName).use { inputStream ->
                    FileOutputStream(pmtilesFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            } catch (e: Exception) {
                // File not found in assets, which is expected during development
                // until the user actually places it there.
            }
        }

        // Copy the style.json if it exists
        val styleName = "style.json"
        val styleFile = File(context.filesDir, styleName)
        if (!styleFile.exists()) {
            try {
                context.assets.open(styleName).use { inputStream ->
                    FileOutputStream(styleFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
    }
}
