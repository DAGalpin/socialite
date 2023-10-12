/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.samples.socialite.ui

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.google.android.samples.socialite.model.extractChatId
import com.google.android.samples.socialite.ui.camera.Camera
import com.google.android.samples.socialite.ui.camera.Media
import com.google.android.samples.socialite.ui.camera.MediaType
import com.google.android.samples.socialite.ui.chat.ChatScreen
import com.google.android.samples.socialite.ui.home.Home
import com.google.android.samples.socialite.ui.player.VideoPlayerScreen
import com.google.android.samples.socialite.ui.videoedit.VideoEditScreen

@Composable
fun Main(
    shortcutParams: ShortcutParams?,
) {
    SocialTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = "home",
        ) {
            composable(
                route = "home",
            ) {
                Home(
                    modifier = Modifier.fillMaxSize(),
                    onChatClicked = { chatId -> navController.navigate("chat/$chatId") },
                )
            }
            composable(
                route = "chat/{chatId}?text={text}",
                arguments = listOf(
                    navArgument("chatId") { type = NavType.LongType },
                    navArgument("text") { defaultValue = "" },
                ),
                deepLinks = listOf(
                    navDeepLink {
                        action = Intent.ACTION_VIEW
                        uriPattern = "https://socialite.google.com/chat/{chatId}"
                    },
                ),
            ) { backStackEntry ->
                val chatId = backStackEntry.arguments?.getLong("chatId") ?: 0L
                val text = backStackEntry.arguments?.getString("text")
                ChatScreen(
                    chatId = chatId,
                    foreground = true,
                    onBackPressed = { navController.popBackStack() },
                    onCameraClick = { navController.navigate("chat/$chatId/camera") },
                    onVideoClick = { uri -> navController.navigate("videoPlayer?uri=$uri") },
                    prefilledText = text,
                )
            }
            composable(
                route = "chat/{chatId}/camera",
                arguments = listOf(
                    navArgument("chatId") { type = NavType.LongType },
                ),
            ) { backStackEntry ->
                val chatId = backStackEntry.arguments?.getLong("chatId") ?: 0L
                Camera(
                    onMediaCaptured = { capturedMedia: Media? ->
                        when (capturedMedia?.mediaType) {
                            MediaType.PHOTO -> {
                                navController.popBackStack()
                            }

                            MediaType.VIDEO -> {
                                navController.navigate("videoEdit?uri=${capturedMedia.uri}&chatId=$chatId")
                            }

                            else -> {
                                // No media to use.
                                navController.popBackStack()
                            }
                        }
                    },
                    chatId = chatId,
                )
            }
            composable(
                route = "videoEdit?uri={videoUri}&chatId={chatId}",
                arguments = listOf(
                    navArgument("videoUri") { type = NavType.StringType },
                    navArgument("chatId") { type = NavType.LongType },
                ),
            ) { backStackEntry ->
                val chatId = backStackEntry.arguments?.getLong("chatId") ?: 0L
                val videoUri = backStackEntry.arguments?.getString("videoUri") ?: ""
                VideoEditScreen(
                    chatId = chatId,
                    uri = videoUri,
                    onCloseButtonClicked = { navController.popBackStack() },
                    navController = navController,
                )
            }
            composable(
                route = "videoPlayer?uri={videoUri}",
                arguments = listOf(
                    navArgument("videoUri") { type = NavType.StringType },
                ),
            ) { backStackEntry ->
                val videoUri = backStackEntry.arguments?.getString("videoUri") ?: ""
                VideoPlayerScreen(
                    uri = videoUri,
                    onCloseButtonClicked = { navController.popBackStack() },
                )
            }
        }

        if (shortcutParams != null) {
            val chatId = extractChatId(shortcutParams.shortcutId)
            val text = shortcutParams.text
            navController.navigate("chat/$chatId?text=$text")
        }
    }
}

data class ShortcutParams(
    val shortcutId: String,
    val text: String,
)