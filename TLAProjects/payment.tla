------------------------------ MODULE payment ------------------------------


=============================================================================
\* Modification History
\* Last modified Tue Dec 03 16:27:18 EET 2019 by enlik
\* Created Tue Dec 03 16:27:14 EET 2019 by enlik
EXTENDS Integers, TLC
 
(* --algorithm payment
 
variables
  card_is_valid = TRUE,
  locked = FALSE,
  investigating = FALSE
 
define
  LimitOutstandingDebt == TRUE (* Replace with own condition. *)
  may_use_service == TRUE  (* Replace with own condition. *)
end define
 
(* As time passes, our credit card may expire: *)
process time = "time"
begin Time:
  card_is_valid := FALSE;
end process
 
(* Our user can either use or service or update an invalid card
   with a new one issued from the bank. *)
process user = "user"
begin User:
  while TRUE do
  either
    (* If the user's account is clear (you have to define that),
       they may use our service. *)
    when may_use_service;
    TakeRide:
      (* If the payment fails, we will flag this for investigation. *)
      if ~card_is_valid then
        investigating := TRUE;
      end if;
  or
    (* If the user has an invalid card, they can get a new card from the bank
       and add a new payment method. We will then be able to clear any outstanding
       transactions and unlock their account. *)
    when ~card_is_valid;
    UpdateCard:
      card_is_valid := TRUE;
      locked := FALSE;
  end either;
  end while
end process
 
(* The back office takes a look at failed or suspicious transactions.
   We only model failures due to expire cards, so the investigation
   will always conclude that the client was at fault and lock their account. *)
process backoffice = "backoffice"
begin CheckTransaction:
  while TRUE do
    await investigating;
    locked := TRUE;
    investigating := FALSE;
  end while
end process
 
end algorithm *)
\* BEGIN TRANSLATION
VARIABLES card_is_valid, locked, investigating, pc

(* define statement *)
LimitOutstandingDebt == TRUE
may_use_service == TRUE


vars == << card_is_valid, locked, investigating, pc >>

ProcSet == {"time"} \cup {"user"} \cup {"backoffice"}

Init == (* Global variables *)
        /\ card_is_valid = TRUE
        /\ locked = FALSE
        /\ investigating = FALSE
        /\ pc = [self \in ProcSet |-> CASE self = "time" -> "Time"
                                        [] self = "user" -> "User"
                                        [] self = "backoffice" -> "CheckTransaction"]

Time == /\ pc["time"] = "Time"
        /\ card_is_valid' = FALSE
        /\ pc' = [pc EXCEPT !["time"] = "Done"]
        /\ UNCHANGED << locked, investigating >>

time == Time

User == /\ pc["user"] = "User"
        /\ \/ /\ may_use_service
              /\ pc' = [pc EXCEPT !["user"] = "TakeRide"]
           \/ /\ ~card_is_valid
              /\ pc' = [pc EXCEPT !["user"] = "UpdateCard"]
        /\ UNCHANGED << card_is_valid, locked, investigating >>

TakeRide == /\ pc["user"] = "TakeRide"
            /\ IF ~card_is_valid
                  THEN /\ investigating' = TRUE
                  ELSE /\ TRUE
                       /\ UNCHANGED investigating
            /\ pc' = [pc EXCEPT !["user"] = "User"]
            /\ UNCHANGED << card_is_valid, locked >>

UpdateCard == /\ pc["user"] = "UpdateCard"
              /\ card_is_valid' = TRUE
              /\ locked' = FALSE
              /\ pc' = [pc EXCEPT !["user"] = "User"]
              /\ UNCHANGED investigating

user == User \/ TakeRide \/ UpdateCard

CheckTransaction == /\ pc["backoffice"] = "CheckTransaction"
                    /\ investigating
                    /\ locked' = TRUE
                    /\ investigating' = FALSE
                    /\ pc' = [pc EXCEPT !["backoffice"] = "CheckTransaction"]
                    /\ UNCHANGED card_is_valid

backoffice == CheckTransaction

Next == time \/ user \/ backoffice

Spec == Init /\ [][Next]_vars

\* END TRANSLATION
