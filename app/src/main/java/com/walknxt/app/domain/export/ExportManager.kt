package com.walknxt.app.domain.export

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.walknxt.app.domain.model.WalkSession
import com.walknxt.app.data.local.entity.RoutePointEntity
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ExportManager(private val context: Context) {

    private val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }

    fun exportToCsv(sessions: List<WalkSession>): Uri? {
        val file = File(exportDir, "WalkNxt_Export_${System.currentTimeMillis()}.csv")
        try {
            file.printWriter().use { out ->
                out.println("SessionID,StartTime,EndTime,DurationMs,ActiveDurationMs,Steps,DistanceMeters,SpeedMps,Confidence,MeasurementMode")
                sessions.forEach { session ->
                    val endTime = session.endTime ?: ""
                    val speed = session.averageSpeed?.metersPerSecond ?: ""
                    out.println("${session.id},${session.startTime},${endTime},${session.totalDuration.millis},${session.activeDuration.millis},${session.stepCount},${session.distance.meters},${speed},${session.confidence.name},${session.measurementMode.name}")
                }
            }
            return FileProvider.getUriForFile(context, "com.walknxt.app.fileprovider", file)
        } catch (e: Exception) {
            return null
        }
    }

    fun exportToGpx(session: WalkSession, points: List<RoutePointEntity>): Uri? {
        val file = File(exportDir, "WalkNxt_${session.id}.gpx")
        try {
            val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            
            file.printWriter().use { out ->
                out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                out.println("<gpx version=\"1.1\" creator=\"WalkNxt\">")
                out.println("  <trk>")
                out.println("    <name>Walk ${df.format(Date(session.startTime))}</name>")
                out.println("    <trkseg>")
                points.forEach { pt ->
                    out.println("      <trkpt lat=\"${pt.latitude}\" lon=\"${pt.longitude}\">")
                    out.println("        <time>${df.format(Date(pt.timestamp))}</time>")
                    out.println("      </trkpt>")
                }
                out.println("    </trkseg>")
                out.println("  </trk>")
                out.println("</gpx>")
            }
            return FileProvider.getUriForFile(context, "com.walknxt.app.fileprovider", file)
        } catch (e: Exception) {
            return null
        }
    }

    fun clearExports() {
        exportDir.listFiles()?.forEach { it.delete() }
    }
}
