------------------------------- MODULE ferry -------------------------------

(*--algorithm ferry
variables 
    left = {"Man", "Goat", "Cabbage", "Wolf"},
    right = {}
    
define
    D1 == {"Goat", "Cabbage"}
    D2 == {"Goat", "Wolf"}
    Eaten == ((D1 \subseteq left \/ D2 \subseteq left) /\ "Man" \in right) \/
             ((D1 \subseteq right \/ D2 \subseteq right) /\ "Man" \in left)
    Happy == {"Man", "Goat", "Cabbage", "Wolf"} = right
end define;
 
process ferryman = "go"

variables
begin
  Left:
    with thing \in left do
        left := left \ {thing};
        right := right \cup {thing}
    end with;
  Right:
    with thing \in right do
        right := right \ {thing};
        left := left \cup {thing}
    end with;
    
    if ~({"Goat", "Cabbage", "Wolf"} \subseteq right) then goto Left end if
  
end process;
end algorithm; *)
\* BEGIN TRANSLATION
VARIABLES left, right, pc

(* define statement *)
D1 == {"Goat", "Cabbage"}
D2 == {"Goat", "Wolf"}
Eaten == ((D1 \subseteq left \/ D2 \subseteq left) /\ "Man" \in right) \/
         ((D1 \subseteq right \/ D2 \subseteq right) /\ "Man" \in left)
Happy == {"Man", "Goat", "Cabbage", "Wolf"} = right


vars == << left, right, pc >>

ProcSet == {"go"}

Init == (* Global variables *)
        /\ left = {"Man", "Goat", "Cabbage", "Wolf"}
        /\ right = {}
        /\ pc = [self \in ProcSet |-> "Left"]

Left == /\ pc["go"] = "Left"
        /\ \E thing \in left:
             /\ left' = left \ {thing}
             /\ right' = (right \cup {thing})
        /\ pc' = [pc EXCEPT !["go"] = "Right"]

Right == /\ pc["go"] = "Right"
         /\ \E thing \in right:
              /\ right' = right \ {thing}
              /\ left' = (left \cup {thing})
         /\ IF ~({"Goat", "Cabbage", "Wolf"} \subseteq right')
               THEN /\ pc' = [pc EXCEPT !["go"] = "Left"]
               ELSE /\ pc' = [pc EXCEPT !["go"] = "Done"]

ferryman == Left \/ Right

(* Allow infinite stuttering to prevent deadlock on termination. *)
Terminating == /\ \A self \in ProcSet: pc[self] = "Done"
               /\ UNCHANGED vars

Next == ferryman
           \/ Terminating

Spec == Init /\ [][Next]_vars

Termination == <>(\A self \in ProcSet: pc[self] = "Done")

\* END TRANSLATION


=============================================================================
\* Modification History
\* Last modified Fri Nov 22 17:41:05 EET 2019 by enlik
\* Created Fri Nov 22 17:28:22 EET 2019 by enlik
