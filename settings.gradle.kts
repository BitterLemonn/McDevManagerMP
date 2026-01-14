rootProject.name = "MCDevManagerMP"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        // 阿里云 Maven 镜像（优先）
        maven("https://maven.aliyun.com/repository/public") {
            mavenContent {
                excludeGroupAndSubgroups("com.github")
                excludeGroupAndSubgroups("jitpack")
            }
        }
        // Maven Central（备用）
        mavenCentral()
        // Google 的 Maven 仓库（备用）
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        gradlePluginPortal()
        maven { setUrl("https://jitpack.io") }
    }
}

dependencyResolutionManagement {
    repositories {
        // Maven Central（备用）
        mavenCentral()
        // 阿里云 Maven 镜像（优先）
        maven("https://maven.aliyun.com/repository/public") {
            mavenContent {
                excludeGroupAndSubgroups("io.github")
                excludeGroupAndSubgroups("com.github")
                excludeGroupAndSubgroups("jitpack")
            }
        }

        // Google 的 Maven 仓库（备用）
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        gradlePluginPortal()
        maven { setUrl("https://jitpack.io") }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":composeApp")
include(":androidApp")