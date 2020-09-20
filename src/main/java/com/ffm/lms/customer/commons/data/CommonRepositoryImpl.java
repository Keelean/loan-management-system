package com.ffm.lms.customer.commons.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.beanutils.BeanUtilsBean2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import com.ffm.lms.customer.commons.domain.BaseEntity;

public class CommonRepositoryImpl<T extends SearchableEntity> extends SimpleJpaRepository<T, Long>
		implements CommonRepository<T> {

	private  Logger logger = LoggerFactory.getLogger(this.getClass());
	private final EntityManager entityManager;
	private JpaEntityInformation<T, ?> info;


	public CommonRepositoryImpl(JpaEntityInformation<T, Long> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);

		this.entityManager = entityManager;
		info = entityInformation;
	}



	@Override
	public Page<T> findUsingPattern(String pattern, Pageable details) {
		String lpattern = pattern.toLowerCase();
		CriteriaBuilder critBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> primaryQuery =  critBuilder.createQuery(info.getJavaType());
		Root<T> rootType = primaryQuery.from(info.getJavaType());
		Predicate[] predicates = null;
		try {
			predicates = new Predicate[getFields().size()];
			int cnt = 0;
			for (String field : getFields()) {
				Predicate predicate = critBuilder.like(critBuilder.lower(rootType.get(field)), "%" + lpattern + "%");
				predicates[cnt] = predicate;
				cnt++;
			}
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException
				| IllegalArgumentException | InvocationTargetException e) {
			return new PageImpl<>(new ArrayList<>());
		}

		CriteriaQuery<T> baseQuery = null;
		CriteriaQuery<Long> countQueryBuilder = critBuilder.createQuery(Long.class);
		CriteriaQuery<Long> countQuery = null;

		baseQuery = primaryQuery.select(rootType);
		countQuery = countQueryBuilder.select(critBuilder.count(countQueryBuilder.from(info.getJavaType())));

		ArrayList<Predicate> orPredicates = new ArrayList<>();
		ArrayList<Predicate> andPredicates = new ArrayList<>();
		if (predicates.length > 0) {
			orPredicates.add(critBuilder.or(predicates));
		}

		// check tenant

		List<Predicate> allPredicates = Stream.concat(orPredicates.stream(), andPredicates.stream())
				.collect(Collectors.toList());
		Predicate total = critBuilder.and(allPredicates.toArray(new Predicate[] {}));

		TypedQuery<T> query = entityManager.createQuery(baseQuery.where(total));

		Long count = entityManager.createQuery(countQuery.where(total)).getSingleResult();

		query.setFirstResult((int)details.getOffset());
		query.setMaxResults(details.getPageSize());
		List<T> list = query.getResultList();

		return new PageImpl<>(list, details, count);
	}


	private List<String> getFields() throws NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
		Class<T> type = info.getJavaType();
		Constructor<T> constructor = type.getConstructor();
		SearchableEntity en = constructor.newInstance();
		return en.getDefaultSearchFields();

	}


	@Override
	public void detach(T entity) {
		entityManager.detach(entity);
	}

	private BeanUtilsBean2 versionValidator = new BeanUtilsBean2();

	@Override
	public boolean isLockOk(Object a, Object b) {

		try {
			return versionValidator.getSimpleProperty(a,"version").equals(versionValidator.getSimpleProperty(b,"version"));
		} catch (Exception e) {
			logger.warn("checking concurrency on unversioned entity, ignoring");
		}
		return true;
	}

	@Override
	public List<T> findAll() {
		CriteriaBuilder critBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> primaryQuery =  critBuilder.createQuery(getDomainClass());
		Root<T> rootType = primaryQuery.from(info.getJavaType());

		CriteriaQuery<T> baseQuery = primaryQuery.select(rootType);


		TypedQuery<T> query = entityManager.createQuery(baseQuery);
		return query.getResultList();
	}

	@Override
	public void delete(BaseEntity entity) {
		entity.setDelFlag(DeleteFlag.DELETED);
		entityManager.merge(entity);
	}


	@Override
	public boolean softDelete(BaseEntity entity) {
		boolean deleted = false;
		entity.setDelFlag(DeleteFlag.DELETED);
		entityManager.merge(entity);
		deleted = true;
		return deleted;
	}
}
