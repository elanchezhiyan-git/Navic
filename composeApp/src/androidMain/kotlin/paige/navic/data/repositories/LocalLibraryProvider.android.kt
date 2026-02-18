package paige.navic.data.repositories

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import paige.navic.MainActivity
import paige.subsonic.api.models.Track

actual object LocalLibraryProvider {
	actual suspend fun getTracks(): List<Track> = withContext(Dispatchers.IO) {
		val context = MainActivity.appContext ?: return@withContext emptyList()
		val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
			Manifest.permission.READ_MEDIA_AUDIO
		else Manifest.permission.READ_EXTERNAL_STORAGE

		if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
			return@withContext emptyList()
		}

		val projection = arrayOf(
			MediaStore.Audio.Media._ID,
			MediaStore.Audio.Media.TITLE,
			MediaStore.Audio.Media.ARTIST,
			MediaStore.Audio.Media.ALBUM,
			MediaStore.Audio.Media.DURATION,
			MediaStore.Audio.Media.MIME_TYPE
		)
		val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
		val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

		runCatching {
			context.contentResolver.query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				projection,
				selection,
				null,
				sortOrder
			)?.use { cursor ->
				val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
				val titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
				val artistIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
				val albumIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
				val durationIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
				val mimeTypeIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)

				buildList {
					while (cursor.moveToNext()) {
						val id = cursor.getLong(idIndex)
						val title = cursor.getString(titleIndex) ?: "Unknown"
						val artist = cursor.getString(artistIndex)
						val album = cursor.getString(albumIndex)
						val durationMs = cursor.getLong(durationIndex)
						val mimeType = cursor.getString(mimeTypeIndex)
						val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
							.buildUpon()
							.appendPath(id.toString())
							.build()
						add(
							Track(
								id = uri.toString(),
								parent = null,
								isDir = false,
								title = title,
								album = album,
								artist = artist,
								track = null,
								year = null,
								genre = null,
								coverArt = null,
								size = null,
								contentType = mimeType,
								suffix = null,
								transcodedContentType = null,
								transcodedSuffix = null,
								duration = (durationMs / 1000).toInt(),
								bitRate = null,
								bitDepth = null,
								samplingRate = null,
								channelCount = null,
								path = uri.toString(),
								isVideo = false,
								userRating = null,
								averageRating = null,
								playCount = null,
								discNumber = null,
								created = null,
								starred = null,
								albumId = null,
								artistId = null,
								type = "music",
								mediaType = "song",
								bookmarkPosition = null,
								originalWidth = null,
								originalHeight = null,
								played = null,
								bpm = null,
								comment = null,
								sortName = null,
								musicBrainzId = null,
								isrc = null,
								genres = null,
								artists = null,
								displayArtist = artist ?: "Unknown Artist",
								albumArtists = null,
								displayAlbumArtist = artist ?: "Unknown Artist",
								contributors = null,
								displayComposer = "",
								moods = null,
								replayGain = null,
								explicitStatus = null
							)
						)
					}
				}
			} ?: emptyList()
		}.getOrElse { emptyList() }
	}
}
