-------------------------------- MODULE wire --------------------------------


=============================================================================
\* Modification History
\* Last modified Fri Nov 15 17:16:52 EET 2019 by enlik
\* Created Fri Nov 15 15:30:23 EET 2019 by enlik
EXTENDS Integers
(*--algorithm wire

variables
    people = {"Enlik", "Tyrion"},
    acc = [p \in people |-> 8],
    sender = "Enlik",
    receiver = "Tyrion",
\*    amount = 1000
    amount \in 1..10

define
\*    NoOverdrafts == TRUE \* 
    NoOverdrafts == \A x \in people : acc[p] >= 0
end define
    

begin
    Withdraw:
        acc[sender] := acc[sender] - amount;
    Deposit:
        acc[receiver] := acc[receiver] + amount;
end algorithm;

*)

\* BEGIN TRANSLATION
VARIABLES people, acc, sender, receiver, amount, pc

(* define statement *)
NoOverdrafts == \A x \in people : acc[p] >= 0


vars == << people, acc, sender, receiver, amount, pc >>

Init == (* Global variables *)
        /\ people = {"Enlik", "Tyrion"}
        /\ acc = [p \in people |-> 8]
        /\ sender = "Enlik"
        /\ receiver = "Tyrion"
        /\ amount \in 1..10
        /\ pc = "Withdraw"

Withdraw == /\ pc = "Withdraw"
            /\ acc' = [acc EXCEPT ![sender] = acc[sender] - amount]
            /\ pc' = "Deposit"
            /\ UNCHANGED << people, sender, receiver, amount >>

Deposit == /\ pc = "Deposit"
           /\ acc' = [acc EXCEPT ![receiver] = acc[receiver] + amount]
           /\ pc' = "Done"
           /\ UNCHANGED << people, sender, receiver, amount >>

(* Allow infinite stuttering to prevent deadlock on termination. *)
Terminating == pc = "Done" /\ UNCHANGED vars

Next == Withdraw \/ Deposit
           \/ Terminating

Spec == Init /\ [][Next]_vars

Termination == <>(pc = "Done")

\* END TRANSLATION
