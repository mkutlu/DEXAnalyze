package com.aarw.dexanalyze.ui.screens.login

import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import java.io.ByteArrayInputStream
import java.util.concurrent.atomic.AtomicBoolean
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is LoginUiState.ShowWebView -> {
            BackHandler { viewModel.cancelAuth() }
            AuthWebView(
                authUrl = state.authUrl,
                onCodeReceived = { code -> viewModel.handleCode(code, state.codeVerifier) },
                onCancel = { viewModel.cancelAuth() }
            )
        }
        else -> LoginLandingScreen(uiState = uiState, onSignIn = { viewModel.startAuth() })
    }
}

@Composable
private fun LoginLandingScreen(uiState: LoginUiState, onSignIn: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f))

            Text(
                "DEXAnalyze",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "DEXA Scan Insights",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(40.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Connect your BodySpec account to view your DEXA scan results and body composition trends.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                if (uiState is LoginUiState.Error) {
                    Text(
                        uiState.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }

                if (uiState is LoginUiState.Loading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Text(
                        "Completing sign-in…",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Button(
                        onClick = onSignIn,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Sign in with BodySpec", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Text(
                "You'll be taken to BodySpec's login page. Sign in with Google or your BodySpec account.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AuthWebView(
    authUrl: String,
    onCodeReceived: (String) -> Unit,
    onCancel: () -> Unit
) {
    val codeHandled = remember { AtomicBoolean(false) }
    val mainHandler = remember { Handler(Looper.getMainLooper()) }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true

                webViewClient = object : WebViewClient() {
                    // Handles client-side navigation (links, window.location)
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean = checkAndCapture(request?.url)

                    // Handles ALL requests including POST-redirect chains —
                    // runs on a background thread, so dispatch code back to main.
                    override fun shouldInterceptRequest(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): WebResourceResponse? {
                        val uri = request?.url ?: return null
                        if (isRedirectUrl(uri)) {
                            val code = uri.getQueryParameter("code")
                            if (code != null && codeHandled.compareAndSet(false, true)) {
                                mainHandler.post { onCodeReceived(code) }
                            }
                            return WebResourceResponse(
                                "text/html", "UTF-8",
                                ByteArrayInputStream(ByteArray(0))
                            )
                        }
                        return null
                    }

                    private fun checkAndCapture(uri: Uri?): Boolean {
                        uri ?: return false
                        if (!isRedirectUrl(uri)) return false
                        val code = uri.getQueryParameter("code")
                        if (code != null && codeHandled.compareAndSet(false, true)) {
                            onCodeReceived(code)
                        }
                        return true
                    }

                    private fun isRedirectUrl(uri: Uri) =
                        uri.host == "app.bodyspec.com" && uri.path?.startsWith("/docs") == true
                }

                loadUrl(authUrl)
            }
        }
    )
}
