package br.com.oi.sgis.repository.impl;

import br.com.oi.sgis.dto.UserExtractionDTO;
import br.com.oi.sgis.dto.UserExtractionReportDTO;
import br.com.oi.sgis.repository.UserRegisterRepositoryCustom;
import br.com.oi.sgis.util.MessageUtils;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRegisterRepositoryCustomImpl implements UserRegisterRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private StringBuilder sql;
    private UserExtractionDTO dto;

    @Override
    public List<UserExtractionReportDTO> findForExtraction(UserExtractionDTO userExtractionDTO) {
        dto = userExtractionDTO;
        sql = new StringBuilder("select new br.com.oi.sgis.dto.UserExtractionReportDTO(a.ndsLogin, b.id, b.technicianName, a.active, d.id, c.tradeName, a.department.id, b.techPhoneBase, b.email ) from UserRegister a " +
        " inner  join a.techinician b inner join b.cgcCpfCompany.company c left join a.levels d " +
                " where 1 = 1 ");
        addCriterias();
        try {
            TypedQuery<UserExtractionReportDTO> query = entityManager.createQuery(sql.toString(), UserExtractionReportDTO.class);
            return query.getResultList();
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.ERROR_QUERY_REPORT.getDescription());
        }
    }

    private void addCriterias() {
        addDepartments();
        addCompanies();
        addUfs();
    }

    private void addDepartments() {
        if(dto.isAllDepartments())
            sql.append(" and a.department.id in (select distinct ts.departmentCode from TechnicalStaff ts where ts.departmentCode is not null) " );
        else {
            if(!dto.getDepartments().isEmpty())
                sql.append(" and a.department.id in ( ")
                        .append(dto.getDepartments().stream().collect(Collectors.joining ("','", "'", "'")))
                    .append(" ) ");
        }
    }

    private void addCompanies() {
        if(dto.isAllCompanies())
            sql.append(" and c.id in (select distinct ts.cgcCpfCompany from TechnicalStaff ts where ts.cgcCpfCompany is not null) " );
        else {
            if(!dto.getCompanies().isEmpty())
                sql.append(" and c.id in ( ")
                        .append(dto.getCompanies().stream().collect(Collectors.joining ("','", "'", "'")))
                        .append(" ) ");
        }
    }
    private void addUfs() {
        if(!dto.getUfs().isEmpty())
            sql.append(" and substring(a.department.id, 1, 2)  in ( ")
                    .append(dto.getUfs().stream().collect(Collectors.joining ("','", "'", "'")))
                    .append(" ) ");
    }


}
