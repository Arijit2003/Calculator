package com.example.calculator

import android.media.MediaPlayer
import android.media.PlaybackParams
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.util.concurrent.ExecutionException

import kotlin.math.pow

var separatedExp:ArrayList<String> = arrayListOf()
var actualSeparatedExpression:MutableList<String> = mutableListOf()
var postfixExp:ArrayList<String> = arrayListOf()
var stack:MutableList<Char> = mutableListOf()
var top:Int=-1
var resultStack:MutableList<Double> = mutableListOf()
var resultTop:Int=-1
class MainActivity : AppCompatActivity() {
    lateinit var result: TextView
    var lastNumeric:Boolean=false
    var lastDot:Boolean=false
    private var tapSound:MediaPlayer= MediaPlayer()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        result=findViewById(R.id.result)
        tapSound=MediaPlayer.create(this,R.raw.click2)



    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
    fun onDigit(view: View){
        tapSound.start()
        if(result.text.toString().lowercase()=="infinity"){
            result.text=""
        }

        result.append((view as Button).text)
        lastNumeric=true
        lastDot=false
        when(view.id){
            R.id.CLR->{
                result.text=""
            }

        }
    }
    fun onDecimalPoint(view: View){

        tapSound.start()
        if(result.text.toString().lowercase().endsWith("infinity")){
            result.text=""
            lastDot=false
            lastNumeric=false
        }
        if(lastNumeric && !lastDot){
            result.append((view as Button).text)
            lastDot=true
            lastNumeric=false
        }
    }
    fun onOperatorAdd(view: View){
        tapSound.start()
        if(lastNumeric && !operatorAtEnd(result.text.toString())){
            result.append((view as Button).text)
            lastNumeric=false
            lastDot=false
        }
    }
    private fun operatorAtEnd(str:String):Boolean{
        if(str.endsWith("+")||str.endsWith("-")||
            str.endsWith("÷")||str.endsWith("×")){
            return true
        }
        return false
    }

    fun onEquals(view:View){
        tapSound.start()
        if (result.text.toString()!=""){
            var countLeftParenthesis:Int=0
            var countRightParenthesis:Int=0
            for(j in result.text.toString()){
                if(j=='('){
                    countLeftParenthesis++
                }
                else if(j==')'){
                    countRightParenthesis++
                }
            }
            if(countLeftParenthesis==countRightParenthesis){
                var A:String=result.text.toString()
                if(A.startsWith("-")){
                    separatedExp.add("-")
                    A=A.substring(1)
                }
                splitMethod(A)
                println(separatedExp)

                var hold:String
                for(i in separatedExp.indices){
                    hold= separatedExp[i]
                    while (hold.startsWith('(')){
                        actualSeparatedExpression.add("(")
                        hold=hold.substring(1)
                    }
                    var arrayList:ArrayList<String> = arrayListOf()
                    while (hold.endsWith(')')){
                        arrayList.add(")")
                        hold=hold.substring(0,hold.length-1)
                    }
                    if(hold!="") {
                        actualSeparatedExpression.add(hold)
                    }
                    for (i in arrayList.indices){
                        actualSeparatedExpression.add(arrayList[i])
                    }
                }
                val minusStore:ArrayList<Int> = arrayListOf()
                for(i in actualSeparatedExpression.indices){
                    if(actualSeparatedExpression[i]=="-"){
                        if(i-1>-1){
                            if(actualSeparatedExpression[i-1]=="("){

                                minusStore.add(i)
                                actualSeparatedExpression[i+1]= "-"+actualSeparatedExpression[i+1]

                            }
                        }
                        else if(i-1==-1){
                            minusStore.add(i)
                            actualSeparatedExpression[i+1]= ("-".plus(actualSeparatedExpression[i+1])).toString()

                        }
                    }
                }
                for(i in minusStore.sortedDescending()){
                    actualSeparatedExpression.removeAt(i)
                }
                findPostfix(actualSeparatedExpression)
                evaluatePostfix(postfixExp)
                result.text= removeZero(resultStack[0].toString())
                resultStack.removeAll(resultStack)
                actualSeparatedExpression.removeAll(actualSeparatedExpression)
                separatedExp.removeAll(separatedExp)
                postfixExp.removeAll(postfixExp)
                stack.removeAll(stack)
                top=-1
                resultTop=-1
            }
            else{
                Toast.makeText(this,"Invalid Expression",Toast.LENGTH_SHORT).show()
            }
        }
    }




