# forkbridge

`forkbridge` is a small, composable Clojure library for spawning and interacting with subprocesses.

It provides a lispy, functional interface over `ProcessBuilder` that lets you:
- Start and monitor long-lived processes
- Read and write lines from standard input/output
- Gracefully or forcefully terminate subprocesses
- Wait for exit and capture exit codes

The API is minimal, transparent, and structured as a functional map with closures — no global state or Java interop leaks.

## Example

```clojure
(require '[forkbridge.core :refer [start-process]])

(def p (start-process ["clojure"]))

((:write-line p) "(+ 1 2)")
(println ((:read-line-stdout p))) ; => "user=> 3"

((:write-line p) "(System/exit 0)")
((:wait p))
(println ((:exit-value p))) ; => 0
```

## API

Each call to `start-process` returns a map with the following keys:

| Key                | Description                                      |
|---------------------|--------------------------------------------------|
| `:alive?`           | Returns true if the process is still running     |
| `:exit-value`       | Returns the exit code (or `nil` if still running)|
| `:write-line`       | Writes a line to stdin, appending a newline      |
| `:read-line-stdout` | Reads a full line from stdout                    |
| `:read-line-stderr` | Reads a full line from stderr                    |
| `:sigterm!`         | Sends SIGTERM to terminate the process gracefully|
| `:sigkill!`         | Sends SIGKILL to forcefully terminate the process|
| `:wait`             | Blocks until the process exits                   |

Each of these is a function or thunk — to use them, call like so:

```clojure
((:write-line p) "(+ 2 2)")
((:read-line-stdout p)) ; => "user=> 4"
```

## Use cases

- Embedding REPLs and long-running interpreters
- Orchestrating tools like Ansible or Bash scripts
- Interactive automation with line-by-line output control
- Safe process management in long-running systems

## Testing

The test suite includes:

- Lifecycle validation (`:alive?`, `:exit-value`)
- Interactive I/O with a Clojure REPL subprocess
- Signal termination and proper exit code capture

See `forkbridge.core-test` for working examples.

## Philosophy

This library favors:
- Minimal state
- Explicit, functional APIs
- Unix-style semantics
- Composability and testability

No global mutation, no macros, no surprise side effects.

## Comparison with babashka.process

While [`babashka.process`](https://github.com/babashka/process) is a powerful and flexible library for managing subprocesses, `forkbridge` offers a different tradeoff, with a focus on minimalism.

In the future, we'll add some more features to `forkbridge` that will make it better at declarative interactivity with subprocesses.

### Strengths of forkbridge

- **Unix-style semantics**: Explicit naming like `:sigterm!`, `:sigkill!`, `:read-line-stdout`, etc. matches shell behavior clearly and transparently.
- **Controlled and explicit I/O**: Line-based readers and writers with newline-handling by default.

### When to use `babashka.process` instead

- **You need piping or redirection across multiple subprocesses** (e.g., `ls | grep foo`).
- **You want tight integration with the Babashka runtime** or use it in scripting contexts.
- **You need automatic stream handling** (e.g., `:inherit`, `:string`, or background execution).
- **You prefer a higher-level API** with more built-in options for capturing and manipulating process output.

## Licence

MIT Licence. See [LICENCE](LICENCE) for details.
