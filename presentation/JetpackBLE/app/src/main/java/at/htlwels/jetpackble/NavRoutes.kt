package at.htlwels.jetpackble

import kotlinx.serialization.Serializable

@Serializable
object ScreenHome

@Serializable
object ScreenScan

@Serializable
object ScreenP0

@Serializable
object ScreenP1

@Serializable
object ScreenP2

@Serializable
object ScreenP3

@Serializable
object ScreenP4

//in Project Structure - add dependency
//org.jetbrains.kotlinx:kotlinx-serialization-json

//libs.versions.toml - add [plugins]
//kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
//build.gradle.kts - add alias
//(libs.plugins.kotlin.serialization)