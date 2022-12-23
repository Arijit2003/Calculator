package com.example.calculator
val arrayList:String="6+3*(5*8)-8"
fun main(){
     //Atfirst check what is the last element
    when(arrayList[arrayList.length-1]){
        '+'->{
            arrayList.plus('(')
        }
        '-'->{
            arrayList.plus('(')
        }
        '×'->{
            arrayList.plus('(')
        }
        '÷'->{
            arrayList.plus('(')
        }
        '^'->{
            arrayList.plus('(')
        }
        '('->{
            arrayList.plus('(')
        }
        ')'->{
            arrayList.plus("×(")
        }
        else->{
            var count:Int=0
            for(i in arrayList.indices){
                if(arrayList[i]=='('){
                    count++
                }
            }
            if(count==0){
                arrayList.plus("×(")
            }
            else{
                var countRightParen:Int=0
                while (count>countRightParen){
                    arrayList.plus(')')
                    countRightParen++
                }
            }
        }
    }
}