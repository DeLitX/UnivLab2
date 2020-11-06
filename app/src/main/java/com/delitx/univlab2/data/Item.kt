package com.delitx.univlab2.data

data class Item (
    val values:MutableMap<String,String>
){
    override fun toString():String{
        var result=""
        var isFirst=true
        for(i in values){
            if(isFirst){
                isFirst=false
            }else{
                result+='\n'
            }
            result+=i.key +" : " +i.value
        }
        return result
    }
}