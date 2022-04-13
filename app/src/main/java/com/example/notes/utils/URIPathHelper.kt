package com.example.notes.utils

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min


object URIPathHelper {
    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.<br></br>
     * <br></br>
     * Callers should check whether the path is local before assuming it
     * represents a local file.
     *
     * @param ctx The context.
     * @param uri     The Uri to query.
     */
    @SuppressLint("NewApi")
    fun getPath(ctx: Context, uri: Uri): String? {
        var selection: String? = null
        var selectionArgs: Array<String>? = null
        // DocumentProvider
        try {
            if (DocumentsContract.isDocumentUri(ctx, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    val fullPath = getPathFromExtSD(split)
                    return if (fullPath !== "") {
                        fullPath
                    } else {
                        null
                    }
                } else if (isDownloadsDocument(uri)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        var cursor: Cursor? = null
                        try {
                            cursor = ctx.contentResolver.query(
                                uri,
                                arrayOf(MediaStore.MediaColumns.DISPLAY_NAME),
                                null,
                                null,
                                null
                            )
                            if (cursor != null && cursor.moveToFirst()) {
                                val fileName = cursor.getString(0)
                                val path = Environment.getExternalStorageDirectory()
                                    .toString() + "/Download/" + fileName
                                if (!TextUtils.isEmpty(path)) {
                                    return path
                                }
                            }
                        } finally {
                            cursor?.close()
                        }
                        val id: String = DocumentsContract.getDocumentId(uri)
                        if (!TextUtils.isEmpty(id)) {
                            if (id.startsWith("raw:")) {
                                return id.replaceFirst("raw:".toRegex(), "")
                            }
                            val contentUriPrefixesToTry = arrayOf(
                                "content://downloads/public_downloads",
                                "content://downloads/my_downloads"
                            )
                            for (contentUriPrefix in contentUriPrefixesToTry) {
                                return try {
                                    val contentUri = ContentUris.withAppendedId(
                                        Uri.parse(contentUriPrefix),
                                        java.lang.Long.valueOf(id)
                                    )

                                    // final Uri contentUri = ContentUris.withAppendedId(
                                    //        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                                    getDataColumn(ctx, contentUri, null, null)
                                } catch (e: NumberFormatException) {
                                    // In Android 8 and Android P the id is not a number
                                    uri.path!!.replaceFirst("^/document/raw:".toRegex(), "")
                                        .replaceFirst("^raw:".toRegex(), "")
                                }
                            }
                        }
                    } else {
                        val id = DocumentsContract.getDocumentId(uri)
                        var contentUri: Uri? = null
                        if (id.startsWith("raw:")) {
                            return id.replaceFirst("raw:".toRegex(), "")
                        }
                        try {
                            contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"),
                                java.lang.Long.valueOf(id)
                            )
                        } catch (e: NumberFormatException) {
                            e.printStackTrace()
                        }
                        if (contentUri != null) {
                            return getDataColumn(ctx, contentUri, null, null)
                        }
                    }
                } else if (isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    val type = split[0]
                    var contentUri: Uri = uri
                    when (type) {
                        "image" -> {
                            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        }
                        "video" -> {
                            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        }
                        "audio" -> {
                            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        }else ->{
                            return uri.path
                        }
                    }
                    selection = "_id=?"
                    selectionArgs = arrayOf(split[1])
                    return getDataColumn(
                        ctx, contentUri, selection,
                        selectionArgs
                    )
                } else if (isGoogleDriveUri(uri)) {
                    return getDriveFilePath(uri, ctx)
                }

            } else if ("content".equals(uri.scheme, ignoreCase = true)) {
                if (isGooglePhotosUri(uri)) {
                    return uri.lastPathSegment
                }
                if (isGoogleDriveUri(uri)) {
                    return getDriveFilePath(uri, ctx)
                }
                return if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
                    // return getFilePathFromURI(context,uri);
                    getMediaFilePathForN(uri, ctx)
                    // return getRealPathFromURI(context,uri);
                } else {
                    getDataColumn(ctx, uri, null, null)
                }
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path
            }
        } catch (e: Exception) {
            Log.e("URIPathHelper", "")
        }
        return null
    }

    /**
     * Check if a file exists on device
     *
     * @param filePath The absolute file path
     */
    private fun fileExists(filePath: String): Boolean {
        val file = File(filePath)
        return file.exists()
    }


    /**
     * Get full file path from external storage
     *
     * @param pathData The storage type and the relative path
     */
    private fun getPathFromExtSD(pathData: Array<String>): String {
//        val type = split[0]
//        if ("primary".equals(type, ignoreCase = true)) {
//            return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
        val type = pathData[0]
        val relativePath = "/" + pathData[1]
        var fullPath = ""
        if ("primary".equals(type, ignoreCase = true)) {
            fullPath = Environment.getExternalStorageDirectory().toString() + relativePath
            if (fileExists(fullPath)) {
                return fullPath
            }
        }

        // Environment.isExternalStorageRemovable() is `true` for external and internal storage
        // so we cannot relay on it.
        //
        // instead, for each possible path, check if file exists
        // we'll start with secondary storage as this could be our (physically) removable sd card
        fullPath = System.getenv("SECONDARY_STORAGE") + relativePath
        if (fileExists(fullPath)) {
            return fullPath
        }
        fullPath = System.getenv("EXTERNAL_STORAGE") + relativePath
        return if (fileExists(fullPath)) {
            fullPath
        } else fullPath
    }

    @SuppressLint("TimberArgCount")
    private fun getDriveFilePath(uri: Uri, context: Context): String? {
        val contentResolver = context.contentResolver
        val returnCursor = contentResolver.query(uri, null, null, null, null)

        // Get the column indexes of the data in the Cursor,
        // move to the first row in the Cursor, get the data,
        // and display it.
        val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        val file = File(context.cacheDir, name)
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            var read = 0
            val maxBufferSize = 1 * 1024 * 1024
            val bytesAvailable: Int = inputStream!!.available()

            // int bufferSize = 1024;
            val bufferSize = min(bytesAvailable, maxBufferSize)
            val buffers = ByteArray(bufferSize)
            while (inputStream.read(buffers).also { read = it } != -1) {
                outputStream.write(buffers, 0, read)
            }
            inputStream.close()
            outputStream.close()
        } catch (e: Exception) {
            Log.e("URIPathHelper","",e)
        }
        return file.path
    }

    @SuppressLint("TimberArgCount")
    private fun getMediaFilePathForN(uri: Uri, context: Context): String? {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)

        // Get the column indexes of the data in the Cursor,
        // move to the first row in the Cursor, get the data,
        // and display it.
        val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        val size = returnCursor.getLong(sizeIndex).toString()
        val file = File(context.filesDir, name)
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            var read = 0
            val maxBufferSize = 1 * 1024 * 1024
            val bytesAvailable: Int? = inputStream?.available()

            //int bufferSize = 1024;
            val bufferSize = bytesAvailable?.let { Math.min(it, maxBufferSize) }
            val buffers = bufferSize?.let { ByteArray(it) }
            while (inputStream?.read(buffers).also {
                    if (it != null) {
                        read = it
                    }
                } != -1) {
                outputStream.write(buffers, 0, read)
            }
            inputStream?.close()
            outputStream.close()
        } catch (e: Exception) {
            Log.e("URIPathHelper","",e)
        }
        return file.path
    }


    private fun getDataColumn(
        context: Context, uri: Uri,
        selection: String?, selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(
                uri, projection,
                selection, selectionArgs, null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } catch (e: Exception) {
            Log.e("URIPathHelper","",e)
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri - The Uri to check.
     * @return - Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri - The Uri to check.
     * @return - Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri - The Uri to check.
     * @return - Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri - The Uri to check.
     * @return - Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Drive.
     */
    private fun isGoogleDriveUri(uri: Uri): Boolean {
        return "com.google.android.apps.docs.storage" == uri.authority || "com.google.android.apps.docs.storage.legacy" == uri.authority
    }

    /**
     * Copy in cache.
     *
     * @param ctx Ctx
     * @param uri Uri
     * @param fullPath Full path
     * @return the path of file copied in cache
     */
    fun copyInCache(ctx: Context, uri: Uri): String? {
        val fullPath: String? = getPath(ctx, uri)
        return try {
            val file = File(ctx.cacheDir, fullPath?.split("/")?.last())
            val parcelFileDescriptor =
                ctx.contentResolver.openFileDescriptor(uri, "r", null)
            parcelFileDescriptor?.let {
                val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                val outputStream = FileOutputStream(file)
//                IOUtils.copy(inputStream, outputStream)
            }
            file.path
        } catch (e: Exception) {
            Log.e("URIPathHelper", "", e)
            fullPath
        }
        fullPath
    }

    fun getBitmap(ctx: Context, uri: Uri): Bitmap {
        var file = copyInCache(ctx, uri)
        return BitmapFactory.decodeFile(file)
    }
    /** Create a File for saving an image or video  */
    @RequiresApi(Build.VERSION_CODES.R)
    private fun getOutputMediaFile(ctx: Context): File? {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        val mediaStorageDir = File(
            Environment.getStorageDirectory().absolutePath + "/Android/data/"
                    + ctx.packageName
                    + "/Files"
        )

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null
            }
        }
        // Create a media file name
        val timeStamp: String = SimpleDateFormat("ddMMyyyy_HHmm").format(Date())
        val mediaFile: File
        val mImageName = "MI_$timeStamp.jpg"
        mediaFile = File(mediaStorageDir.path + File.separator.toString() + mImageName)
        return mediaFile
    }

    fun saveToInternalStorage(ctx: Context, uri: Uri): String? {
//        var image = BitmapFactory.decodeFile(getPath(ctx, uri))
//        val pictureFile: File? = getOutputMediaFile(ctx)
//        if (pictureFile == null) {
//            Log.d(
//                "TAG",
//                "Error creating media file, check storage permissions: "
//            ) // e.getMessage());
//            return null
//        }
//        try {
//            val fos = FileOutputStream(pictureFile)
//            image.compress(Bitmap.CompressFormat.PNG, 90, fos)
//            fos.close()
//            return  pictureFile.absolutePath
//        } catch (e: FileNotFoundException) {
//            Log.d("TAG", "File not found: " + e.toString())
//        } catch (e: IOException) {
//            Log.d("TAG", "Error accessing file: " + e.toString())
//        }
//        return null
        return getPath(ctx, uri)

            }
//
    fun loadImageFromStorage(path: String)=
         BitmapFactory.decodeStream(FileInputStream(File(path)))

}
