# forkbridge

`forkbridge` is a small, well-tested Clojure library for spawning and interacting with subprocesses.

It provides a simple interface over Java's `ProcessBuilder` that lets you:
- Start and monitor long-lived processes
- Read and write lines from standard input/output in a safe, predictable way
- Gracefully or forcefully terminate subprocesses
- Wait for exit and capture exit codes

The API is minimal, transparent, and structured as a functional map with closures.

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


| Key                  | Description                                                                 |
|----------------------|-----------------------------------------------------------------------------|
| `:alive?`            | Returns `true` if the process is still running                              |
| `:exit-value`        | Returns the exit code (or `nil` if the process hasn't exited yet)           |
| `:write-line!`       | Writes a line to the process's stdin (with a newline); returns `true` if successful, `nil` if the process is dead |
| `:read-line-stdout!` | Blocks until a full line is available from stdout; returns the line, or `nil` if the process is dead |
| `:read-line-stderr!` | Blocks until a full line is available from stderr; returns the line, or `nil` if the process is dead |
| `:sigterm!`          | Sends SIGTERM to request graceful termination (non-blocking)                |
| `:sigkill!`          | Sends SIGKILL to forcibly terminate the process (non-blocking)              |
| `:wait!`             | Blocks until the process exits                                              |


Each of these is a function or thunk — to use them, call them like so:

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
- Verification of blocking operations (reading from `stdout`/`stderr`)

See `forkbridge.core-test` for working examples.

## Philosophy

This library favours:
- Minimal state
- UNIX-style semantics
- Composability and testability

## Comparison with babashka.process

While [`babashka.process`](https://github.com/babashka/process) is a powerful and flexible library for managing subprocesses, `forkbridge` offers a different tradeoff, with a focus on minimalism.

In the future, we may add some more features to `forkbridge`. We're still coming up with new ideas, but for now this is a usable base that just gets out of the way and provides some safety cushioning around interacting with subprocesses.

### Strengths of `forkbridge`

- **UNIX-style semantics**: Explicit naming like `:sigterm!`, `:sigkill!`, `:read-line-stdout`, etc. matches shell behavior clearly and transparently.
- **Controlled and explicit I/O**: Line-based readers and writers with newline-handling by default.

### When to use `babashka.process` instead

- **You want tight integration with the Babashka runtime** or use it in scripting contexts.
- **You need piping or redirection across multiple subprocesses** (e.g., `ls | grep foo`).
- **You need automatic stream handling** (e.g., `:inherit`, `:string`, or background execution).
- **You prefer a higher-level API** with more built-in options for capturing and manipulating process output.

## Licence

MIT Licence. See [LICENCE](LICENCE) for details.
