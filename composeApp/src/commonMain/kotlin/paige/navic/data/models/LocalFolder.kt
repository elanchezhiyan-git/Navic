package paige.navic.data.models

import kotlinx.serialization.Serializable

@Serializable
data class LocalFolder(
	val path: String,
	val name: String,
	val trackCount: Int
)
