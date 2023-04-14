package com.example.gestorednd.DataClasses

//classe con personaggio dettagliato
class Pg(
    var idOwner : String? = null,

    var pgName : String = "",
    var species : String = "",
    var clss : String = "",
    var lvl : Int = 0,

    var hp : Int = 0,
    var ac : Int = 0,
    var speed : Int = 0,
    var profBonus : Int = 0,
    var initBonus : Int = 0,

    var str : Int = 0,
    var dex : Int = 0,
    var con : Int = 0,
    var int : Int = 0,
    var wis : Int = 0,
    var cha : Int = 0,

    var acr : Int = 0,
    var ath : Int = 0,
    var arc : Int = 0,
    var dec : Int = 0,
    var ins : Int = 0,
    var kno : Int = 0,
    var med : Int = 0,
    var per : Int = 0,
    var ste : Int = 0,
    var sur : Int = 0,

    var equip : ArrayList<String> = arrayListOf(),
    var bag : ArrayList<String> = arrayListOf(),
    var spellArray : ArrayList<Pair<String, Int>> = arrayListOf(),
    var featArray : ArrayList<Pair<String, String>> = arrayListOf()

) {
    var spells = spellArray.map {
        Spells(it.first, it.second)
    } as ArrayList<Spells>

    var feats = featArray.map{
        Feats(it.first, it.second)
    } as ArrayList<Feats>
}