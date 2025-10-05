pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }

    // DECLARAÇÃO DE PLUGINS:
    plugins {
        // Corrigido: Usando ID direto com versão em vez de 'libs'
        id("com.android.application") version "8.5.1" apply false // Versão do Android Gradle Plugin

        // PLUGIN DO GOOGLE SERVICES (corrigido da etapa anterior)
        id("com.google.gms.google-services") version "4.4.2" apply false
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "CorteDeEliteApp"
include(":app")
