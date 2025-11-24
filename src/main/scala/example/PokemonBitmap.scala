package example

import com.github.tototoshi.csv.CSVReader
import java.io.File

import scala.collection.immutable.BitSet
import scala.collection.mutable
import java.nio.MappedByteBuffer


case class Pokemon(
  id: Int, 
  name: String,
  type1: String, 
  type2: String,
  total: Int, 
  hp: Int, 
  attack: Int, 
  defense: Int, 
  spAttack: Int, 
  spDefense: Int, 
  speed: Int, 
  generation: Int, 
  legendary: Boolean
)

class BitMap[T] {
  private val bitMaps = mutable.Map[T, BitSet]()

  def add(key: T, index: Int) = {
    val current = bitMaps.getOrElse(key, BitSet.empty)
    bitMaps(key) = current + index
  }

  def addAll(key: T, indices: List[Int]) = {
    val current = bitMaps.getOrElse(key, BitSet.empty)
    bitMaps(key) = current ++ indices
  }

  def get(key: T): BitSet = {
    bitMaps.getOrElse(key, BitSet.empty)
  }

  def search(key: T): List[Int] = {
    bitMaps.getOrElse(key, BitSet.empty).toList
  }

}


// class PokemonListsByTypes {
//   private val pokemonLists = List[Pokemon] 
// }

object PokemonListsByTypes {
  def getPokemonListsByTypes(pokemons: List[Pokemon], pokemonTypes: List[String]): List[(String, List[Int])] = {
    pokemonTypes.map(pokemonType => (pokemonType, pokemons.filter(pokemon => pokemon.type1 == pokemonType || pokemon.type2 == pokemonType).map(pokemon => pokemon.id)))
  }

  def getPokemonBitmapByTypes(pokemonListsByTypes: List[(String, List[Int])]): BitMap[String] = {
    val typeBitmap = new BitMap[String]()
    pokemonListsByTypes.foreach(pokemonList => typeBitmap.addAll(pokemonList._1, pokemonList._2))
    typeBitmap
  }
}



object PokemonBitmap extends App {
  val reader = CSVReader.open(new File("../../dataset/Pokemon.csv"))
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
      val types = pokemons.flatMap(pokemon => List(pokemon.type1, pokemon.type2)).distinct

      val pokemonListsByTypes = PokemonListsByTypes.getPokemonListsByTypes(pokemons, types)
      println(pokemonListsByTypes)

      val typeBitmap = PokemonListsByTypes.getPokemonBitmapByTypes(pokemonListsByTypes)

      println(typeBitmap.search("Normal"))

  }
  catch {
    case e: Exception => println(e)
  }
  finally {
    reader.close()
  }
}