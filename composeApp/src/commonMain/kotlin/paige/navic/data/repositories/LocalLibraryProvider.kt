package paige.navic.data.repositories

import paige.navic.data.models.LocalFolder
import paige.subsonic.api.models.Track

expect object LocalLibraryProvider {
	suspend fun getTracks(): List<Track>
	suspend fun getFolders(parentPath: String? = null): List<LocalFolder>
	suspend fun getTracksInFolder(folderPath: String): List<Track>
}
