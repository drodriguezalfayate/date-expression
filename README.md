# Date Expression 

## Introducción
El objetivo de este pequeño proyecto es el de proporcionar una librería que
permite obtener de una expresión de fecha las fechas reales 
efectivas en un conjunto de años determinado, a partir del año actual o 
bien del año especificado.

Se basa en una gramática desarrollada con ANTLR y utilizando expresioines en
lenguajes natural, adaptado de forma inicial al castellano.

## Uso de la librería
La clase principal es `DateExpression` que permite especificar una
serie de datos de entrada:

* Idioma de trabajo, importante a la hora de determinar si las semanas 
  comienzan en domingo o lunes.
* Zona horaria
* Año inicial a partir de que se computan las fechas reales a partir de la expresion
* Número de años para el que se resuelven las fechas reales
* Expresión en castellano que refleja la fecha.


## Ejemplos de expresiones

Todos los ejemplos se refieren a 2024 y con una periodicidad de dos años
* `uno de enero`, resolvería como el día '1/01/2024' y '1/01/2025'
* `segundo domingo de enero`, resolvería como los días '14/01/2024' y '12/05/2024'
* `lunes despues del lunes de pascua`, resolvería como el día '08/04/2024' y '28/04/2024'
* `3 y cuarto domingo de junio y seis de junio y primer domingo de semana santa` que resolvería como los días
  '24/03/2024', '06/06/2024', '16/06/2023', '23/06/2024', '13/04/2025', '06/06/2025', '22/06/2029' y '29/06/2029'

Permite, adicionalmente la expresión de condicionales:
```
si sabado de carnaval es igual a segundo sabado de febrero
entonces primer fin de semana de marzo
si no ultimo fin de semana de abril
```   

Que resolvería para 2024, como '2/3/2024', '3/3/2024' y para 2025 como '26/04/2024' y '27/04/2024'

Y la creación de constantes que pueden ser utilizadas a lo largo de condicionales o bien para devolver datos
```
def navidad: 25 de diciembre
navidad
```
En este caso tendríamos como resultado el '25/12/2024' y el '25/12/2025'

## Expresiones soportadas

* Referencias a días concretos de la semana
* Referencias a meses y quincenas
* Conceptos de antes, después y proximidad
* Rangos de fechas

````
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
at: (day (And day)*) (weekday (And weekday)*)? atom  |
    weekday atom |
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

````



