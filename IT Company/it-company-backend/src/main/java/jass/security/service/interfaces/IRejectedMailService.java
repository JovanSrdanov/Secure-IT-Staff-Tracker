package jass.security.service.interfaces;

import jass.security.model.RejectedMail;
import jass.security.service.implementations.ICrudService;

public interface IRejectedMailService extends ICrudService<RejectedMail> {
    boolean isMailRejected(String email);
}
