# Homework 6

# Hot-Keys
1. **Ctrl + /** -  добавить // в начало каждой из выделенных строк
2. **Alt + =** - показать тип выделенного выражения
3. **Ctrl + Alt + H** - иерархия вызовов
4. **Ctrl + Alt + Shift + X** - создать пустой scala worksheet
5. **Alt + left** - шаг назад (в контексте навигации по файлу и между ними)
6. **Alt + right** - шаг вперёд (в контексте навигации по файлу и между ними)
7. **Ctrl + click** - зайти в реализацию метода/класса
8. **Ctrl + Alt + L** - рефакторинг выделенного кода в соответствии с заданым стилем кода
9. **Ctrl + Alt + Shift + L** - рефакторинг текущего файла в соответствии с заданым стилем кода
10. **Ctrl + Shift + T** - переход к тесту

# Live templates
1. `ararb` 

`new Array[ArrayBuffer[$ARGT$]]($LENGTH$)`

2. `ararbin`
```
for (i<- $NAME$.indices)
    $NAME$(i) = new ArrayBuffer()
```    
    
3. `pp` 

` += 1`

# Sbt all test

add to build.sbt 

```
lazy val scalacheck = "org.scalacheck" %% "scalacheck" % "1.13.4"
libraryDependencies += scalacheck % Test
```

in sbt write
`> test`


# Intellij idea all test task

1. **Alt + Shift + F10** - выбор конфигурации
2. **0** - изменение конфигурации
3. **Alt + Insert** - создать новый шаблон
4. Выбрать **ScalaTest**
5. В поле **Name** дать имя конфигурации
6. В поле **Test kind** выбрать **All in package**
7. В списке **Search for tests** выбрать **In whole project**
8. Нажать **OK**
9. Запустить созданую конфигурацию
