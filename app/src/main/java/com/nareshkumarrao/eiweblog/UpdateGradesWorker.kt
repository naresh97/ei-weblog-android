package com.nareshkumarrao.eiweblog

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class UpdateGradesWorker(private val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val gradesNotificationsEnabled = sharedPref?.getBoolean(context.getString(R.string.enable_grades_notifications_key), true)

        if (gradesNotificationsEnabled!!) {
            HISUtility.checkForUpdates(context) { gradeUpdates ->
                if (gradeUpdates != null) {
                    for (grade in gradeUpdates) {
                        HISUtility.sendNotification(context, grade, gradeUpdates.indexOf(grade))
                    }
                }
            }

        }
        return Result.success()
    }
}