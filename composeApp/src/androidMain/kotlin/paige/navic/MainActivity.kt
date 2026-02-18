package paige.navic

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.ktor.client.plugins.cache.storage.FileStorage
import paige.navic.data.session.SessionManager
import java.io.File

class MainActivity : ComponentActivity() {
	companion object {
		var appContext: android.content.Context? = null
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		appContext = applicationContext
		requestMediaPermissionIfNeeded()
		SessionManager.cacheStorage = FileStorage(File(cacheDir, "http_cache"))
		setContent { App() }
	}

	private fun requestMediaPermissionIfNeeded() {
		val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			Manifest.permission.READ_MEDIA_AUDIO
		} else {
			Manifest.permission.READ_EXTERNAL_STORAGE
		}

		if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, arrayOf(permission), 1001)
		}
	}
}
