alter table te_employment_history add employee_last_updated date;
 
update te_employment_history set employee_last_updated = now() 
 	where eh_trainer_id IN (
 		select trainer_id
 		from te_trainers 
 		where trainer_return_complete = true);