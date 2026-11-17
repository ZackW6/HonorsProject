<h1>18000 Honors Project Overview</h1>

<img width="968" height="692" alt="image" src="https://github.com/user-attachments/assets/0a478152-00cd-41a7-ac2e-6faf7465b6dc" />

**Class Elaboration: (Only for complex classes that need it)**

Connection.java -> 
* works by keeping a hashmap of all users, which is then again another hashmap which stores the most recent user inputs. 
* Also stores the most recent sent server outputs, like lastSentTime, viewableGameElements, and species
* Sending data compresses it, such that each message is sub 50 kilobytes (normally), so the users experience can be clean.
* Sending data is done for all users in a loop, rather than individual threads. Spatial hashmaps greatly improve efficiency being resorted once per 50 individuals than 50 times.
* Receiving data is handled by Spring Boot stuff, but is functionally threaded.

Global.java ->
* For testing purposes, it creates 4000 creatures at the start with random velocities.
* Contains a thread which resorts the spatial hashmap, and sends near creatures to each other to compute collisions.
* Finally has a method to send user viewable creatures, again from the spatial hashmap.

Creature.java -> 
* Extends CirclePhysics (Even for triangles), which runs collisions
* tickLife checks for collisions, and (TBD) will perform actions such as eating food, duplicating, etc...

main.js ->
* Takes inputs of: zoom, drag, arrowkeys.
* Sends data through usermade endpoints, unique to each, such that user input can be processed asynchronous to sever outputs.
* decompresses inputs
* Sends input through a function to draw shapes onto canvas.

NeuralNet.java (TBD) ->
* Likely will receive closest creature data of color and postion, so it may go toward or away based on color.
* Likely will output desired vectors, maybe also whether to duplicate, maybe whether to attempt damaging

Genome.java (TBD) ->
* Enum like class where certain traits are defined
* Perhaps traits are also told how to act in this class, but maybe also just in creature.

**Core:** 
* Creatures perform actions such as duplication, movement, consumption, even if on a non UI level format.
* Simulated/real players who can modify to some level the environment and/or genomes of the creatures.
  
**Silver qualifiers:** 
* Ability to be networked and run from many computers
* GUI so users can see creatures and interact
* Independent threading of physics and graphics and communications

**Gold qualifiers:** 
* Neural networks rather than just closest follow for creatures
* Creatures ability to behave differently based on genome and/or neural makeup.
* Save files on running computer for user made creatures.
