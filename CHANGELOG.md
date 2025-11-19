### Now releasing 1.0.9:

-> Fabric: Fix an issue that rendering items implemented with the newly added IBlockEntityItem interface were not actually rendered.

-> Forge: All event bus listeners now are created explicitly, thus saving some computation time during event registration.

-> Added new events that fire during when the registries are ready to be consumed. 

-> Fixed an issue where all the setup events were not adding the work to do to the queue's tail.