# Emulator startup ANR review

Overview: **EXTERNAL_BOTTLENECK** (external starvation during cold framework class loading).

## Trace context
- Process: com.aistudio.worldclock.abxzqr, PID 15062, foreground.
- Trigger: input focus dispatch timed out after 5014ms at 2026-10-31 00:47:35 +0800.
- User-perceived: yes; Android terminated the first launch. Main thread tid=1 was captured Runnable.
- Trace: `anr-diagnostics.txt`, matching package/PID and input timeout. Snapshot captured around the reported ANR; preceding Debug Store lifecycle messages took 6.6s and 4.3s.

## Evidence and verdict
- CPU PSI avg10 80.85%; memory PSI avg10 9.54%; I/O PSI avg10 10.06%.
- system_server 93% CPU versus application 58%; wellbeing, System UI, GMS and kswapd also busy. System server had 356 major faults; app 61. Application RSS about 218MB.
- Main thread stack is in `androidx.compose.material3.ComposableSingletons$AppBarKt.<clinit>` through normal `TopAppBar` composition. No app-side I/O, wait, deadlock, or blocking database dependency is shown in that stack.
- Verdict: external starvation of cold framework/class initialization under a loaded emulator. Contributing factor: initial emulator boot plus simultaneous IDE preview/build activity. Do not infer a catalog/database bug from the app frame calling TopAppBar.

## Remediation
- Application code fix: N/A — no application code change required for the external system bottleneck.
- Application prevention: N/A — no application workaround introduced.
- Verification action: let emulator settle, finish build/preview work, relaunch and check actual UI interactions and later cold launches. Subsequent verification results are recorded in CHANGELOG.md.

Follow-up: three force-stop/cold starts reached Home (8255/8278/8811ms TotalTime). The last-ANR timestamp remained 00:47:35; subsequent theme/search/keyboard/favorites/size-change tests produced no further observed app ANR. Device display/font overrides and test favorites/preferences were restored. No application code change was made to address this external-starvation event.
