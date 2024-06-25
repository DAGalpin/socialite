/*
 * Copyright (C) 2024 The Android Open Source Project
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

package com.google.android.samples.socialite.widget

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.core.net.toUri
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.mutableActionParametersOf
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Box
import com.google.android.samples.socialite.BuildConfig
import com.google.android.samples.socialite.MainActivity
import com.google.android.samples.socialite.repository.ChatRepository
import com.google.android.samples.socialite.widget.model.WidgetModel
import com.google.android.samples.socialite.widget.model.WidgetModelRepository
import com.google.android.samples.socialite.widget.model.WidgetState.Empty
import com.google.android.samples.socialite.widget.model.WidgetState.Loading
import com.google.android.samples.socialite.widget.ui.FavoriteContact
import com.google.android.samples.socialite.widget.ui.ZeroState
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val contactKey = ActionParameters.Key<Long>("contactId")

class SociaLiteAppWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val widgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)
        val repository = WidgetModelRepository.get(context)

        provideContent {
            GlanceTheme {
                Content(repository, widgetId)
            }
        }
    }

    @Composable
    private fun Content(repository: WidgetModelRepository, widgetId: Int) {
        val model = repository.loadModel(widgetId).collectAsState(Loading).value
        val context = LocalContext.current
        when (model) {
            is Empty -> ZeroState(widgetId = widgetId)
            is Loading -> Box {}
            is WidgetModel -> {
                FavoriteContact(
                    model = model,
                    onClick = actionStartActivity(
                        Intent(context.applicationContext, MainActivity::class.java)
                            .setAction(Intent.ACTION_VIEW)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            .setData("https://socialite.google.com/chat/${model.contactId}".toUri()),
                    ),
                )
            }
        }
    }
}


@EntryPoint
@InstallIn(SingletonComponent::class)
interface ChatEntryPoint {
    fun getChatRepository(): ChatRepository
}
class SendAIMessage : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            ChatEntryPoint::class.java
        )
        val chatRepository = hiltEntryPoint.getChatRepository()
//        if (BuildConfig.DEBUG) {
//            text?.let {
//                // only use this for debugging...
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(context, "Sent: ${it}", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
    }
}

