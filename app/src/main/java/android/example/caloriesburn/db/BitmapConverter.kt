package android.example.caloriesburn.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class BitmapConverter {

     @TypeConverter
    fun fromBitmap(btmp: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        btmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun toBitmap(bArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bArray, 0, bArray.size)
    }
}
