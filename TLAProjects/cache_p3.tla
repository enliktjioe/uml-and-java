------------------------------ MODULE cache_p3 ------------------------------
EXTENDS Integers, Sequences, TLC
CONSTANT ResourceCap, MaxConsumerReq
 
ASSUME ResourceCap > 0
ASSUME MaxConsumerReq \in 1..ResourceCap
 
(*--algorithm cache
variables
  reserved = 0,
  resources_left = ResourceCap;
define
  ResourceInvariant == resources_left >= 0
  MayAlwaysUse == <>(pc["actor1"]="UseResources")
end define;
 
fair process actor \in {"actor1", "actor2"}
variables
  resources_needed \in 1..MaxConsumerReq
begin
  WaitForResources:
while TRUE do
  when resources_left - reserved >= resources_needed;
  reserved := reserved + resources_needed;
  UseResources:
    while resources_needed > 0 do
      resources_left := resources_left - 1;
      resources_needed := resources_needed - 1;
      reserved := reserved - 1
    end while;
  with x \in 1..MaxConsumerReq do
    resources_needed := x;
  end with;
end while;
end process;
 
fair process time = "time"
begin
  Tick:
    resources_left := ResourceCap;
    goto Tick;
end process;
end algorithm; *)
\* BEGIN TRANSLATION
VARIABLES reserved, resources_left, pc

(* define statement *)
ResourceInvariant == resources_left >= 0
MayAlwaysUse == <>(pc["actor1"]="UseResources")

VARIABLE resources_needed

vars == << reserved, resources_left, pc, resources_needed >>

ProcSet == ({"actor1", "actor2"}) \cup {"time"}

Init == (* Global variables *)
        /\ reserved = 0
        /\ resources_left = ResourceCap
        (* Process actor *)
        /\ resources_needed \in [{"actor1", "actor2"} -> 1..MaxConsumerReq]
        /\ pc = [self \in ProcSet |-> CASE self \in {"actor1", "actor2"} -> "WaitForResources"
                                        [] self = "time" -> "Tick"]

WaitForResources(self) == /\ pc[self] = "WaitForResources"
                          /\ resources_left - reserved >= resources_needed[self]
                          /\ reserved' = reserved + resources_needed[self]
                          /\ pc' = [pc EXCEPT ![self] = "UseResources"]
                          /\ UNCHANGED << resources_left, resources_needed >>

UseResources(self) == /\ pc[self] = "UseResources"
                      /\ IF resources_needed[self] > 0
                            THEN /\ resources_left' = resources_left - 1
                                 /\ resources_needed' = [resources_needed EXCEPT ![self] = resources_needed[self] - 1]
                                 /\ reserved' = reserved - 1
                                 /\ pc' = [pc EXCEPT ![self] = "UseResources"]
                            ELSE /\ \E x \in 1..MaxConsumerReq:
                                      resources_needed' = [resources_needed EXCEPT ![self] = x]
                                 /\ pc' = [pc EXCEPT ![self] = "WaitForResources"]
                                 /\ UNCHANGED << reserved, resources_left >>

actor(self) == WaitForResources(self) \/ UseResources(self)

Tick == /\ pc["time"] = "Tick"
        /\ resources_left' = ResourceCap
        /\ pc' = [pc EXCEPT !["time"] = "Tick"]
        /\ UNCHANGED << reserved, resources_needed >>

time == Tick

Next == time
           \/ (\E self \in {"actor1", "actor2"}: actor(self))

Spec == /\ Init /\ [][Next]_vars
        /\ \A self \in {"actor1", "actor2"} : WF_vars(actor(self))
        /\ WF_vars(time)

\* END TRANSLATION

=============================================================================
\* Modification History
\* Last modified Tue Dec 03 17:04:18 EET 2019 by enlik
\* Created Tue Nov 26 16:22:10 EET 2019 by enlik
