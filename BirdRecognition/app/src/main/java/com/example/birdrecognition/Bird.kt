package com.example.birdrecognition

class Bird {
    var birdName:String? = null
    var birdImage:Int? = null

    constructor(){}

    constructor(birdName:String,birdImage:Int){
        this.birdName = birdName
        this.birdImage = birdImage
    }
    constructor(birdName: String){
        this.birdName = birdName
    }
}