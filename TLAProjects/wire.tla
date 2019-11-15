-------------------------------- MODULE wire --------------------------------


=============================================================================
\* Modification History
\* Last modified Fri Nov 15 15:34:38 EET 2019 by enlik
\* Created Fri Nov 15 15:30:23 EET 2019 by enlik
EXTENDS Integers
(*--algorithm wire
variables people, acc, sender, receiver, amount;
 
begin
\*    skip;
    Withdraw:
        acc[sender] := ...;
    Deposit:
        acc[receiver] := ...;

define
\*    people = {"Whatever", "Name"}
\*    acc = [p \in people |-> n]

    NoOverdrafts == "test";
end define; 

end algorithm;*) 