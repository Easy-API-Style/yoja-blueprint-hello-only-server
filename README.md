# yoja-blueprint-hello-only-server

Minimal blueprint for the [yoja](https://github.com/easygoingapi) framework. It wires a single REST endpoint to a frontend page in the fewest possible lines — the starting point for any new yoja project.

## What it shows

| Layer | File | Concept |
|---|---|---|
| Backend | `Hello.java` | `HttpRouter` setup, serving a folder as static resources, declaring a REST route |
| Frontend | `index.html` | yoja-web page, controller binding via `yw-controler` |
| Frontend | `HelloControler.js` | `httpClient.get()`, DOM update with `section.firstTag()` |

## Project structure

```
yoja-blueprint-hello-only-server/
├── src/main/java/blueprint/
│   └── Hello.java                 # Entry point: router + HTTP server
└── webapp/blueprint/
    ├── index.html                 # Single page
    ├── HelloControler.js          # Frontend controller
    └── yoja/                      # yoja-web framework JS files (served as static files)
        └── YojaWeb-1.0.0.js
```

## Prerequisites

- Java 25+
- yoja dependencies:
  - `com.easygoingapi:yoja-http-server:VERSION`

## Gradle commands

```bash
# Start the application (dev mode, uses logback-test.xml)
./gradlew run

# Build the project (compile + resources)
./gradlew build

# Package as a self-contained ZIP with startup scripts
./gradlew distZip

# Clean build outputs
./gradlew clean

# List all available tasks
./gradlew tasks
```

Once running, open: **http://localhost:8080/index.html**

The page calls `GET /hello` and displays the response (`hello, yoja`).

## Why folder-based serving

Serving the webapp from a plain directory (instead of bundling resources inside the JAR) means every JS, CSS, and HTML file is read directly from the filesystem on each request. Any change to a frontend file is visible immediately in the browser — no server restart, no rebuild, no packaging step. The Java backend only needs to restart when the server-side code changes.

This makes the development loop for frontend work very fast: edit a file, refresh the browser, see the result.

## How it works

**Backend** (`Hello.java`) builds an `HttpRouter` that:
1. Serves the entire `webapp/blueprint/` folder as static resources at `/*` — including the yoja-web framework JS files
2. Exposes `GET /hello` returning a plain-text response

```java
HttpRouter.builder()
    .contentType("js", "application/javascript")
    .contentType("html", "text/html")
    .webResource(WebApp.folder(folderWebapp), "/*")
    .webService(HttpMethod.GET, "/hello", r -> r.response().send("hello, yoja"))
    .build();
```

The first program argument sets the webapp folder path. If omitted, it defaults to `webapp/blueprint` resolved as an absolute path from the working directory.

| Argument | Default | Description |
|---|---|---|
| `args[0]` | `webapp/blueprint` (absolute) | Path to the directory containing the frontend files to serve |

```bash
# Default — works when launched from the project root
./gradlew run

# Custom path
./gradlew run --args="/opt/myapp/webapp"
```

**Frontend** (`HelloControler.js`) runs when the page is ready, calls the endpoint, and writes the response into the DOM:

```js
section.pageReady(() => {
    yojaWebApi.httpClient
        .get({ url: '/hello' })
        .then(res => {
            section.firstTag('.message').textContent = res.body;
        });
});
```

## Distribution (deployment)

```bash
./gradlew distZip
unzip build/distributions/yoja-blueprint-hello-only-server.zip -d /opt/yoja-hello
/opt/yoja-hello/yoja-blueprint-hello-only-server/bin/yoja-blueprint-hello-only-server /path/to/webapp/blueprint
```

The webapp folder path must be passed as an argument at runtime since it is not bundled into the JAR.
