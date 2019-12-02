------------------------------ MODULE peterson ------------------------------


=============================================================================
\* Modification History
\* Last modified Tue Nov 26 17:01:20 EET 2019 by enlik
\* Created Tue Nov 26 16:45:19 EET 2019 by enlik

EXTENDS TLC, Integers
CONSTANT Threads
 
(*--algorithm peterson
 
variables
  flag = [t \in Threads |-> FALSE],
  turn \in Threads;
 
define
OnlyOneCritical == \A t1, t2 \in Threads: pc[t1] = "CS" /\ pc[t2] = "CS" => t1 = t2
NoLiveLocks == \A t \in Threads: <>(pc[t] = "CS")
end define
 
fair process thread \in Threads
variable other = CHOOSE t \in Threads : t /= self
begin
    P1: flag[self] := TRUE;
    P2: turn := other;
    P3: await ~flag[other] /\ turn = self;
    CS: skip;
    P4: flag[self] := FALSE;
    P5: goto P1;
end process;
 
end algorithm; *)
\* BEGIN TRANSLATION
VARIABLES flag, turn, pc

(* define statement *)
OnlyOneCritical == \A t1, t2 \in Threads: pc[t1] = "CS" /\ pc[t2] = "CS" => t1 = t2
NoLiveLocks == \A t \in Threads: <>(pc[t] = "CS")

VARIABLE other

vars == << flag, turn, pc, other >>

ProcSet == (Threads)

Init == (* Global variables *)
        /\ flag = [t \in Threads |-> FALSE]
        /\ turn \in Threads
        (* Process thread *)
        /\ other = [self \in Threads |-> CHOOSE t \in Threads : t /= self]
        /\ pc = [self \in ProcSet |-> "P1"]

P1(self) == /\ pc[self] = "P1"
            /\ flag' = [flag EXCEPT ![self] = TRUE]
            /\ pc' = [pc EXCEPT ![self] = "P2"]
            /\ UNCHANGED << turn, other >>

P2(self) == /\ pc[self] = "P2"
            /\ turn' = other[self]
            /\ pc' = [pc EXCEPT ![self] = "P3"]
            /\ UNCHANGED << flag, other >>

P3(self) == /\ pc[self] = "P3"
            /\ ~flag[other[self]] /\ turn = self
            /\ pc' = [pc EXCEPT ![self] = "CS"]
            /\ UNCHANGED << flag, turn, other >>

CS(self) == /\ pc[self] = "CS"
            /\ TRUE
            /\ pc' = [pc EXCEPT ![self] = "P4"]
            /\ UNCHANGED << flag, turn, other >>

P4(self) == /\ pc[self] = "P4"
            /\ flag' = [flag EXCEPT ![self] = FALSE]
            /\ pc' = [pc EXCEPT ![self] = "P5"]
            /\ UNCHANGED << turn, other >>

P5(self) == /\ pc[self] = "P5"
            /\ pc' = [pc EXCEPT ![self] = "P1"]
            /\ UNCHANGED << flag, turn, other >>

thread(self) == P1(self) \/ P2(self) \/ P3(self) \/ CS(self) \/ P4(self)
                   \/ P5(self)

(* Allow infinite stuttering to prevent deadlock on termination. *)
Terminating == /\ \A self \in ProcSet: pc[self] = "Done"
               /\ UNCHANGED vars

Next == (\E self \in Threads: thread(self))
           \/ Terminating

Spec == /\ Init /\ [][Next]_vars
        /\ \A self \in Threads : WF_vars(thread(self))

Termination == <>(\A self \in ProcSet: pc[self] = "Done")

\* END TRANSLATION
