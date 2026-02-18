package paige.subsonic.api.models

import kotlinx.serialization.Serializable

@Serializable
data class LocalTrackCollection(
	override val id: String = "local-library",
	override val title: String = "Local Library",
	override val subtitle: String? = null,
	override val coverArt: String? = null,
	override val duration: Int? = null,
	override val year: Int? = null,
	override val genre: String? = null,
	override val trackCount: Int? = null,
	override val tracks: List<Track> = emptyList(),
	override val artistId: String? = null,
) : TrackCollection
