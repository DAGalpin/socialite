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

package com.google.android.samples.socialite.data

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.google.android.samples.socialite.BuildConfig
import javax.inject.Singleton

private val TAG = GenerativeAI::class.java.simpleName

@Singleton
class GenerativeAI {

    // select our generative model and configuration
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash-latest",
        apiKey = BuildConfig.GEMINI_KEY,
        generationConfig = generationConfig {
            temperature = 2.0f
            maxOutputTokens = 256
        },
    )

    // retrieve reasonable responses rapidly
    suspend fun getHelloAnimalResponse( animalName: String ) : String? {
        try {
            val response =
                generativeModel.generateContent(
                    "Write a short chat message that says hello using ${animalName}" +
                        "related puns, alliteration, and emoji")
            return response.text
        } catch (e: Exception) {
            Log.e(TAG, "getHelloAnimalResponse: ", e)
            return null
        }
    }
}
