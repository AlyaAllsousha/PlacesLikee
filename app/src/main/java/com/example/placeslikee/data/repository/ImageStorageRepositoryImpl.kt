package com.example.placeslikee.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.placeslikee.data.remote.claudinary.CloudinaryManager
import com.example.placeslikee.domain.repositories.ImageStorageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.net.toUri

@Singleton
class ImageStorageRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cloudinaryManager: CloudinaryManager
): ImageStorageRepository{
    override suspend fun saveImageLocally(uriString: String): String? {
        return withContext(Dispatchers.IO){
            try{
                val uri  = uriString.toUri()
                val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext null
                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()

                if(originalBitmap == null) return@withContext null
                val resizedBitmap  = scaleBitmapDown(originalBitmap, 1920)
                val fileName = "marker_${UUID.randomUUID()}.webp"
                val file = File(context.filesDir, fileName)
                val outputStream = FileOutputStream(file)
                val success = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R){
                    resizedBitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 80, outputStream)
                }
                else{
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                }
                outputStream.close()
                originalBitmap.recycle()
                if(resizedBitmap != originalBitmap){
                    resizedBitmap.recycle()
                }
                if(success) file.absolutePath else null
            }
            catch( e: Exception){
               e.printStackTrace()
               null
            }
        }
    }

    override suspend fun uploadImage(imageUriString: String): String {
        val uri = if (imageUriString.startsWith("/")){
            Uri.fromFile(File(imageUriString))
        } else{
            imageUriString.toUri()
        }
        return cloudinaryManager.uploadImage(uri)
    }

    private fun scaleBitmapDown(bitmap: Bitmap, maxDimension: Int): Bitmap{
        val originWidth = bitmap.width
        val originHeight = bitmap.height
        var resizedWidth = maxDimension
        var resizedHeight = maxDimension

        if(originHeight > originWidth){
            resizedHeight = maxDimension
            resizedWidth = (resizedHeight * originWidth.toFloat() / originHeight.toFloat()).toInt()
        } else if(originWidth > originHeight){
            resizedWidth = maxDimension
            resizedHeight = (resizedWidth * originHeight.toFloat() / originWidth.toFloat()).toInt()
        } else if( originWidth == originHeight){
            resizedWidth = maxDimension
            resizedHeight = maxDimension
        }
        if(originWidth <= maxDimension && originHeight <= maxDimension){
            return bitmap
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false)
    }
}