# Adivina Quién — Guía del proyecto

Este documento resume cómo está armado el juego hoy y qué tener en cuenta si sumás algo nuevo. No es una spec rígida: la idea es mantener coherencia con el diseño actual, no atarse a reglas estrictas.

## Cómo funciona el juego

- Dos modos: **Humano vs Máquina** (jugás contra M1 y, si le ganás, contra M2) y **Máquina vs Máquina** (mirás jugar a las dos IAs).
- Cada lado tiene un personaje secreto, elegido al azar entre todos los cargados. No hay restricciones sobre quién puede elegir a quién — que coincida con el secreto de otro jugador no afecta la partida ni le da ventaja a nadie.
- En su turno, cada jugador hace una de dos cosas:
  - **Pregunta** algo del catálogo (sí/no) y descarta candidatos según la respuesta.
  - **Adivina** un personaje puntual por su número (id).
- Adivinar mal **no termina la partida**: se descarta ese personaje de los candidatos y se sigue jugando. La partida termina solo cuando alguien adivina bien.
- Los perfiles de los personajes (género, pelo, lentes, humano) **no tienen por qué ser únicos** — puede haber dos personajes con los mismos 4 atributos. El juego lo tiene en cuenta: si ninguna pregunta separa a los candidatos que quedan, la máquina arriesga en vez de trabarse.
- Cuando la máquina le pregunta al humano, el humano no puede mentir: si contesta algo que no coincide con su secreto, se rechaza y se le vuelve a preguntar.
- No hay ranking ni persistencia entre partidas — cada corrida es independiente.

## Estrategia y algoritmos

- **Divide y conquista + recursión**: `Tablero` ordena los personajes con un merge sort escrito a mano (nada de `Collections.sort`), como ejemplo real de D&C con caso base.
- **Greedy, con dos heurísticas para poder comparar**:
  - `EstrategiaGreedy` (M1): en cada turno prueba las preguntas del catálogo y elige la que divide a los candidatos más parejo (minimiza |sí − no|).
  - `EstrategiaDesbalanceada` (M2): mismo algoritmo, pero busca la pregunta que divide más *desparejo* (maximiza |sí − no|) — apuesta a aislar rápido un grupo chico de sospechosos.
  - Las dos implementan `EstrategiaPreguntas`, así que `Maquina` recibe cualquiera de las dos sin saber cuál es.

No usamos programación dinámica/memoización a propósito: se probó una estrategia "óptima" con memoización y también un caché compartido entre estrategias, pero no aportaban nada real en la práctica (o no era contenido visto en la cursada), así que se sacaron. Si en algún momento hace falta memoización de verdad, que sea para un caso donde efectivamente se reutilice un cálculo — no solo para nombrarla en el informe.

## Estructura del proyecto

```
src/adivinaquien/
├── Main.java          // arma todo (wiring) y arranca el juego
├── dominio/            // datos: Personaje, Genero, ColorPelo, Tablero, CargaPersonajes
├── algoritmos/         // Pregunta, CatalogoPreguntas, EstrategiaPreguntas + sus 2 implementaciones
├── juego/              // Maquina (dato simple) y MotorJuego (toda la lógica de turnos)
└── ui/                 // InterfazUsuario (interface) y ConsolaUI (consola)
```

- `dominio` no depende de nada más del proyecto.
- `algoritmos` depende solo de `dominio`.
- `juego` depende de `dominio`, `algoritmos` y la interfaz `InterfazUsuario` (nunca de `ConsolaUI` directamente).
- Toda la entrada/salida pasa por `InterfazUsuario`, para poder reemplazar la consola por una GUI (JavaFX/Swing) sin tocar el resto del juego.
- `MotorJuego` concentra toda la coreografía del juego (turnos, secretos, anti-mentira). Se prefiere código plano y legible en un solo lugar antes que muchas clases chicas para cada responsabilidad mínima.

## Si vas a sumar algo

- Antes de agregar una clase nueva, pensá si realmente hace falta o si puede ser un método más en algo que ya existe. Varias cosas se sacaron en el camino (ranking, un árbol de decisión precomputado, exclusiones de secretos que no hacían nada) porque no aportaban nada real, solo complejidad.
- Para un atributo nuevo en los personajes: un valor al enum correspondiente (o uno nuevo), un caso en `Pregunta.evaluar`, una línea en `CatalogoPreguntas`. Un solo lugar.
- Para una estrategia nueva: que implemente `EstrategiaPreguntas` y listo, no hace falta tocar `Maquina` ni `MotorJuego`.
- Nombres en español, clases en PascalCase, métodos y variables en camelCase.
