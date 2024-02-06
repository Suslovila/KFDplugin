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