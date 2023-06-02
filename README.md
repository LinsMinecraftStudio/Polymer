# Polymer
Polymer is a library for LMS plugins, also you can use it.
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
        <artifactId>polymer</artifactId>
        <version>VERSION</version>
        <scope>provided</scope>
    </dependency>
    <!-- add your dependencies -->
</dependencies>
```
### Gradle
```
repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.linsminecraftstudio:Polymer:VERSION'
}
```