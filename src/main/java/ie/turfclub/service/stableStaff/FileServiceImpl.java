package ie.turfclub.service.stableStaff;

import ie.turfclub.model.stableStaff.TeFile;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FileServiceImpl implements FileService {

	@Autowired
	private SessionFactory sessionFactory;

	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public List<TeFile> list(Integer userId) {
		Criteria criteria = getCurrentSession().createCriteria(TeFile.class);
		criteria.add(Restrictions.eq("userId", userId));
		return criteria.list();
	}

	@Override
	public void create(TeFile file) {
		getCurrentSession().save(file);
	}

	@Override
	public TeFile get(Long id) {
		TeFile file = (TeFile) getCurrentSession().get(
				TeFile.class, id);
		return file;
	}

	@Override
	public void delete(TeFile file) {
		getCurrentSession().delete(file);

	}

	@Override
	public boolean hasFiles(Integer userId) {
		Criteria criteria = getCurrentSession().createCriteria(TeFile.class);
		criteria.add(Restrictions.eq("userId", userId));
		Integer totalResult = ((Number)criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
		return totalResult > 0;
	}

}
