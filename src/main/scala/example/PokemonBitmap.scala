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
  private val bitmaps = mutable.Map[T, BitSet]()

  def add(key: T, index: Int) = {
    val current = bitmaps.getOrElse(key, BitSet.empty)
    bitmaps(key) = current + index
  }

  def addAll(key: T, indices: List[Int]) = {
    val current = bitmaps.getOrElse(key, BitSet.empty)
    bitmaps(key) = current ++ indices
  }

  def getBitSet(key: T): BitSet = {
    bitmaps.getOrElse(key, BitSet.empty)
  }

  def search(key: T): List[Int] = {
    bitmaps.getOrElse(key, BitSet.empty).toList
  }

  def union(keys: List[T]): List[Int] = {
    keys.map(key => bitmaps.getOrElse(key, BitSet.empty)).reduce((a, b) => a | b).toList
  }

  def inter(keys: List[T]): List[Int] = {
    keys.map(key => bitmaps.getOrElse(key, BitSet.empty)).reduce((a, b) => a & b).toList
  }

  def diff(keys: List[T]): List[Int] = {
    keys.map(key => bitmaps.getOrElse(key, BitSet.empty)).reduce((a, b) => a &~ b).toList
  }

  def symDiff(keys: List[T]): List[Int] = {
    keys.map(key => bitmaps.getOrElse(key, BitSet.empty)).reduce((a, b) => a ^ b).toList
  }
}

class PokemonZukan(
  val pokemons: List[Pokemon],
  private val pokemonTypes: List[String],
  private val typeBitmap: BitMap[String] 
  ) {
  def search(pokemonType: String): List[Pokemon] = {
    val resultIds = typeBitmap.search(pokemonType)
    pokemons.filter(pokemon => resultIds.contains(pokemon.id))
  }

  def searchAnd(pokemonTypes: List[String]): List[Pokemon] = {
    val resultIds = typeBitmap.inter(pokemonTypes)
    pokemons.filter(pokemon => resultIds.contains(pokemon.id))
  }

}

object PokemonZukan {
  def apply(pokemons: List[Pokemon]): PokemonZukan = {
    val pokemonTypes = getPokemonTypes(pokemons)
    val pokemonListsByTypes = getPokemonListsByTypes(pokemons, pokemonTypes)
    val typeBitmap = getPokemonBitmapByTypes(pokemonListsByTypes)
    new PokemonZukan(pokemons, pokemonTypes, typeBitmap)
  }

  private def getPokemonTypes(pokemons: List[Pokemon]): List[String] = {
    pokemons.flatMap(pokemon => List(pokemon.type1, pokemon.type2)).distinct
  }

  private def getPokemonListsByTypes(pokemons: List[Pokemon], pokemonTypes: List[String]): List[(String, List[Int])] = {
    pokemonTypes.map{ pokemonType => 
      (pokemonType, pokemons.filter(pokemon => pokemon.type1 == pokemonType || pokemon.type2 == pokemonType).map(pokemon => pokemon.id))
    }
  }

  private def getPokemonBitmapByTypes(pokemonListsByTypes: List[(String, List[Int])]): BitMap[String] = {
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

      val pokemonZukan = PokemonZukan(pokemons)
      
      pokemonZukan.searchAnd(List("Normal", "Fire")).foreach(pokemon => println(pokemon.name + ": " + pokemon.type1 + ", " + pokemon.type2))
  }
  catch {
    case e: Exception => println(e)
  }
  finally {
    reader.close()
  }
}