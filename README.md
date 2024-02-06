# Описание
Созданный плагин использует `ANTLR4` (v 4.7.1) с автоматически сгенерированными грамматиками под `Kotlin` и `Java`.
# Использование
```
./gradlew getProjectInfo
```

По умолчанию отчет об анализе сохраняется в папку build корневого проекта.
```
GradlePlugin
├──────────build
│   └─────────projectInfo
│       └───────statistic.json
```
## Пример 
```javascript
{
  "lineCount" : 8,
  "functionCount" : 0,
  "classCount" : 1
}
```
## Основная логика плагина
```kotlin
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger

class MyPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    target.tasks.register("getProjectInfo") {
      val parseListener = KotlinParserListenerImpl()
      val lineCounter = AtomicInteger()

      File("${target.projectDir}/src").walk()
        .filter(File::isFile)
        .filter { it.extension == "kt" }
        .forEach {
          lineCounter.addAndGet(it.readLines().size)
          val walker = ParseTreeWalker()
          val parser = KotlinParser(CommonTokenStream(KotlinLexer(CharStreams.fromString(it.readText()))))
          val tree = parser.kotlinFile()
          walker.walk(parseListener, tree)
        }
      with(parseListener) {
        val statistic = CodeStats(lineCounter.get(), classCounter.get(), functionCounter.get())

        try {
          if (!target.buildDir.exists()) {
            target.mkdir(target.buildDir)
          }

          val saveDir = target.file("${target.buildDir}/projectInfo/")
          if (!saveDir.exists()) {
            target.mkdir(saveDir)
          }

          val file = target.file("${target.buildDir}/projectInfo/statistic.json")
          if (!file.exists()) {
            file.createNewFile()
          }


          ObjectMapper().apply {
            enable(SerializationFeature.INDENT_OUTPUT)
          }.writeValue(file, statistic)

        } catch (exception: IOException) {
          exception.printStackTrace()
        }
      }
    }
  }
}

```
```kotlin
import java.util.concurrent.atomic.AtomicInteger
class KotlinParserListenerImpl : KotlinParserBaseListener() {

    val classCounter = AtomicInteger()
    val functionCounter = AtomicInteger()

    override fun enterClassDeclaration(ctx: KotlinParser.ClassDeclarationContext) {
        classCounter.incrementAndGet()
    }

    override fun enterFunctionDeclaration(ctx: KotlinParser.FunctionDeclarationContext) {
        functionCounter.incrementAndGet()
    }
}
data class CodeStats(
    val lineCount: Int,
    val functionCount: Int,
    val classCount: Int
)
```
```kotlin
data class CodeStats(
    val lineCount: Int,
    val functionCount: Int,
    val classCount: Int
)
