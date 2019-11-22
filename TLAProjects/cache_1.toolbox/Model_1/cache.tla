------------------------------- MODULE cache -------------------------------

EXTENDS Integers, Sequences, TLC
CONSTANT ResourceCap, MaxConsumerReq
 
ASSUME ResourceCap > 0
ASSUME MaxConsumerReq \in 1..ResourceCap
 
(*--algorithm cache
variables resources_left = ResourceCap;
define
  ResourceInvariant == resources_left >= 0
end define;
 
process actor = {"actor1", "actor2"}

variables
  resources_needed \in 1..MaxConsumerReq
begin
  UseResources:
    while TRUE do
      await resources_left >= resources_needed;
      resources_left := resources_left - resources_needed; 
    end while;
end process;
 
process time = "time"
begin
  Tick:
    resources_left := ResourceCap;
    goto Tick;
end process;
end algorithm; *)
\* BEGIN TRANSLATION
VARIABLES resources_left, pc

(* define statement *)
ResourceInvariant == resources_left >= 0

VARIABLE resources_needed

vars == << resources_left, pc, resources_needed >>

ProcSet == {{"actor1", "actor2"}} \cup {"time"}

Init == (* Global variables *)
        /\ resources_left = ResourceCap
        (* Process actor *)
        /\ resources_needed \in 1..MaxConsumerReq
        /\ pc = [self \in ProcSet |-> CASE self = {"actor1", "actor2"} -> "UseResources"
                                        [] self = "time" -> "Tick"]

UseResources == /\ pc[{"actor1", "actor2"}] = "UseResources"
                /\ resources_left >= resources_needed
                /\ resources_left' = resources_left - resources_needed
                /\ pc' = [pc EXCEPT ![{"actor1", "actor2"}] = "UseResources"]
                /\ UNCHANGED resources_needed

actor == UseResources

Tick == /\ pc["time"] = "Tick"
        /\ resources_left' = ResourceCap
        /\ pc' = [pc EXCEPT !["time"] = "Tick"]
        /\ UNCHANGED resources_needed

time == Tick

Next == actor \/ time

Spec == Init /\ [][Next]_vars

\* END TRANSLATION
=============================================================================
\* Modification History
\* Last modified Fri Nov 22 16:53:11 EET 2019 by enlik
\* Created Fri Nov 22 16:22:20 EET 2019 by enlik
