package com.example.testbackend.helper

import android.content.ContentResolver
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.testbackend.ui.activity.ApplicationformActivity
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import android.provider.OpenableColumns
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.FileOutputStream
import java.io.InputStream


class ImagePickerHelper(private val activity: ApplicationformActivity) {
    private var imageUri: Uri? = null

    // Khởi tạo launcher để chọn ảnh từ thư viện và trả về Uri của ảnh đã chọn
    private val pickImageLauncher: ActivityResultLauncher<String> =
        activity.registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageUri = it
                currentImageView?.setImageURI(it)  // Đặt ảnh vào ImageView sau khi người dùng chọn ảnh
                imagePickedCallback?.invoke(it)  // Gọi callback để thông báo ảnh đã chọn
            }
        }

    // Lưu ImageView hiện tại và callback sau khi chọn ảnh
    private var currentImageView: ImageView? = null
    private var imagePickedCallback: ((Uri) -> Unit)? = null

    // Phương thức để mở trình chọn ảnh từ thư viện và đặt ảnh vào ImageView
    fun launchImagePickerAndSetImage(imageView: ImageView, onImagePicked: (Uri) -> Unit) {
        currentImageView = imageView
        imagePickedCallback = onImagePicked
        pickImageLauncher.launch("image/*")
    }

    // Phương thức lấy tên của ảnh từ Uri (nếu cần)
    fun getImageName(contentResolver: ContentResolver, uri: Uri): String? {
        var name: String? = null
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) {
                    name = it.getString(nameIndex)
                }
            }
        }
        return name
    }

    // Tạo MultipartBody.Part để gửi ảnh dưới dạng Multipart
    fun getMultipartImage(contentResolver: ContentResolver, uri: Uri, fieldName: String): MultipartBody.Part? {
        return try {
            val file = getFileFromUri(contentResolver, uri)
            val requestFile: RequestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(fieldName, file.name, requestFile)
        } catch (e: Exception) {
            null
        }
    }

    // Tạo File từ Uri và nén ảnh thành JPEG
    private fun getFileFromUri(contentResolver: ContentResolver, uri: Uri): File {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)

        val file = File(activity.cacheDir, getImageName(contentResolver, uri)?.replace(".png", ".jpg") ?: "temp_image.jpg")
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        }

        return file
    }
}