    fun pushInStack(input:Char){
        top++
        stack.add(top,input)

    }
    fun popInStack():Char{

        if(top!=-1) {
            val pop:Char= stack[top]
            stack.removeAt(top)
            top--
            return pop

        }
        return '0'

    }
    fun pushInResultStack(input: Double){
        resultTop++
        resultStack.add(resultTop,input)
    }
    fun popFromResultStack():Double{
        if(resultTop!=-1){
            val popped:Double= resultStack.removeAt(resultTop)
            resultTop--
            return popped
        }
        return 0.0
    }
    fun evaluatePostfix(postfix:ArrayList<String>){
        var operand1:Double=0.0
        var operand2:Double=0.0
        for(i in postfix.indices){
            when(postfix[i]){
                "+"->{
                    operand1= popFromResultStack()
                    operand2= popFromResultStack()
                    pushInResultStack(operand1+operand2)
                }
                "-"->{
                    operand1= popFromResultStack()
                    operand2= popFromResultStack()
                    pushInResultStack(operand2-operand1)
                }
                "×"->{
                    operand1= popFromResultStack()
                    operand2= popFromResultStack()
                    pushInResultStack(operand1*operand2)
                }
                "÷"->{
                    operand1= popFromResultStack()
                    operand2= popFromResultStack()
                    pushInResultStack(operand2/operand1)
                }
                "^"->{
                    operand1= popFromResultStack()
                    operand2= popFromResultStack()
                    pushInResultStack(operand2.pow(operand1))
                }
                else->{
                    pushInResultStack(postfix[i].toDouble())
                }

            }
        }
    }
    fun precedence(operator:String):Int{
        when(operator){
            "^"->{
                return 3
            }
            "×"->{
                return 2
            }
            "÷"->{
                return 2
            }
            "+"->{
                return 1
            }
            "-"->{
                return 1
            }
            else->{
                return 0
            }

        }
    }

    fun findPostfix(infix:MutableList<String>){
        for(i in (infix.indices)){
            when(infix[i]){
                "("->{
                    pushInStack('(')
                }
                "+"->{

                    if(top>-1) {
                        var holdChar:Char='2'
                        while (precedence(infix[i]) <= precedence(stack[top].toString())) {
                            holdChar = popInStack()
                            if (holdChar != '0' && holdChar != '2') {
                                postfixExp.add(holdChar.toString())
                            }
                            if (top  == -1) {
                                break
                            }
                        }
                    }
                    pushInStack('+')
                }
                "-"->{
                    var holdChar:Char='2'
                    if(top>-1) {
                        while (precedence(infix[i]) <= precedence(stack[top].toString())) {
                            holdChar = popInStack()
                            if (holdChar != '0' && holdChar != '2') {
                                postfixExp.add(holdChar.toString())
                            }
                            if (top  == -1) {
                                break
                            }
                        }
                    }
                    pushInStack('-')
                }
                "×"->{
                    var holdChar:Char='2'
                    if(top>-1) {
                        while (precedence(infix[i]) <= precedence(stack[top].toString())) {
                            holdChar = popInStack()
                            if (holdChar != '0' && holdChar != '2') {
                                postfixExp.add(holdChar.toString())
                            }
                            if (top  == -1) {
                                break
                            }
                        }

                    }
                    pushInStack('×')
                }
                "÷"->{
                    var holdChar:Char='2'
                    if (top>-1) {
                        while (precedence(infix[i]) <= precedence(stack[top].toString())) {
                            holdChar = popInStack()
                            if (holdChar != '0' && holdChar != '2') {
                                postfixExp.add(holdChar.toString())
                            }
                            if (top  == -1) {
                                break
                            }
                        }
                    }
                    pushInStack('÷')
                }
                "^"->{
                    var holdChar:Char='2'
                    if(top>-1) {
                        while (precedence(infix[i]) <= precedence(stack[top].toString())) {
                            holdChar = popInStack()
                            if (holdChar != '0' && holdChar != '2') {
                                postfixExp.add(holdChar.toString())
                            }
                            if (top  == -1) {
                                break
                            }
                        }
                    }
                    pushInStack('^')
                }
                ")"->{
                    var newString:String
                    while (stack[top]!='('){
                        newString= popInStack().toString()
                        postfixExp.add(newString)
                    }
                    popInStack()
                }
                else->{
                    postfixExp.add(infix[i])
                }
            }
        }
        // start from here

        var newPop:String
        while(top!=-1){
            newPop= popInStack().toString()
            postfixExp.add(newPop)
        }
    }

    fun splitMethod(value:String){
        if(value.contains('+')||value.contains('-')||value.contains('×')||value.contains('÷')||value.contains('^')) {
            val firstOperator = whoIsFirst(value)

            val split = newSplitAlgo(value,firstOperator)
            val first = split[0]
            val second = split[1]
            splitMethod(first)
            separatedExp.add(firstOperator.toString())
            splitMethod(second)
        }

        else{
            separatedExp.add(value)

        }
    }

