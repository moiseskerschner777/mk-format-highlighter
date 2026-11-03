# Project Instructions

## 1. Code Readability
- The code must never contain comments.
- Code must be self-explanatory through meaningful class, method, and variable names.
- If a piece of logic requires a comment to explain its purpose, extract it into a well-named method.
- Method names must describe what they do, not how they do it.
- Avoid abbreviations unless they are widely recognized within the domain.
- Avoid magic numbers and hardcoded strings. Use named constants or configuration.
- Prefer simple, readable implementations over clever or unnecessarily complex solutions.
- Avoid deeply nested conditionals. Prefer guard clauses and early returns.
- Never sacrifice readability for fewer lines of code.

## 2. Class Responsibilities
- Every class must have a single, clearly defined responsibility.
- Business rules and use cases must have a dedicated entry-point class.
- Entry-point classes must orchestrate business operations rather than implement unrelated technical details.
- If supporting logic is required, such as date formatting, string manipulation, or data conversion, extract it into a separate class with an explicit responsibility.
- Helper classes must have specific names reflecting their purpose. Avoid generic names such as CommonUtils, Helper, or Manager.
- Do not create unnecessary classes for trivial operations.
- Avoid classes that accumulate unrelated methods.
- Prefer composition over inheritance.

## 3. Methods
- Each method must perform one logical operation.
- Extract complex conditions into descriptively named methods.
- Methods must remain focused and reasonably small, but never split them solely to reduce line count.
- Avoid boolean parameters that significantly change a method's behavior. Prefer explicit methods or types.
- Avoid methods with excessive parameters. Group related data into meaningful objects.
- Methods must not produce unexpected side effects.
- Prefer returning results over mutating external state.
- Avoid duplicating logic across methods or classes.

## 4. Business Logic
- Business rules must be isolated from infrastructure and framework-specific concerns whenever practical.
- Each use case must expose a clear, explicit entry point.
- Use-case classes should coordinate domain objects, services, and dependencies.
- Technical concerns such as formatting, serialization, database access, and HTTP communication must not be mixed directly with business rules.
- Business validations must have descriptive names and explicit failure behavior.
- Domain terminology must be reflected consistently in class and method names.
- Do not introduce abstractions without a concrete requirement.
- Keep business rules testable without requiring an entire application to start.

## 5. Dependencies & Architecture
- Dependencies must point toward the appropriate architectural layer.
- Domain objects must not depend on controllers, persistence frameworks, or HTTP-specific types.
- Use interfaces when they establish meaningful architectural boundaries or support necessary polymorphism.
- Do not create an interface for every class automatically.
- Constructor injection must be preferred over field injection.
- Avoid circular dependencies.
- Infrastructure implementations must not dictate business behavior.
- Avoid introducing design patterns unless they solve an actual problem.

## 6. Data & State
- Prefer immutable objects when mutation is unnecessary.
- Minimize shared mutable state.
- Validate inputs at the appropriate system boundary.
- Avoid passing unstructured maps when a meaningful typed object can be created.
- Use domain-specific types when primitive values would introduce ambiguity.
- Distinguish between domain objects, persistence entities, and API DTOs when their responsibilities differ.
- Do not expose internal mutable collections directly.

## 7. Error Handling
- Never silently ignore exceptions.
- Exceptions must communicate meaningful failures.
- Use specific exception types instead of generic exceptions when appropriate.
- Do not use exceptions for ordinary business flow.
- Handle exceptions at the layer responsible for deciding how to respond.
- Preserve the original cause when wrapping exceptions.
- Avoid logging and rethrowing the same exception at multiple layers.
