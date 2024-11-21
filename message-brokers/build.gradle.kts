plugins {
    id("java")
    id("io.freefair.lombok") version "8.4"
    id("me.champeau.jmh") version "0.6.6"
}

group = "org.tbank"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("com.rabbitmq:amqp-client:5.22.0")
    testImplementation("org.openjdk.jmh:jmh-core:1.37")
    testImplementation("org.openjdk.jmh:jmh-generator-annprocess:1.37")
    implementation("org.openjdk.jmh:jmh-core:1.37")
    annotationProcessor("org.openjdk.jmh:jmh-generator-annprocess:1.37")

    implementation("org.apache.kafka:kafka-clients:3.9.0")

    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("ch.qos.logback:logback-classic:1.5.8")
    implementation("ch.qos.logback:logback-core:1.5.8")
    compileOnly("org.projectlombok:lombok")
}

tasks.test {
    useJUnitPlatform()
}

// Задача для сборки JMH тестов
tasks.register<JavaCompile>("jmhCompile") {
    source = fileTree("src/main/java")
    include("**/*.java")

    // Использую зависимости runtimeClasspath
    classpath = sourceSets.main.get().runtimeClasspath

    // Задание директории
    destinationDirectory.set(file("build/classes/jmh"))

    // Указываю компилятору использовать аннотации
    options.compilerArgs = listOf("-proc:only")
}


// Задача для запуска JMH тестов
tasks.register<JavaExec>("runJmh") {
    dependsOn("jmhCompile")
    mainClass.set("org.openjdk.jmh.Main")
    classpath = files("build/classes/jmh") + sourceSets.main.get().runtimeClasspath

    jvmArgs = listOf(
        "-Dorg.slf4j.simpleLogger.defaultLogLevel=warn",
        "-Dorg.slf4j.simpleLogger.log.org.apache.kafka=warn",
        "-Dorg.slf4j.simpleLogger.log.kafka=warn"
    )

    args = listOf(
        "-o", "kafka_benchmark_report.txt",
        "-f", "1",                            // 1 прогон
        "-i", "5",                            // 5 итераций измерения
        "-wi", "2"                            // 2 итерации прогрева
    )
}
