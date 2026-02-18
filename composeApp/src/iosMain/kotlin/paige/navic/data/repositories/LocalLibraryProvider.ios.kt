package paige.navic.data.repositories

import paige.subsonic.api.models.Track

actual object LocalLibraryProvider {
	actual suspend fun getTracks(): List<Track> = emptyList()
}
