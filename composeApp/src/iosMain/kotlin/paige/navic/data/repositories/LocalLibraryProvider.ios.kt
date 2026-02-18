package paige.navic.data.repositories

import paige.navic.data.models.LocalFolder
import paige.subsonic.api.models.Track

actual object LocalLibraryProvider {
	actual suspend fun getTracks(): List<Track> = emptyList()
	actual suspend fun getFolders(parentPath: String?): List<LocalFolder> = emptyList()
	actual suspend fun getTracksInFolder(folderPath: String): List<Track> = emptyList()
}
