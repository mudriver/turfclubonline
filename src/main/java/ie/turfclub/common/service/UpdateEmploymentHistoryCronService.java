package ie.turfclub.common.service;

import ie.turfclub.model.stableStaff.TeEmployentHistory;
import ie.turfclub.service.trainer.TrainersService;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UpdateEmploymentHistoryCronService {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private TrainersService trainerService;
	
	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
	
	private TeEmployentHistory copyProperties(TeEmployentHistory src, TeEmployentHistory dest) {
		
		if(dest.getTeEmployees() == null) {
			dest.setTeEmployees(src.getTeEmployees());
			dest.setTeTrainers(src.getTeTrainers());
			dest.setEhDateFrom(src.getEhDateFrom());
			dest.setEhDateTo(src.getEhDateTo());
			dest.setEhHoursWorked(src.getEhHoursWorked());
			dest.setEhEmploymentCategory(src.getEhEmploymentCategory());
			dest.setEhYear(src.getEhYear());
			dest.setEhEarnings(src.getEhEarnings());
			dest.setEhTempCategory(src.getEhTempCategory());
			dest.setEhPpsNumber(src.getEhPpsNumber());
			dest.setEhVerified(src.isEhVerified());
			dest.setEmployeeLastUpdated(new Date());
		}
		return dest;
	}
	
	@Scheduled(cron="0 17 18 * * ?")
	public void updateEmploymentHistoryRecord() throws IllegalAccessException, InvocationTargetException {
		
		//Handle year record
		int year = Integer.parseInt(trainerService.getYearForTrainerEmployeeOnline())-1;
		Date startDate = new DateTime(year, 01, 01, 0, 0).toDate();
		Date endDate = new DateTime(year, 12, 31, 0, 0).toDate();
		Criteria criteria = getCurrentSession().createCriteria(TeEmployentHistory.class);
		criteria.add(Restrictions.between("ehDateFrom", startDate, endDate));
		criteria.add(Restrictions.isNull("ehDateTo"));
		List<TeEmployentHistory> records = criteria.list();
		
		if(records != null && records.size() > 0) {
			for (TeEmployentHistory teEmployentHistory : records) {
				
				teEmployentHistory.setEhDateTo(endDate);
				teEmployentHistory.setEmployeeLastUpdated(new Date());
				getCurrentSession().saveOrUpdate(teEmployentHistory);
				
				startDate = new DateTime(2015, 01, 01, 0, 0).toDate();
				endDate = new DateTime(2015, 12, 31, 0, 0).toDate();
				
				
				Criteria criteria1 = getCurrentSession().createCriteria(TeEmployentHistory.class);
				criteria1.add(Restrictions.between("ehDateFrom", startDate, endDate));
				criteria1.add(Restrictions.eq("teEmployees.employeesEmployeeId", teEmployentHistory.getTeEmployees().getEmployeesEmployeeId()));
				criteria1.add(Restrictions.eq("teTrainers.trainerId", teEmployentHistory.getTeTrainers().getTrainerId()));
				List<TeEmployentHistory> rec2015 = criteria1.list();
				
				TeEmployentHistory newRecord = (rec2015 != null && rec2015.size() > 0) ? rec2015.get(0) : new TeEmployentHistory();
				copyProperties(teEmployentHistory, newRecord);
				newRecord.setEhDateFrom(startDate);
				newRecord.setEhDateTo(endDate);
				getCurrentSession().saveOrUpdate(newRecord);
				
				startDate = new DateTime(2016, 01, 01, 0, 0).toDate();
				endDate = new DateTime(2016, 12, 31, 0, 0).toDate();
				Criteria criteria2 = getCurrentSession().createCriteria(TeEmployentHistory.class);
				criteria2.add(Restrictions.between("ehDateFrom", startDate, endDate));
				criteria2.add(Restrictions.eq("teEmployees.employeesEmployeeId", teEmployentHistory.getTeEmployees().getEmployeesEmployeeId()));
				criteria2.add(Restrictions.eq("teTrainers.trainerId", teEmployentHistory.getTeTrainers().getTrainerId()));
				List<TeEmployentHistory> rec2016 = criteria2.list();
				
				TeEmployentHistory newRecord1 = (rec2016 != null && rec2016.size() > 0) ? rec2016.get(0) : new TeEmployentHistory();
				copyProperties(teEmployentHistory, newRecord1);
				newRecord1.setEhDateFrom(startDate);
				newRecord1.setEhDateTo(endDate);
				getCurrentSession().saveOrUpdate(newRecord1);
				
				TeEmployentHistory newRecord2 = new TeEmployentHistory();
				copyProperties(teEmployentHistory, newRecord2);
				startDate = new DateTime(2017, 01, 01, 0, 0).toDate();
				newRecord2.setEhDateFrom(startDate);
				newRecord2.setEhDateTo(null);
				getCurrentSession().saveOrUpdate(newRecord2);
			}
		}
		
		//Handle 2015 year record
		startDate = new DateTime(2015, 01, 01, 0, 0).toDate();
		endDate = new DateTime(2015, 12, 31, 0, 0).toDate();
		Criteria criteria3 = getCurrentSession().createCriteria(TeEmployentHistory.class);
		criteria3.add(Restrictions.between("ehDateFrom", startDate, endDate));
		criteria3.add(Restrictions.isNull("ehDateTo"));
		records = criteria3.list();
		
		if(records != null && records.size() > 0) {
			for (TeEmployentHistory teEmployentHistory : records) {
				
				teEmployentHistory.setEhDateTo(endDate);
				teEmployentHistory.setEmployeeLastUpdated(new Date());
				getCurrentSession().saveOrUpdate(teEmployentHistory);
				
				startDate = new DateTime(2016, 01, 01, 0, 0).toDate();
				endDate = new DateTime(2016, 12, 31, 0, 0).toDate();
				Criteria criteria4 = getCurrentSession().createCriteria(TeEmployentHistory.class);
				criteria4.add(Restrictions.between("ehDateFrom", startDate, endDate));
				criteria4.add(Restrictions.eq("teEmployees.employeesEmployeeId", teEmployentHistory.getTeEmployees().getEmployeesEmployeeId()));
				criteria4.add(Restrictions.eq("teTrainers.trainerId", teEmployentHistory.getTeTrainers().getTrainerId()));
				List<TeEmployentHistory> rec2016 = criteria4.list();
				
				TeEmployentHistory newRecord1 = (rec2016 != null && rec2016.size() > 0) ? rec2016.get(0) : new TeEmployentHistory();
				copyProperties(teEmployentHistory, newRecord1);
				newRecord1.setEhDateFrom(startDate);
				newRecord1.setEhDateTo(endDate);
				getCurrentSession().saveOrUpdate(newRecord1);
				
				TeEmployentHistory newRecord2 = new TeEmployentHistory();
				copyProperties(teEmployentHistory, newRecord2);
				startDate = new DateTime(2017, 01, 01, 0, 0).toDate();
				newRecord2.setEhDateFrom(startDate);
				newRecord2.setEhDateTo(null);
				getCurrentSession().saveOrUpdate(newRecord2);
			}
		}
		
		Calendar prevYear = Calendar.getInstance();
	    prevYear.add(Calendar.YEAR, -1);
	    int preYear =  prevYear.get(Calendar.YEAR);
		    
		//Handle 2016 year record
		startDate = new DateTime(preYear, 01, 01, 0, 0).toDate();
		endDate = new DateTime(preYear, 12, 31, 0, 0).toDate();
		Criteria criteria5 = getCurrentSession().createCriteria(TeEmployentHistory.class);
		criteria5.add(Restrictions.between("ehDateFrom", startDate, endDate));
		criteria5.add(Restrictions.isNull("ehDateTo"));
		records = criteria5.list();
		
		if(records != null && records.size() > 0) {
			for (TeEmployentHistory teEmployentHistory : records) {
				
				teEmployentHistory.setEhDateTo(endDate);
				teEmployentHistory.setEmployeeLastUpdated(new Date());
				getCurrentSession().saveOrUpdate(teEmployentHistory);
				
				TeEmployentHistory newRecord2 = new TeEmployentHistory();
				copyProperties(teEmployentHistory, newRecord2);
				startDate = new DateTime(2017, 01, 01, 0, 0).toDate();
				newRecord2.setEhDateFrom(startDate);
				newRecord2.setEhDateTo(null);
				getCurrentSession().saveOrUpdate(newRecord2);
			}
		}
	}
}
