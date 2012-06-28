package edu.harvard.med.hks.dao.hibernate;

import org.springframework.stereotype.Repository;

import edu.harvard.med.hks.dao.HksGameDao;
import edu.harvard.med.hks.model.Game;

@Repository
public class HksGameDaoImpl extends GenericDaoImpl<Game> implements HksGameDao {

}