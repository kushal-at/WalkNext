# Versioning Rule

Whenever a new feature is successfully implemented and built/updated, the application version number in `app/build.gradle.kts` MUST be updated according to the following mathematical rule:

1. Extract the numeric portion of the current `versionName`.
2. Find the last digit of that numeric portion.
3. Add 1 to that digit.
4. Append a letter `A`, `B`, `C`, or `D`.
5. Increment the `versionCode` by 1.

Examples:
- If current version is `0.4A`, the next version is `0.5A` (or B/C/D).
- If current version is `0.9C`, the next version is `0.10C`.
