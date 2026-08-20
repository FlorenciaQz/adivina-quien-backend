# 🎮 Adivina Quién

Trabajo práctico de **Programación III**.

El proyecto consiste en desarrollar un juego sencillo de **Adivina Quién**, en el cual el usuario deberá intentar descubrir cuál de los personajes disponibles fue seleccionado por el programa como **"EL ELEGIDO"**.

## 🌿 Convención de ramas

Para mantener organizado el repositorio, las ramas deberán nombrarse utilizando la siguiente convención:

```text
tipo/descripcion-corta
```

Se utilizarán los siguientes prefijos:

* `feat/` → Desarrollo de una nueva funcionalidad.
* `fix/` → Corrección de errores.
* `refactor/` → Modificación o reorganización de código sin agregar una nueva funcionalidad.
* `chore/` → Configuraciones, mantenimiento o cambios que no afectan la lógica del juego.
* `docs/` → Cambios relacionados con documentación.

### Ejemplos

```text
feat/create-characters
feat/linear-search
feat/group-search
feat/wildcard
fix/character-selection
refactor/search-logic
docs/update-readme
chore/project-setup
```

La rama `main` contendrá la versión estable del proyecto. El desarrollo de nuevas funcionalidades se realizará en ramas independientes y, una vez finalizadas, se integrarán a `main`.

## 📋 Consigna

El juego contará inicialmente con **7 personajes**.

Cada personaje será representado mediante un objeto con los siguientes atributos:

* **ID**
* **Nombre**
* **Estado:** indica si el personaje es o no "EL ELEGIDO".

Al iniciar la aplicación, se deberá saludar al usuario y mostrar los personajes disponibles para que pueda seleccionar uno mediante su **ID**.

### Resultado de la elección

Si el usuario selecciona al personaje correcto, se mostrarán en pantalla los atributos del personaje elegido.

Si el usuario no acierta, se mostrará el mensaje:

```text
NOMBRE: no es el elegido
```

## 🃏 Comodín

El juego contará con una opción de **comodín**.

Al utilizarla, el programa deberá reducir las posibilidades agrupando los personajes en grupos de **3 o 4** e informar al usuario dentro de qué rango se encuentra el personaje elegido.

Por ejemplo:

```text
El personaje está entre el ID 1 y el ID 4.
```

De esta manera, se descarta el grupo que no contiene al personaje elegido.

## 🤖 Resolución automática

En caso de no encontrar al personaje elegido, el usuario tendrá la posibilidad de pedirle a la máquina que resuelva el juego automáticamente.

Se implementarán diferentes métodos de resolución:

### Búsqueda lineal

La máquina recorrerá los personajes uno por uno hasta encontrar al elegido.

### Búsqueda por agrupación

La máquina dividirá las posibilidades en grupos, identificará en cuál se encuentra la posible respuesta y descartará el grupo que no contiene al personaje elegido.

## 🖥️ Interfaz gráfica

En futuras etapas del trabajo práctico se incorporará una interfaz gráfica para el juego utilizando **JavaFX o Swing**.

## 🚧 Desarrollo incremental

Este proyecto se desarrollará de manera **incremental** durante la cursada de Programación III.

A medida que se incorporen nuevos conceptos y se entreguen nuevas consignas, se agregarán funcionalidades y métodos de resolución al juego.

