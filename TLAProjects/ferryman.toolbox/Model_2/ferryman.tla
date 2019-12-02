------------------------------ MODULE ferryman ------------------------------


=============================================================================
\* Modification History
\* Last modified Tue Nov 26 17:14:39 EET 2019 by enlik
\* Created Tue Nov 26 17:14:17 EET 2019 by enlik

goods == {"goat", "cabbage", "wolf", "ferryman"}
 
(* --algorithm ferryman
 
variables
  position = [item \in goods |-> "left"],
  current_item = "init" (*just for nicer trace!*)
 
define
  flip(dir) == CASE dir = "left" -> "right" [] dir = "right" -> "left"
  currentside == position["ferryman"]
  otherside == flip(currentside)
  currentside_items == {it \in goods : position[it] = currentside}
 
  Safe == \/ position["goat"] = position["wolf"]
          \/ position["goat"] = position["cabbage"]
          => position["ferryman"] = position["goat"]
  Solved == \A item \in goods : position[item] = "right"
  Solution == TRUE (* replace with your formula *)
  ThereIsNoSolution == ~Solution
end define
 
begin Ferry:
  while TRUE do
    with item \in currentside_items do (* picking ferryman to go alone *)
      position[item] := otherside || position["ferryman"] := otherside;
      current_item := item
    end with   
  end while
 
end algorithm*)

\* BEGIN TRANSLATION
VARIABLES position, current_item

(* define statement *)
flip(dir) == CASE dir = "left" -> "right" [] dir = "right" -> "left"
currentside == position["ferryman"]
otherside == flip(currentside)
currentside_items == {it \in goods : position[it] = currentside}

Safe == \/ position["goat"] = position["wolf"]
        \/ position["goat"] = position["cabbage"]
        => position["ferryman"] = position["goat"]
Solved == \A item \in goods : position[item] = "right"
Solution == TRUE
ThereIsNoSolution == ~Solution


vars == << position, current_item >>

Init == (* Global variables *)
        /\ position = [item \in goods |-> "left"]
        /\ current_item = "init"

Next == \E item \in currentside_items:
          /\ position' = [position EXCEPT ![item] = otherside,
                                          !["ferryman"] = otherside]
          /\ current_item' = item

Spec == Init /\ [][Next]_vars

\* END TRANSLATION
