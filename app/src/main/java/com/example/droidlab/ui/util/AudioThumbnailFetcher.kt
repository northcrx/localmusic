package com.example.droidlab.ui.util

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.util.Size
import coil.ImageLoader
import coil.decode.DataSource
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.request.Options
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AudioThumbnailFetcher(
    private val context: Context,
    private val data: Uri
) : Fetcher {

    override suspend fun fetch(): FetchResult? = withContext(Dispatchers.IO) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                val bitmap = context.contentResolver.loadThumbnail(data, Size(512, 512), null)
                return@withContext DrawableResult(
                    drawable = BitmapDrawable(context.resources, bitmap),
                    isSampled = false,
                    dataSource = DataSource.DISK
                )
            } catch (e: Exception) {
                // If loadThumbnail fails (e.g. albumart Uri or no generated thumbnail), try stream fallback
                try {
                    context.contentResolver.openInputStream(data)?.use { stream ->
                        val bitmap = BitmapFactory.decodeStream(stream)
                        if (bitmap != null) {
                            return@withContext DrawableResult(
                                drawable = BitmapDrawable(context.resources, bitmap),
                                isSampled = false,
                                dataSource = DataSource.DISK
                            )
                        }
                    }
                } catch (ignored: Exception) {}
                null
            }
        } else {
            try {
                context.contentResolver.openInputStream(data)?.use { stream ->
                    val bitmap = BitmapFactory.decodeStream(stream)
                    if (bitmap != null) {
                        return@withContext DrawableResult(
                            drawable = BitmapDrawable(context.resources, bitmap),
                            isSampled = false,
                            dataSource = DataSource.DISK
                        )
                    }
                }
                null
            } catch (e: Exception) {
                null
            }
        }
    }

    class Factory(private val context: Context) : Fetcher.Factory<Uri> {
        override fun create(data: Uri, options: Options, imageLoader: ImageLoader): Fetcher? {
            val scheme = data.scheme
            val auth = data.authority
            if (scheme == "content" && (auth == "media" || auth?.contains("media") == true)) {
                return AudioThumbnailFetcher(context, data)
            }
            return null
        }
    }
}
