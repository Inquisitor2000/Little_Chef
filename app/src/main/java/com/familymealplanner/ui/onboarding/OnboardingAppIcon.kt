package com.familymealplanner.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.familymealplanner.R

@Composable
fun OnboardingAppIcon(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val appIcon = remember {
        ContextCompat.getDrawable(context, R.mipmap.ic_launcher)
            ?.toBitmap()
            ?.asImageBitmap()
    }
    
    Box(
        modifier = modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // App icon
        appIcon?.let { bitmap ->
            Surface(
                modifier = Modifier.size(160.dp),
                shape = CircleShape,
                shadowElevation = 8.dp
            ) {
                Image(
                    bitmap = bitmap,
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }
        }
    }
}
