import arrow.Effect
import arrow.EffectScope
import arrow.effect

context(Comparator<A>)
fun <A> List<A>.sort(): List<A> =
  sortedWith(this@Comparator)

fun sortScopeFunctions(): Unit {
  with(Comparator.naturalOrder<Int>()) {
    listOf(3, 5, 1).sort() // [1, 3, 5]
  }

  Comparator.naturalOrder<Int>().run {
    listOf(4, 6, 2, 1).sort() // [1, 2, 4, 6]
  }
}

@JvmInline value class Content(val body: List<String>)

sealed interface FileError
@JvmInline value class SecurityError(val msg: String?) : FileError
@JvmInline value class FileNotFound(val path: String) : FileError
object EmptyPath : FileError {
  override fun toString() = "EmptyPath"
}

context(EffectScope<FileError>)
suspend fun readFile(path: String?): Content {
  TODO("All functionality from cont { } available here")
}

val res: Effect<FileError, Content> = effect {
  readFile("")
}

context(EffectScope<FileError>)
suspend fun allFiles(vararg path: String): List<Content> =
  path.map { readFile(it) }

fun interface Logger {
  suspend fun info(msg: String): Unit
}

context(Logger, EffectScope<FileError>)
  suspend fun allFiles(vararg path: String): List<Content> {
  info("Processing files")
  return path.map { readFile(it) }
}

val res2: Effect<FileError, List<Content>> = with(Logger(::println)) {
  effect {
    allFiles("", "")
  }
}

context(Logger)
suspend fun allFilesOrEmpty(vararg path: String): List<Content> =
  effect<FileError, List<Content>> {
    allFiles(*path)
  }.orElse { emptyList() }



