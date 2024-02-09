/**
 * MIT License
 *
 * Copyright (c) 2024  David Rodríguez Alfayate - Divisa iT
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/**
 * Parser para las expresiones lógicas de reglas de fechas
 *
 * @author David Rodríguez Alfayate
 */
grammar DateExpressionGrammar;
@header {
package com.proxiasuite.dateparser.grammar;
}

/**
 * El programa puede ser bien una operación lógica o bien una serie de periodos
 * temporales.
 */
prog: def* (logic | expr);

/**
 * Una expresión que es uno o más periodos separados por "," o "y"
 */
expr: period (And period)*;

/**
 * Expresión lógica para evaluar una condición si o no, admite definición
 * de variables para facilitar su uso.
 */
logic: If expr (Equals | Lesser | Greater) expr Then expr Else expr;

/**
 * Definición de una variable en el sistema.
 */
def: Def ID Assign expr;

/**
 * Expresión que nos define el periodo, al final no deja de ser sino
 * el átomo, o bien un conjunto de días dentro de ese átomo.
 */
period: dateExpr (To dateExpr)?;


/**
 * Expresión de tipo fecha
 */
dateExpr: (before|after|near|at);


/**
 * Una expresión de tipo before, nos permite mantener la referencia
 * a n [algo] antes
 */
before: day (Week | Month)? Before at |
        day? weekday Before at;

/**
 * Una expresión de tipo after, nos permite mantener la referencia
 * a [n] algo después
 */
after: day (Week | Month)? After at |
       day? weekday After at;
/**
 * Una operación de proximidad, puede ser un día próximo o el fin de semana próximo
 */
near: baseDay Near at;

/**
 * Una operación de "en fecha concreta" o "en rango de fechas"
 */
at: (day (And day)*) (weekday (And weekday)*)? dateExpr  |
    weekday dateExpr |
    atom ;


/**
 * Expresión que nos define el concepto de "átomo" de un periodo, su parte
 * más "minuscula". Puede ser una de las fechas predefinidas (carnaval,
 * semana santa, o una expresión de tipo mes
 */
atom:   HolyWeek |
        Carnival |
        AshWednesday |
        CorpusChristi |
        Ascension |
        Pentecost |
        Easter |
        month |
        ID;


/**
 * ======================================================================
 * Reglas básicas del parseador para introducir en el sistema el concepto
 * que necesitamos
 * ======================================================================
 */

/**
 * Concepto de día
 */
day: One |
     Two |
     Three |
     Four |
     Five |
     Six |
     Seven |
     Eight |
     Nine |
     Ten |
     Eleven |
     Twelve |
     Thirteen |
     Fourteen |
     Fifteen |
     Sixteen |
     Seventeen |
     Eighteen |
     Nineteen |
     Twenty |
     TwentyOne |
     TwentyTwo |
     TwentyThree |
     TwentyFour |
     TwentyFive |
     TwentySix |
     TwentySeven |
     TwentyEight |
     TwentyNine |
     Thirty |
     ThirtyOne |
     Last |
     Prelast |
     Yesterday |
     NUMBER;

/**
 * Meses
 */
month:    January |
          February |
          March |
          April |
          May |
          Jun |
          July |
          August |
          September |
          October |
          November |
          December;

/**
 * Días de la semana
 */
weekday: baseDay |
         Week |
         Fortnight;

/**
 * Los formatos básicos de día
 */
baseDay:  Monday |
          Tuesday |
          Wednesday |
          Thursday |
          Friday |
          Saturday |
          Sunday |
          WeekEnd;


/**
 * Reglas lexicas
 */
If: 'si';
Equals: 'es' WS 'igual' (WS 'a')?;
Greater: 'es' WS 'mayor' (WS 'qu' ('e'|'\u00E9') )?;
Lesser: 'es' WS 'menor' (WS 'qu' ('e'|'\u00E9') )?;
Else: 'si' WS 'no';
Then: 'entonces';

HolyWeek: 'semana' WS 'santa';
WeekEnd: 'fin' WS PREPO WS 'semana';
Carnival: 'carnaval';
AshWednesday:'mi' ('e'|'\u00E9') 'rcoles' WS PREPO WS 'ceniza';
CorpusChristi: 'corpus' (WS 'christi')?;
Ascension: 'ascensi' ('o'|'\u00F3') 'n';
Pentecost: 'pentecost' ('e'|'\u00E9') 's';
Easter: 'pascua';

