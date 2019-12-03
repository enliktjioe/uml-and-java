------------------------------ MODULE payment ------------------------------
EXTENDS Integers, TLC
 
(* --algorithm payment
 
variables
  card_is_valid = TRUE,
  locked = FALSE,
  investigating = FALSE,
  ghost_debt = 0
 
define
  LimitOutstandingDebt == ghost_debt <= 1 (* Replace with own condition. *)
  may_use_service == ~locked  (* Replace with own condition. *)
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
        ghost_debt := ghost_debt + 1;
      end if;
  or
    (* If the user has an invalid card, they can get a new card from the bank
       and add a new payment method. We will then be able to clear any outstanding
       transactions and unlock their account. *)
    when ~card_is_valid;
    UpdateCard:
      card_is_valid := TRUE;
      locked := FALSE;
      ghost_debt := 0;
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
    \* if ~card_is_valid then
    locked := TRUE;
    \* end if
    investigating := FALSE;
  end while
end process
 
end algorithm *)
\* BEGIN TRANSLATION
VARIABLES card_is_valid, locked, investigating, ghost_debt, pc

(* define statement *)
LimitOutstandingDebt == ghost_debt <= 1
may_use_service == ~locked


vars == << card_is_valid, locked, investigating, ghost_debt, pc >>

ProcSet == {"time"} \cup {"user"} \cup {"backoffice"}

Init == (* Global variables *)
        /\ card_is_valid = TRUE
        /\ locked = FALSE
        /\ investigating = FALSE
        /\ ghost_debt = 0
        /\ pc = [self \in ProcSet |-> CASE self = "time" -> "Time"
                                        [] self = "user" -> "User"
                                        [] self = "backoffice" -> "CheckTransaction"]

Time == /\ pc["time"] = "Time"
        /\ card_is_valid' = FALSE
        /\ pc' = [pc EXCEPT !["time"] = "Done"]
        /\ UNCHANGED << locked, investigating, ghost_debt >>

time == Time

User == /\ pc["user"] = "User"
        /\ \/ /\ may_use_service
              /\ pc' = [pc EXCEPT !["user"] = "TakeRide"]
           \/ /\ ~card_is_valid
              /\ pc' = [pc EXCEPT !["user"] = "UpdateCard"]
        /\ UNCHANGED << card_is_valid, locked, investigating, ghost_debt >>

TakeRide == /\ pc["user"] = "TakeRide"
            /\ IF ~card_is_valid
                  THEN /\ investigating' = TRUE
                       /\ ghost_debt' = ghost_debt + 1
                  ELSE /\ TRUE
                       /\ UNCHANGED << investigating, ghost_debt >>
            /\ pc' = [pc EXCEPT !["user"] = "User"]
            /\ UNCHANGED << card_is_valid, locked >>

UpdateCard == /\ pc["user"] = "UpdateCard"
              /\ card_is_valid' = TRUE
              /\ locked' = FALSE
              /\ ghost_debt' = 0
              /\ pc' = [pc EXCEPT !["user"] = "User"]
              /\ UNCHANGED investigating

user == User \/ TakeRide \/ UpdateCard

CheckTransaction == /\ pc["backoffice"] = "CheckTransaction"
                    /\ investigating
                    /\ locked' = TRUE
                    /\ investigating' = FALSE
                    /\ pc' = [pc EXCEPT !["backoffice"] = "CheckTransaction"]
                    /\ UNCHANGED << card_is_valid, ghost_debt >>

backoffice == CheckTransaction

Next == time \/ user \/ backoffice

Spec == Init /\ [][Next]_vars

\* END TRANSLATION
=============================================================================
\* Modification History
\* Last modified Tue Dec 03 17:29:39 EET 2019 by enlik
\* Created Tue Dec 03 16:27:14 EET 2019 by enlik
