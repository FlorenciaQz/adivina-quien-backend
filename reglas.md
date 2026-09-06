# Adivina Quién — Guía del proyecto

Este documento resume cómo está armado el juego hoy y qué tener en cuenta si sumás algo nuevo. No es una spec rígida: la idea es mantener coherencia con el diseño actual, no atarse a reglas estrictas.

## Cómo funciona el juego

- Dos modos: **Humano vs Máquina** (jugás contra M1 y, si le ganás, contra M2) y **Máquina vs Máquina** (mirás jugar a las dos IAs).
- Cada lado tiene un personaje secreto, elegido al azar entre todos los cargados. No hay restricciones sobre quién puede elegir a quién — que coincida con el secreto de otro jugador no afecta la partida ni le da ventaja a nadie.
- En su turno, cada jugador hace una de dos cosas:
  - **Pregunta** algo del catálogo (sí/no) y descarta candidatos según la respuesta.
  - **Adivina** un personaje puntual por su número (id).
- Adivinar mal **no termina la partida**: se descarta ese personaje de los candidatos y se sigue jugando. La partida termina solo cuando alguien adivina bien.
- El diseño **no asume que los perfiles sean únicos** (género, pelo, lentes, humano): si ninguna pregunta separa a los candidatos que quedan, la máquina arriesga en vez de trabarse. Con los 23 personajes actuales ese caso no llega a darse, porque los 23 perfiles son distintos y el catálogo cubre los 4 atributos, así que siempre hay una pregunta que separa. La salvaguarda queda igual, por si más adelante se agregan personajes con perfiles repetidos.
- Cuando la máquina le pregunta al humano, el humano no puede mentir: si contesta algo que no coincide con su secreto, se rechaza y se le vuelve a preguntar.
- No hay ranking ni comparación entre jugadores — pero el humano sí acumula sus propias victorias entre corridas: `persistencia.MarcadorPartidas` las guarda en `marcador.properties` (raíz del proyecto, no versionado) y `MotorJuego` muestra el resumen apenas arranca `jugarFlujoCompleto`. Cada partida ganada (a M1, y a M2 si corresponde) suma una victoria por separado.

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
├── Main.java           // arma todo (wiring) y arranca el juego
├── dominio/            // datos: Personaje, Genero, ColorPelo, Tablero, CargaPersonajes
├── algoritmos/         // Pregunta, CatalogoPreguntas, EstrategiaPreguntas + sus 2 implementaciones
├── juego/              // Maquina, MotorJuego, Candidatos, EntradaJugador, PresentadorJuego
├── persistencia/       // MarcadorPartidas: guarda/lee las victorias del humano en marcador.properties
└── ui/                 // InterfazUsuario (interface) y ConsolaUI (consola)
```

- `dominio` no depende de nada más del proyecto.
- `algoritmos` depende solo de `dominio`.
- `persistencia` no depende de nada más del proyecto (solo `java.io`/`java.util`).
- `ui` depende solo de `dominio` (necesita `Personaje` para dibujar el tablero).
- `juego` depende de `dominio`, `algoritmos`, la interfaz `InterfazUsuario` (nunca de `ConsolaUI` directamente) y `persistencia`.
- Toda la entrada/salida pasa por `InterfazUsuario`, para poder reemplazar la consola por una GUI (JavaFX/Swing) sin tocar el resto del juego.
- `InterfazUsuario` expone métodos semánticos, no strings ya formateados: `mostrarTablero(titulo, todos, idsVigentes)` recibe los datos y cada implementación decide cómo se ven. Antes el motor armaba las marcas `[posible]`/`[descartado]` y `ConsolaUI` espiaba esos literales dentro del texto para elegir el color; ese protocolo implícito ya no existe y es lo que hace viable una GUI.
- `MotorJuego` se queda solo con la coreografía de turnos: quién juega, qué pasa cuando alguien acierta o falla, cuándo termina la partida. Todo lo demás está repartido en clases con una responsabilidad clara cada una:
  - `Candidatos`: operaciones puras sobre listas de candidatos (filtrar, contar, descartar). No conoce ni la UI ni el estado de la partida.
  - `EntradaJugador`: pide datos al jugador y los devuelve ya validados. No conoce las reglas ni el personaje secreto de nadie.
  - `PresentadorJuego`: único lugar donde se arman los textos que ve el jugador. El motor dice qué pasó, el presentador decide cómo se escribe.
- La división busca que cada clase tenga una sola razón para cambiar. El criterio fue mantener un método más en una clase existente antes que una clase nueva, salvo que la responsabilidad sea claramente distinta.
- `MotorJuego` recibe el `Random` por constructor en vez de crearlo. `Main` acepta `-Dseed=N` para fijar la semilla: con la misma semilla se sortean los mismos personajes secretos y la partida se repite igual, lo que permite comparar la salida antes y después de un cambio. Sin el parámetro, cada corrida sortea distinto.

## Si vas a sumar algo

- Antes de agregar una clase nueva, pensá si realmente hace falta o si puede ser un método más en algo que ya existe. Varias cosas se sacaron en el camino (ranking, un árbol de decisión precomputado, exclusiones de secretos que no hacían nada) porque no aportaban nada real, solo complejidad.
- Para un atributo nuevo en los personajes: un valor al enum correspondiente (o uno nuevo), un caso en `Pregunta.evaluar`, una línea en `CatalogoPreguntas`. Un solo lugar.
- Para una estrategia nueva: que implemente `EstrategiaPreguntas` y listo, no hace falta tocar `Maquina` ni `MotorJuego`.
- Si agregás un mensaje que ve el jugador, va en `PresentadorJuego`, no concatenado en el motor.
- Nombres en español, clases en PascalCase, métodos y variables en camelCase.