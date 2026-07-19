# Math in chat

Math in chat is a client-side Fabric mod for Minecraft that shows a gray ghost preview of the result while you type a math expression into the chat box. It never modifies or sends anything — pressing Enter sends exactly what you typed, so it works on any server.

```
5x5                →  = 25
2*(3+4)            →  = 14
10/4               →  = 2.5
1,5+1              →  = 2.5
1m+500k            →  = 1.5m (1500000)
/gamemode creative →  no preview, command untouched
```

## Features

- Operators `+ - * / % ^`, with `x`, `X`, `×` as multiply and `÷` as divide
- Parentheses and unary minus
- Decimals with `.` or `,`
- Magnitude suffixes `k` / `m` / `b` (1,000 / 1,000,000 / 1,000,000,000), shown compact plus full when exact
- Bare numbers and non-math text are left alone — no ghost is shown

## Installation

- Minecraft 26.1.2 or 26.2
- Fabric Loader >= 0.19.3
- Java 25 or newer

Client-only: drop the jar into your `mods` folder. No Fabric API required.

## Building

Requires JDK 26.

```
./gradlew build
```

The jar lands in `build/libs/`. Run the parser test suite with:

```
./gradlew test
```

## License

MIT, see [LICENSE](LICENSE).
