# Polymer
Polymer is a library for plugins from Lins Minecraft Studio, and you can also use it
(of course, this is more suitable for lazy developers, like me).  
Discord: https://discord.gg/W36MJhBtGy

## Modules Description

scheduler: For scheduling tasks(make bukkit and folia compatibility)
plugin: Just the plugin
core: The core of the library
objectutils: Some small utilities that don't need paper-api

## Installation
### Maven
```
<repositories>
    <repository>
        <id>sonatype</id>
        <url>https://s01.oss.sonatype.org/content/repositories/snapshots/</url>
    </repository>
    <!-- add your repositories -->
</repositories>

<dependencies>
    <dependency>
        <groupId>io.github.linsminecraftstudio</groupId>
        <artifactId>Polymer-Core</artifactId>
        <version>VERSION</version>
        <scope>provided</scope>
    </dependency>
    <!-- add your dependencies -->
</dependencies>
```
### Gradle
```
repositories {
    maven {
      url 'https://s01.oss.sonatype.org/content/repositories/snapshots/'
    }
}

dependencies {
    implementation 'io.github.linsminecraftstudio:Polymer:VERSION'
}
```