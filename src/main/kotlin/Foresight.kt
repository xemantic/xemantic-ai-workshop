/*
 * Copyright (c) 2026. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

package com.xemantic.ai.workshop

import com.xemantic.ai.anthropic.Anthropic

@Suppress("FunctionName")
fun Foresight() = Anthropic {
    apiKey = requireNotNull(System.getenv("FORESIGHT_API_KEY")) {
        "FORESIGHT_API_KEY environment variable not set"
    }
    baseUrl = "https://dev1.ycluster.net/v1/"
    defaultModel = "mlx-community/Kimi-K2.6-mlx-DQ3_K_M-q8"
    useXApiKeyHeader = false
    useAuthorizationBearerHeader = true
}
