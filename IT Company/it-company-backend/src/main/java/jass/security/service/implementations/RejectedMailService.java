package jass.security.service.implementations;

import jass.security.model.RejectedMail;
import jass.security.repository.IRejectedMailRespository;
import jass.security.service.interfaces.IRejectedMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Primary
public class RejectedMailService implements IRejectedMailService {
    private final IRejectedMailRespository rejectedMailRespository;

    @Autowired
    public RejectedMailService(IRejectedMailRespository rejectedMailRespository) {
        this.rejectedMailRespository = rejectedMailRespository;
    }

    @Override
    public List<RejectedMail> findAll() {
        return null;
    }

    @Override
    public RejectedMail findById(UUID id) {
        return null;
    }

    @Override
    public RejectedMail save(RejectedMail entity) {
        return rejectedMailRespository.save(entity);
    }

    @Override
    public void delete(UUID id) {
        rejectedMailRespository.deleteById(id);
    }

    @Override
    public boolean isMailRejected(String email) {
        var rejectedMail = rejectedMailRespository.findByEmail(email);
        if(rejectedMail == null) {
            return false;
        } else if(rejectedMail.getBlockedUntil().before(new Date())) {
            delete(rejectedMail.getId());
            return false;
        } else {
            return true;
        }
    }
}
