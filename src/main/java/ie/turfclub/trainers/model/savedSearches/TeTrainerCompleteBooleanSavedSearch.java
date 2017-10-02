package ie.turfclub.trainers.model.savedSearches;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@DiscriminatorValue(value = "TRAINER_RETURN_COMPLETE")
public class TeTrainerCompleteBooleanSavedSearch extends TeBooleanSavedSearch{

	
}
