-------------------------------- MODULE wire --------------------------------


EXTENDS Integers
(*--algorithm wire

variables
    people = {"Jamie", "Tyrion"},
    acc = [p \in people |-> 8]
\*    sender = "Enlik",
\*    receiver = "Tyrion"
\*    amount \in 1..10

define
\*    NoOverdrafts == TRUE \* 
    NoOverdrafts == \A x \in people : acc[x] >= 0
end define

process Wire \in {1,2}

variables
    sender = "Jamie",
    receiver = "Tyrion",
    amount \in 1..acc[sender]

begin
    Withdraw:
        acc[sender] := acc[sender] - amount;
    Deposit:
        acc[receiver] := acc[receiver] + amount;
        end process
end algorithm;

*)

\* BEGIN TRANSLATION
VARIABLES people, acc, pc

(* define statement *)
NoOverdrafts == \A x \in people : acc[x] >= 0

VARIABLES sender, receiver, amount

vars == << people, acc, pc, sender, receiver, amount >>

ProcSet == ({1,2})

Init == (* Global variables *)
        /\ people = {"Jamie", "Tyrion"}
        /\ acc = [p \in people |-> 8]
        (* Process Wire *)
        /\ sender = [self \in {1,2} |-> "Jamie"]
        /\ receiver = [self \in {1,2} |-> "Tyrion"]
        /\ amount \in [{1,2} -> 1..acc[sender[CHOOSE self \in  {1,2} : TRUE]]]
        /\ pc = [self \in ProcSet |-> "Withdraw"]

Withdraw(self) == /\ pc[self] = "Withdraw"
                  /\ acc' = [acc EXCEPT ![sender[self]] = acc[sender[self]] - amount[self]]
                  /\ pc' = [pc EXCEPT ![self] = "Deposit"]
                  /\ UNCHANGED << people, sender, receiver, amount >>

Deposit(self) == /\ pc[self] = "Deposit"
                 /\ acc' = [acc EXCEPT ![receiver[self]] = acc[receiver[self]] + amount[self]]
                 /\ pc' = [pc EXCEPT ![self] = "Done"]
                 /\ UNCHANGED << people, sender, receiver, amount >>

Wire(self) == Withdraw(self) \/ Deposit(self)

(* Allow infinite stuttering to prevent deadlock on termination. *)
Terminating == /\ \A self \in ProcSet: pc[self] = "Done"
               /\ UNCHANGED vars

Next == (\E self \in {1,2}: Wire(self))
           \/ Terminating

Spec == Init /\ [][Next]_vars

Termination == <>(\A self \in ProcSet: pc[self] = "Done")

\* END TRANSLATION

=============================================================================
\* Modification History
\* Last modified Fri Nov 15 17:51:50 EET 2019 by enlik
\* Created Fri Nov 15 15:30:23 EET 2019 by enlik
