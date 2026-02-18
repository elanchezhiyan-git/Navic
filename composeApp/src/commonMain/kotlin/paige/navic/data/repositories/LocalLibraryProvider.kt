package paige.navic.data.repositories

import paige.subsonic.api.models.Track

expect object LocalLibraryProvider {
	suspend fun getTracks(): List<Track>
}
