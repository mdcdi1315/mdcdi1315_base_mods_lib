### Now releasing 1.0.41:

-> Multiple bug fixes, including:
 - `Dictionary.Add` bug: Add allowed entries to overwrite values, while the Add contract is not to do that.
 - `IDestroyedOnUseEvent` bug: The events manager did not approriately checked for this kind of event,
 so it caused other events to fail.
These bug fixes will come in as version 1.0.41 for 1.21.1 and 1.21.5.

-> Added a new diagnostics API for users to be informed about minor issues or warnings,
or anything else mods to judge necessary.

-> Cleaned up the rendering code, the about screens now all use an optimized layout logic.

-> Added a new `ClientAssetsReloadedEvent` that injects just right before reloading has finished,
so that users know when to load assets and when the client has gone into the main screen.

-> Event cleanup