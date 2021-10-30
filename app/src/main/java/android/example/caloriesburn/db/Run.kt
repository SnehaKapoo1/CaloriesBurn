package android.example.caloriesburn.db

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "running_table")
data class Run(
    var img: Bitmap? = null,
    var timestamp: Long = 0L,
    var avgSpeedInKMH: Float = 0f,
    var distanceInMaters: Int = 0,
    var timeInMills: Long = 0L,
    var caloriesBurned: Int = 0
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
