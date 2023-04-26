---------------------------- MODULE assignment1 ----------------------------
EXTENDS TLC, Integers, Sequences

(* --fair algorithm directory_analyzer

variables
    files_lines = << 10, 20, 30, 40, 50 >>,
    tot_lines_read = 0,
    mutex = 1;
define
    MutualExclusion == []~(pc["w1"] = "update_stats" /\ pc["w2"] = "update_stats")
    StarvationFreedom == /\ [](pc["w1"] = "wait" ~> <>(pc["w1"] = "update_stats")) 
                        /\ [](pc["w2"] = "wait" ~> <>(pc["w2"] = "update_stats"))
    LostUpdateFreedom == <>[](tot_lines_read = 150)
end define;

macro signal(m) begin
    m:= m+1
end macro;

macro wait(m) begin
    await m > 0;
    m:= m-1
end macro;

fair process worker \in {"w1", "w2"}
variable current_item = 0;
begin Analyse:
    while files_lines /= <<>> do
        current_item := Head(files_lines);
        files_lines := Tail(files_lines);
        wait: wait(mutex);
        update_stats: tot_lines_read := tot_lines_read + current_item;
        signal(mutex);
    end while;
end process;     

end algorithm; *)
\* BEGIN TRANSLATION (chksum(pcal) = "a97cd308" /\ chksum(tla) = "e57e1af3")
VARIABLES files_lines, tot_lines_read, mutex, pc

(* define statement *)
MutualExclusion == []~(pc["w1"] = "update_stats" /\ pc["w2"] = "update_stats")
StarvationFreedom == /\ [](pc["w1"] = "wait" ~> <>(pc["w1"] = "update_stats"))
                    /\ [](pc["w2"] = "wait" ~> <>(pc["w2"] = "update_stats"))
LostUpdateFreedom == <>[](tot_lines_read = 150)

VARIABLE current_item

vars == << files_lines, tot_lines_read, mutex, pc, current_item >>

ProcSet == ({"w1", "w2"})

Init == (* Global variables *)
        /\ files_lines = << 10, 20, 30, 40, 50 >>
        /\ tot_lines_read = 0
        /\ mutex = 1
        (* Process worker *)
        /\ current_item = [self \in {"w1", "w2"} |-> 0]
        /\ pc = [self \in ProcSet |-> "Analyse"]

Analyse(self) == /\ pc[self] = "Analyse"
                 /\ IF files_lines /= <<>>
                       THEN /\ current_item' = [current_item EXCEPT ![self] = Head(files_lines)]
                            /\ files_lines' = Tail(files_lines)
                            /\ pc' = [pc EXCEPT ![self] = "wait"]
                       ELSE /\ pc' = [pc EXCEPT ![self] = "Done"]
                            /\ UNCHANGED << files_lines, current_item >>
                 /\ UNCHANGED << tot_lines_read, mutex >>

wait(self) == /\ pc[self] = "wait"
              /\ mutex > 0
              /\ mutex' = mutex-1
              /\ pc' = [pc EXCEPT ![self] = "update_stats"]
              /\ UNCHANGED << files_lines, tot_lines_read, current_item >>

update_stats(self) == /\ pc[self] = "update_stats"
                      /\ tot_lines_read' = tot_lines_read + current_item[self]
                      /\ mutex' = mutex+1
                      /\ pc' = [pc EXCEPT ![self] = "Analyse"]
                      /\ UNCHANGED << files_lines, current_item >>

worker(self) == Analyse(self) \/ wait(self) \/ update_stats(self)

(* Allow infinite stuttering to prevent deadlock on termination. *)
Terminating == /\ \A self \in ProcSet: pc[self] = "Done"
               /\ UNCHANGED vars

Next == (\E self \in {"w1", "w2"}: worker(self))
           \/ Terminating

Spec == /\ Init /\ [][Next]_vars
        /\ WF_vars(Next)
        /\ \A self \in {"w1", "w2"} : WF_vars(worker(self))

Termination == <>(\A self \in ProcSet: pc[self] = "Done")

\* END TRANSLATION 

=============================================================================
\* Modification History
\* Last modified Mon Apr 24 22:17:26 CEST 2023 by Lorenzo
\* Created Sun Apr 23 02:58:14 CEST 2023 by Lorenzo