    fun whoIsFirst(str:String):Char{
        for(i in str.indices){
            if(str[i]=='+'){
                return '+'
            }
            else if(str[i]=='-'){
                return '-'
            }
            else if(str[i]=='÷'){
                return '÷'
            }
            else if(str[i]=='×'){
                return '×'
            }
            else if(str[i]=='^'){
                return '^'
            }

        }
        return '0'
    }

    fun newSplitAlgo(str:String,operator:Char):List<String>{
        for(i in (0..(str.length-1))){
            if(str[i]==operator){
                return listOf(str.substring(0,i),str.substring(i+1,(str.length)))

            }
        }
        return listOf("0")
    }


    private fun removeZero(value:String):String{
        if(value.endsWith(".0")){
            return value.substring(0,(value.length-2))
        }
        return value
    }
    fun backspace(view: View){
        tapSound.start()
        if(result.text.toString()!="") {
            result.text = (result.text.toString()).substring(0, (result.text.toString()).length - 1)
        }
    }
    fun parenthesis(view:View){
        tapSound.start()
        if(result.text.toString().length-1>-1){
        when(result.text.toString()[result.text.toString().length-1]){
            '+'->{
                result.append("(")
            }
            '-'->{
                result.append("(")
            }
            '×'->{
                result.append("(")
            }
            '÷'->{
                result.append("(")
            }
            '^'->{
                result.append("(")
            }
            '('->{
                result.append("(")
            }
            ')'->{
                result.append("(")
            }
            else->{
                var count:Int=0
                for(i in result.text.toString().indices){
                    if(result.text.toString()[i]=='('){
                        count++
                    }
                }
                if(count==0){
                    result.append("×(")
                }
                else if(count!=0){
                    var countRightParen:Int=0
                    while (count>countRightParen){
                        result.append(")")
                        countRightParen++
                    }
                }
            }
        }
        }
        else{
            result.append("(")
        }
    }
    fun addSub(view:View){
        if(result.text.toString().length-1>-1) {
            when (result.text.toString()[result.text.toString().length - 1]) {
                '+' -> {
                    result.append("(-")
                }
                '-' -> {
                    result.append("(-")
                }
                '×' -> {
                    result.append("(-")
                }
                '÷' -> {
                    result.append("(-")
                }
                '^' -> {
                    result.append("(-")
                }
                '(' -> {
                    result.append("(-")
                }
                ')' -> {
                    result.append("×(-")
                }
                else -> {
                    // if it is operator then what we have to do ???????
                    Toast.makeText(this,"Not yet implemented",Toast.LENGTH_SHORT).show()
                }
            }
        }
        else{
            result.append("(-")
        }
    }
}

/*
var holdMinus:String?=null
        var newResult:String=result.text.toString()
        if(newResult.startsWith("-")){
            holdMinus="-"
            newResult=newResult.substring(1)
        }
        try {
            //subtract
            if(newResult.contains("-")&&lastNumeric){
                val array=newResult.split("-")
                var operand1:String=array[0]
                val operand2:String=array[1]
                if(holdMinus=="-"){
                    operand1=holdMinus+operand1
                }
                val value=(operand1.toDouble()-operand2.toDouble()).toString()
                result.text=removeZero(value)
                holdMinus=null
            }
            // add
            else if(newResult.contains("+")&&lastNumeric){
                val array=newResult.split("+")
                var operand1:String=array[0]
                val operand2:String=array[1]
                if(holdMinus=="-"){
                    operand1=holdMinus+operand1
                }
                val value=(operand1.toDouble()+operand2.toDouble()).toString()
                result.text=removeZero(value)
                holdMinus=null
            }
            //divide
            else if(newResult.contains("÷")&&lastNumeric){
                val array=newResult.split("÷")
                var operand1:String=array[0]
                val operand2:String=array[1]
                if(holdMinus=="-"){
                    operand1=holdMinus+operand1
                }
                val value=(operand1.toDouble()/operand2.toDouble()).toString()
                result.text=removeZero(value)
                holdMinus=null
            }
            //multiply
            else if(newResult.contains("×")&&lastNumeric){
                val array=newResult.split("×")
                var operand1:String=array[0]
                val operand2:String=array[1]
                if(holdMinus=="-"){
                    operand1=holdMinus+operand1
                }
                val value=(operand1.toDouble()*operand2.toDouble()).toString()
                result.text=removeZero(value)
                holdMinus=null
            }
        }catch (e: ArithmeticException){
            e.printStackTrace()
        }
 */