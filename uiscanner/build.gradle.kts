/*
 * Copyright (c) 2022, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific prior
 * written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

plugins {
    alias(libs.plugins.nordic.feature)
    alias(libs.plugins.nordic.nexus)
    alias(libs.plugins.kotlin.parcelize)
}

group = "no.nordicsemi.android.kotlin.ble"

nordicNexusPublishing {
    POM_ARTIFACT_ID = "uiscanner"
    POM_NAME = "Nordic Kotlin library for BLE server side."

    POM_DESCRIPTION = "Nordic Android Kotlin BLE library"
    POM_URL = "https://github.com/NordicPlayground/Kotlin-BLE-Library"
    POM_SCM_URL = "https://github.com/NordicPlayground/Kotlin-BLE-Library"
    POM_SCM_CONNECTION = "scm:git@github.com:NordicPlayground/Kotlin-BLE-Library.git"
    POM_SCM_DEV_CONNECTION = "scm:git@github.com:NordicPlayground/Kotlin-BLE-Library.git"

    POM_DEVELOPER_ID = "syzi"
    POM_DEVELOPER_NAME = "Sylwester Zieliński"
    POM_DEVELOPER_EMAIL = "sylwester.zielinski@nordicsemi.no"
}

android {
    namespace = "no.nordicsemi.android.kotlin.ble.ui.scanner"
}

dependencies {
    api(project(":core"))
    implementation(project(":scanner"))

    implementation(libs.nordic.theme)
    implementation(libs.nordic.core)
    implementation(libs.nordic.permissions.ble)
    implementation(libs.accompanist.flowlayout)

    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material.iconsExtended)
}
