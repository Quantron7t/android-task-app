package com.example.todoapp.ancillary

import com.google.gson.annotations.SerializedName

data class Delta(
    @SerializedName("ops" ) var ops : ArrayList<Ops> = arrayListOf()
)

data class Ops (
    @SerializedName("attributes" ) var attributes : Attributes? = Attributes(),
    @SerializedName("insert"     ) var insert     : String?     = null
)

data class Attributes (
    var list : String? = null,
    val underline: Boolean = false,
    val strike: Boolean = false,
    val italic: Boolean = false,
    val bold: Boolean = false,
    val header : Int? = null,
    val background : String? = null,
    val color : String? = null,
    val script : String? = null,
    val blockquote: Boolean = false,
    @SerializedName("code-block"     ) val code : Boolean = false
)

enum class Checkbox(val value:String) {
    CHECKED("checked"),
    UNCHECKED("unchecked")
}