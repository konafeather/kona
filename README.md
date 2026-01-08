Kona is a programming language in active development. So far, the compiler can only print an abstract syntax tree. The language is object-oriented, indentation-based, and statically typed, but without type annotations. Also, it provides a new kind of syntax for calling functions. As a general purpose language, writing all kinds of programs will be easier, faster, and more effective. To get an overview of the language, read "language_overview.txt".

Here is an example of the fibbonaci sequence in Kona:

_________________________________________________________________________________________________________________________

# this is a single line comment
# "|" are lines in a block that would appear in an IDE, not actual characters to be typed

fun fibonacci_recursive: n

   if n == 0
   |  return 0
   elif n == 1
   |  return 1
   else
   |  return (fibonacci_recursive: n - 1) + (fibonacci_recursive: n - 2)


echo "Hello world"                     # built-in functions don't require a colon for function calls   
val n1, _ = [50, 100]                  # type inference and array destructuring
echo fibonacci_recursive: n1           # Nested function calls. User defined functions require a colon.

_________________________________________________________________________________________________________________________

