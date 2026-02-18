package paige.navic.ui.viewmodels

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import paige.navic.data.models.User
import paige.navic.data.session.SessionManager
import paige.navic.utils.LoginState

class LoginViewModel : ViewModel() {
	private val _loginState = MutableStateFlow<LoginState<User?>>(LoginState.LoggedOut)
	val loginState: StateFlow<LoginState<User?>> = _loginState.asStateFlow()

	val instanceState = TextFieldState()
	val usernameState = TextFieldState()
	val passwordState = TextFieldState()

	init {
		loadUser()
	}

	fun loadUser() {
		viewModelScope.launch {
			val user = SessionManager.currentUser
			if (user != null) {
				_loginState.value = LoginState.Success(user)
			} else {
				_loginState.value = LoginState.LoggedOut
			}
		}
	}

	fun login() {
		viewModelScope.launch {
			_loginState.value = LoginState.Loading
			_loginState.value = try {
				SessionManager.login(
					normalizeInstanceUrl(instanceState.text.toString()),
					usernameState.text.toString(),
					passwordState.text.toString()
				)
				if (SessionManager.currentUser != null) {
					LoginState.Success(SessionManager.currentUser)
				} else {
					throw Exception("currentUser is null")
				}
			} catch (e: Exception) {
				LoginState.Error(e)
			}
		}
	}

	private fun normalizeInstanceUrl(rawValue: String): String {
		val value = rawValue.trim().removeSuffix("/")
		if (value.startsWith("https://") || value.startsWith("http://")) {
			return value
		}

		val host = value.substringBefore('/').substringBefore(':').lowercase()
		val isLocalHost =
			host == "localhost" ||
			host == "127.0.0.1" ||
			host == "::1" ||
			host.endsWith(".local") ||
			host.startsWith("10.") ||
			host.startsWith("192.168.") ||
			host.matches(Regex("^172\\.(1[6-9]|2\\d|3[0-1])\\..*"))

		return if (isLocalHost) {
			"http://$value"
		} else {
			"https://$value"
		}
	}

	fun logout() {
		viewModelScope.launch {
			SessionManager.logout()
			_loginState.value = LoginState.LoggedOut
		}
	}
}
