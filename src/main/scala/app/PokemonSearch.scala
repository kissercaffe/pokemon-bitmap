package app

import com.github.tototoshi.csv.CSVReader
import com.github.tototoshi.csv.defaultCSVFormat
import java.io.File

import model.Pokemon
import zukan.PokemonZukan

object PokemonSearch extends App {
  val reader = CSVReader.open(new File("dataset/Pokemon.csv"))
  try {
    val pokemons = reader.allWithHeaders().map{row => 
      Pokemon(
        row("Id").toInt, 
        row("Name").toString, 
        row("Type 1").toString, 
        row("Type 2").toString, 
        row("Total").toInt, 
        row("HP").toInt, 
        row("Attack").toInt, 
        row("Defense").toInt, 
        row("Sp. Atk").toInt, 
        row("Sp. Def").toInt, 
        row("Speed").toInt, 
        row("Generation").toInt, 
        row("Legendary").toBoolean
        )
      }

      val pokemonZukan = PokemonZukan(pokemons)
      
      println("ポケモン図鑑検索システム")
      println("タイプでポケモンを検索できます。検索クエリを入力してください（終了する場合は 'quit' または 'exit' を入力）")
      println("例: Normal & (Water | Fire)")

      @annotation.tailrec
      def processQuery(): Unit = {
        print("> ")
        Option(scala.io.StdIn.readLine()) match {
          case None =>
            println("終了します。")
          case Some(q) if q.trim.toLowerCase == "quit" || q.trim.toLowerCase == "exit" =>
            println("終了します。")
          case Some(q) if q.trim.toLowerCase == "types" =>
            println(pokemonZukan.getPokemonTypes.mkString(", "))
          case Some(q) if q.trim.isEmpty =>
            println("クエリが空です。")
            processQuery()
          case Some(q) =>
            try {
              val results = pokemonZukan.search(q.trim)
              if (results.isEmpty) {
                println("該当するポケモンが見つかりませんでした。")
              } else {
                println(s"${results.length}件のポケモンが見つかりました:")
                results.foreach(pokemon => println(pokemon.name + ": " + pokemon.type1 + ", " + pokemon.type2))
              }
            } catch {
              case e: Exception => println(s"エラー: ${e.getMessage}")
            }
            processQuery()
        }
      }
      
      processQuery()
  }
  catch {
    case e: Exception => println(e)
  }
  finally {
    reader.close()
  }
}