Before: 'antes' | 'anterior' (WS To)?;
After: 'despu' ('e'|'\u00E9') 's' | (('siguiente' | 'posterior') (WS To)?);
Near: ('cerca' | 'pr' ('o'|'\u00F3') 'xim' GenderExtension) (WS To)?;

To: 'al' | 'hasta' | 'a';

January: 'enero';
February: 'febrero';
March: 'marzo';
April: 'abril';
May: 'mayo';
Jun: 'junio';
July: 'julio';
August: 'agosto';
September: 'septiembre';
October: 'octubre';
November: 'noviembre';
December: 'diciembre';

Monday: 'lunes';
Tuesday: 'martes';
Wednesday: 'mi' ('e'|'\u00E9') 'rcoles';
Thursday: 'jueves';
Friday: 'viernes';
Saturday: 's' ('a'|'\u00E1') 'bado';
Sunday: 'domingo' 's'?;

Month: 'mes' ('es')?;
Week: 'semana' 's'?;
Fortnight: 'quincena' 's'?;

// Los días son implicitos, es una operación que podemos ignorar tranquilamente.
Day: 'd' ('i'|'\u00ED') 'a' 's'? -> skip;

fragment GenderExtension: ('o' |'a') 's'?;
fragment FirstFragment: 'primer' GenderExtension?;
fragment SecndFragment: 'segund' GenderExtension;
fragment ThirdFragment: 'tercer' GenderExtension?;
fragment FourthFragment: 'cuart' GenderExtension;
fragment FifthFragment: 'quint' GenderExtension;
fragment SixthFragment: 'sext' GenderExtension;
fragment SeventhFragment: 'septim' GenderExtension;
fragment EighthFragment: 'octav' GenderExtension;
fragment NinthFragment: 'noven' GenderExtension;
fragment TenthFragment: 'decim' GenderExtension;
fragment TwentiethFragment: 'vigesim' GenderExtension;
fragment ThirthiethFragment: 'trigesim' GenderExtension;

NUMBER: [0-9]+;
Assign: ':';
Def: 'def';

And: ',' | 'y';

Last: ('u'|'\u00FA') 'ltim' GenderExtension;
Prelast: 'pen' ('u'|'\u00FA') 'ltim' GenderExtension;
Yesterday: 'vispera' | 'ayer';
One: FirstFragment | 'un' GenderExtension?;
Two: SecndFragment | 'dos';
Three: ThirdFragment | 'tres';
Four: FourthFragment | 'cuatro';
Five: FifthFragment | 'cinco';
Six: SixthFragment | 'seis';
Seven: SeventhFragment | 'siete';
Eight: EighthFragment | 'ocho';
Nine: NinthFragment | 'nueve';
Ten: TenthFragment | 'diez';
Eleven: TenthFragment FirstFragment | 'once';
Twelve: TenthFragment SecndFragment | 'doce';
Thirteen: TenthFragment ThirdFragment | 'trece';
Fourteen: TenthFragment FourthFragment | 'catorce';
Fifteen: TenthFragment FifthFragment | 'quince';
Sixteen: TenthFragment SixthFragment | 'dieciseis';
Seventeen: TenthFragment SeventhFragment | 'diecisiete';
Eighteen: TenthFragment EighthFragment | 'dieciocho';
Nineteen: TenthFragment NinthFragment | 'diecinueve';
Twenty: TwentiethFragment | 'veinte';
TwentyOne: TwentiethFragment FirstFragment | 'veintiuno';
TwentyTwo: TwentiethFragment SecndFragment | 'veintidos';
TwentyThree: TwentiethFragment ThirdFragment | 'veintitres';
TwentyFour: TwentiethFragment FourthFragment | 'veinticuatro';
TwentyFive: TwentiethFragment FifthFragment | 'veinticinco';
TwentySix: TwentiethFragment SixthFragment | 'veintiseis';
TwentySeven: TwentiethFragment SeventhFragment | 'veintisiete';
TwentyEight: TwentiethFragment EighthFragment | 'veintiocho';
TwentyNine: TwentiethFragment NinthFragment | 'veintinueve';
Thirty: ThirthiethFragment | 'treinta';
ThirtyOne: ThirthiethFragment FirstFragment | 'treintayuno' | 'treinta' WS 'y' WS 'uno';

WS: [ \t\n\r\f]+ -> skip ;
PREPO: ('de' ('l')? | 'la' | 'el') -> skip;
ID: [a-z$]+;


