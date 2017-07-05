package ie.turfclub.service.stableStaff;



import ie.turfclub.model.stableStaff.TeFile;

import java.util.List;

public interface FileService {

	public boolean hasFiles(Integer userId);
	public List<TeFile> list(Integer userId);
	public void create(TeFile file);
	public TeFile get(Long id);
	public void delete(TeFile file);
	
}
