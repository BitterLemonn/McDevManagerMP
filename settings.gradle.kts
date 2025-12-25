rootProject.name = "MCDevManagerMP"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        // 阿里云 Maven 镜像（优先）
        maven("https://maven.aliyun.com/repository/public")
        // Google 的 Maven 仓库（备用）
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        // Maven Central（备用）
        mavenCentral()
        gradlePluginPortal()
        maven { setUrl("https://jitpack.io")  }
    }
}

dependencyResolutionManagement {
    repositories {
        // 阿里云 Maven 镜像（优先）
        maven("https://maven.aliyun.com/repository/public")
        // Google 的 Maven 仓库（备用）
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        // Maven Central（备用）
        mavenCentral()
        gradlePluginPortal()
        maven { setUrl("https://jitpack.io")  }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":composeApp")