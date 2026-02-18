package paige.navic.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import navic.composeapp.generated.resources.Res
import navic.composeapp.generated.resources.action_add_all_to_playlist
import navic.composeapp.generated.resources.title_create_playlist
import navic.composeapp.generated.resources.action_play
import navic.composeapp.generated.resources.title_songs
import org.jetbrains.compose.resources.stringResource
import paige.navic.LocalContentPadding
import paige.navic.LocalMediaPlayer
import paige.navic.LocalNavStack
import paige.navic.data.models.LocalFolder
import paige.navic.data.models.Screen
import paige.navic.data.repositories.LocalLibraryProvider
import paige.navic.ui.components.common.TrackRow
import paige.navic.ui.components.layouts.NestedTopBar
import paige.subsonic.api.models.LocalTrackCollection
import paige.subsonic.api.models.Track
import kotlinx.coroutines.launch

@Composable
fun LocalFoldersScreen(path: String? = null, title: String = "Local Library") {
	val backStack = LocalNavStack.current
	val player = LocalMediaPlayer.current
	val scope = rememberCoroutineScope()
	var folders by remember(path) { mutableStateOf<List<LocalFolder>>(emptyList()) }
	var tracks by remember(path) { mutableStateOf<List<Track>>(emptyList()) }

	LaunchedEffect(path) {
		folders = LocalLibraryProvider.getFolders(path)
		tracks = if (path.isNullOrBlank()) emptyList() else LocalLibraryProvider.getTracksInFolder(path)
	}

	Scaffold(
		topBar = {
			NestedTopBar(title = { Text(title) })
		}
	) {
		LazyColumn(
			modifier = Modifier.padding(it),
			contentPadding = PaddingValues(
				start = 16.dp,
				end = 16.dp,
				top = 12.dp,
				bottom = LocalContentPadding.current.calculateBottomPadding()
			),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			if (path.isNullOrBlank()) {
				item {
					Text(
						text = stringResource(Res.string.title_songs),
						style = MaterialTheme.typography.titleMedium
					)
				}
			}

			items(folders, key = { it.path }) { folder ->
				Column(
					modifier = Modifier.fillMaxWidth()
				) {
					Button(
						modifier = Modifier.fillMaxWidth(),
						onClick = {
							backStack.add(Screen.LocalFolders(folder.path, folder.name))
						}
					) {
						Text("${folder.name} (${folder.trackCount})")
					}
					Spacer(Modifier.height(4.dp))
					Button(
						modifier = Modifier.fillMaxWidth(),
						onClick = {
							scope.launch {
								backStack.add(
									Screen.CreatePlaylist(
										LocalLibraryProvider.getTracksInFolder(folder.path)
									)
								)
							}
						}
					) {
						Text(stringResource(Res.string.title_create_playlist))
					}
					Button(
						modifier = Modifier.fillMaxWidth(),
						onClick = {
							scope.launch {
								backStack.add(
									Screen.AddToPlaylist(
										LocalLibraryProvider.getTracksInFolder(folder.path)
									)
								)
							}
						}
					) {
						Text(stringResource(Res.string.action_add_all_to_playlist))
					}
				}
			}

			if (tracks.isNotEmpty()) {
				item {
					Button(
						modifier = Modifier.fillMaxWidth(),
						onClick = {
							player.play(
								LocalTrackCollection(
									title = title,
									tracks = tracks,
									trackCount = tracks.size
								),
								0
							)
						}
					) { Text(stringResource(Res.string.action_play)) }
				}
				items(tracks, key = { it.id }) { track ->
					TrackRow(track = track)
				}
			}
		}
	}
}
