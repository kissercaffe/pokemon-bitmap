package zukan

import model.Pokemon
import bitmap.BitMap

class PokemonZukan(
  val pokemons: List[Pokemon],
  private val pokemonTypes: List[String],
  private val typeBitmap: BitMap[String] 
  ) {

  def search(query: String): List[Pokemon] = {
    val resultIds = typeBitmap.query(query)
    pokemons.filter(pokemon => resultIds.contains(pokemon.id))
  }

  def getPokemonTypes: List[String] = pokemonTypes

}

object PokemonZukan {
  def apply(pokemons: List[Pokemon]): PokemonZukan = {
    val pokemonTypes = getPokemonTypes(pokemons)
    val pokemonListsByTypes = getPokemonListsByTypes(pokemons, pokemonTypes)
    val typeBitmap = getPokemonBitmapByTypes(pokemonListsByTypes)
    new PokemonZukan(pokemons, pokemonTypes, typeBitmap)
  }

  private def getPokemonTypes(pokemons: List[Pokemon]): List[String] = {
    pokemons.flatMap(pokemon => List(pokemon.type1, pokemon.type2)).distinct.filter(_.nonEmpty)
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

