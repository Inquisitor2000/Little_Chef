pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "FamilyMealPlanner"
include(":app")
include(":2fast_2hungry_pack")
include(":eastern_traditional_pack")
include(":exotic_tropics_pack")
